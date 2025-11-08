package com.example.promptdefensedemo

import android.os.Bundle
import android.os.SystemClock
import android.text.method.ScrollingMovementMethod
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = findViewById<EditText>(R.id.basePromptInput)
        val spinner = findViewById<Spinner>(R.id.strategySpinner)
        val seekBar = findViewById<SeekBar>(R.id.countSeekBar)
        val countLabel = findViewById<TextView>(R.id.countLabel)
        val button = findViewById<Button>(R.id.generateButton)
        val metricsText = findViewById<TextView>(R.id.metricsTextView)
        val resultsText = findViewById<TextView>(R.id.resultsTextView)
        resultsText.movementMethod = ScrollingMovementMethod()

        ArrayAdapter.createFromResource(
            this,
            R.array.strategy_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        seekBar.max = 100
        seekBar.progress = 20
        countLabel.text = "Variants: 20"
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                countLabel.text = "Variants: $progress"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        button.setOnClickListener {
            val base = input.text.toString()
            val count = seekBar.progress.coerceAtLeast(1)
            val strategyName = spinner.selectedItem.toString()
            val strategy = when (strategyName) {
                "Synonym" -> PromptGenerator.Strategy.SYNONYM
                "Obfuscation" -> PromptGenerator.Strategy.OBFUSCATION
                "Casing" -> PromptGenerator.Strategy.CASING
                "Whitespace" -> PromptGenerator.Strategy.WHITESPACE
                "Encoding" -> PromptGenerator.Strategy.ENCODING
                "Mixed" -> PromptGenerator.Strategy.MIXED
                else -> PromptGenerator.Strategy.SYNONYM
            }

            val start = SystemClock.elapsedRealtime()
            val safeBase = PromptGenerator.sanitize(base)
            val variants = PromptGenerator.generateVariants(safeBase, count, strategy)
            val elapsedMs = SystemClock.elapsedRealtime() - start

            val unique = variants.toSet().size
            val tokens = variants.sumOf { PromptGenerator.approxTokenCount(it) }
            metricsText.text = "Generated: $unique/$count • Total tokens≈$tokens • Time: ${elapsedMs}ms"

            resultsText.text = variants.joinToString(separator = "\n\n—\n\n")
        }
    }
}

