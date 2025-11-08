package com.example.promptdefensedemo

object PromptGenerator {
    enum class Strategy { SYNONYM, OBFUSCATION, CASING, WHITESPACE, ENCODING, MIXED }

    private val flagged = setOf(
        "harm", "illegal", "violence", "exploit", "breach",
        "malware", "bypass", "hack", "attack", "jailbreak"
    )

    fun sanitize(input: String): String {
        var s = TextNormalizer.normalize(input)
        s = TextNormalizer.collapseWhitespace(s)
        flagged.forEach { w ->
            val regex = Regex("(?i)\\b${Regex.escape(w)}\\b")
            s = s.replace(regex, "[redacted]")
        }
        return s
    }

    private val synonymDict = mapOf(
        "blue" to listOf("azure", "cerulean", "navy", "indigo"),
        "refuse" to listOf("decline", "withhold", "refrain"),
        "describe" to listOf("depict", "portray", "outline"),
        "explain" to listOf("clarify", "illuminate", "expound")
    )

    fun generateVariants(base: String, count: Int, strategy: Strategy): List<String> {
        val variants = mutableListOf<String>()
        val seed = base.trim().ifEmpty { "Please describe a topic without using a specific word." }
        val rnd = java.util.Random(seed.hashCode().toLong())

        fun mutateOnce(text: String, strat: Strategy): String = when (strat) {
            Strategy.SYNONYM -> synonymSwap(text, rnd)
            Strategy.OBFUSCATION -> obfuscate(text, rnd)
            Strategy.CASING -> casePlay(text, rnd)
            Strategy.WHITESPACE -> whitespacePlay(text, rnd)
            Strategy.ENCODING -> encodeLeet(text, rnd)
            Strategy.MIXED -> encodeLeet(casePlay(obfuscate(synonymSwap(text, rnd), rnd), rnd), rnd)
        }

        repeat(count) {
            variants += mutateOnce(seed + " [v${it + 1}]", strategy)
        }
        return variants
    }

    private fun synonymSwap(text: String, rnd: java.util.Random): String {
        val words = text.split(Regex("\\s+")).toMutableList()
        for (i in words.indices) {
            val w = words[i].trim(' ', ',', '.', ';', ':', '!', '?')
            val options = synonymDict[w.lowercase()]
            if (options != null && options.isNotEmpty() && rnd.nextDouble() < 0.6) {
                val syn = options[rnd.nextInt(options.size)]
                words[i] = words[i].replace(Regex("(?i)${Regex.escape(w)}"), syn)
            }
        }
        return words.joinToString(" ")
    }

    private fun obfuscate(text: String, rnd: java.util.Random): String {
        val insertions = listOf("(kindly)", "[context only]", "{neutral}", "<placeholder>", "(meta)")
        val tokens = text.split(Regex("\\s+")).toMutableList()
        val inserts = rnd.nextInt(3) + 1
        repeat(inserts) {
            val pos = rnd.nextInt(tokens.size.coerceAtLeast(1))
            tokens.add(pos, insertions[rnd.nextInt(insertions.size)])
        }
        return tokens.joinToString(" ")
    }

    private fun casePlay(text: String, rnd: java.util.Random): String {
        val sb = StringBuilder(text.length)
        text.forEach { ch ->
            if (ch.isLetter()) {
                sb.append(if (rnd.nextBoolean()) ch.uppercaseChar() else ch.lowercaseChar())
            } else sb.append(ch)
        }
        return sb.toString()
    }

    private fun whitespacePlay(text: String, rnd: java.util.Random): String {
        val parts = text.split(Regex("(\\s+)"))
        val sb = StringBuilder()
        parts.forEachIndexed { idx, p ->
            sb.append(p)
            if (idx < parts.size - 1) {
                val ws = when (rnd.nextInt(3)) {
                    0 -> " "
                    1 -> "  "
                    else -> "\t"
                }
                sb.append(ws)
            }
        }
        return sb.toString()
    }

    private fun encodeLeet(text: String, rnd: java.util.Random): String {
        val map = mapOf('a' to '@', 'e' to '3', 'i' to '1', 'o' to '0', 's' to '$')
        val sb = StringBuilder(text.length)
        text.forEach { ch ->
            val m = map[ch.lowercaseChar()]
            if (m != null && rnd.nextDouble() < 0.3) {
                sb.append(m)
            } else sb.append(ch)
        }
        return sb.toString()
    }

    fun approxTokenCount(text: String): Int {
        return text.split(Regex("\\s+")).filter { it.isNotBlank() }.size
    }
}
