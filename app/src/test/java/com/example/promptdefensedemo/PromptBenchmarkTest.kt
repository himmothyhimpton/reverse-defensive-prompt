package com.example.promptdefensedemo

import org.junit.Test
import java.io.File
import kotlin.system.measureTimeMillis

class PromptBenchmarkTest {
    @Test
    fun generateCsvReport() {
        val base = "Explain quantum superposition to beginners."
        val counts = listOf(100, 500, 1000)
        val strategies = PromptGenerator.Strategy.values().toList()

        val projectDir = File(System.getProperty("user.dir"))
        val outDir = projectDir.resolve("app").resolve("build").resolve("reports")
        outDir.mkdirs()
        val outFile = outDir.resolve("prompt-benchmark.csv")

        outFile.printWriter().use { w ->
            w.println("strategy,count,unique,tokens,elapsed_ms,avg_ms_per_variant")
            for (strategy in strategies) {
                for (count in counts) {
                    val safeBase = PromptGenerator.sanitize(base)
                    var variants: List<String> = emptyList()
                    val elapsed = measureTimeMillis {
                        variants = PromptGenerator.generateVariants(safeBase, count, strategy)
                    }
                    val unique = variants.toSet().size
                    val tokens = variants.sumOf { PromptGenerator.approxTokenCount(it) }
                    val avg = elapsed.toDouble() / count
                    w.println("${strategy.name},${count},${unique},${tokens},${elapsed},${"%.3f".format(avg)}")
                }
            }
        }
    }
}

