package com.example.promptdefensedemo

import java.text.Normalizer

object TextNormalizer {
    private val zeroWidthRegex = Regex("[\u200B\u200C\u200D\uFEFF]")
    private val controlRegex = Regex("[\p{Cc}]")

    // Minimal confusable mapping for demonstration; extend safely if needed
    private val confusableMap: Map<Char, Char> = mapOf(
        'Ａ' to 'A', 'Ｂ' to 'B', 'Ｃ' to 'C', 'Ｄ' to 'D', 'Ｅ' to 'E', 'Ｆ' to 'F', 'Ｇ' to 'G', 'Ｈ' to 'H', 'Ｉ' to 'I', 'Ｊ' to 'J', 'Ｋ' to 'K', 'Ｌ' to 'L', 'Ｍ' to 'M', 'Ｎ' to 'N', 'Ｏ' to 'O', 'Ｐ' to 'P', 'Ｑ' to 'Q', 'Ｒ' to 'R', 'Ｓ' to 'S', 'Ｔ' to 'T', 'Ｕ' to 'U', 'Ｖ' to 'V', 'Ｗ' to 'W', 'Ｘ' to 'X', 'Ｙ' to 'Y', 'Ｚ' to 'Z',
        'ａ' to 'a', 'ｂ' to 'b', 'ｃ' to 'c', 'ｄ' to 'd', 'ｅ' to 'e', 'ｆ' to 'f', 'ｇ' to 'g', 'ｈ' to 'h', 'ｉ' to 'i', 'ｊ' to 'j', 'ｋ' to 'k', 'ｌ' to 'l', 'ｍ' to 'm', 'ｎ' to 'n', 'ｏ' to 'o', 'ｐ' to 'p', 'ｑ' to 'q', 'ｒ' to 'r', 'ｓ' to 's', 'ｔ' to 't', 'ｕ' to 'u', 'ｖ' to 'v', 'ｗ' to 'w', 'ｘ' to 'x', 'ｙ' to 'y', 'ｚ' to 'z',
        '０' to '0', '１' to '1', '２' to '2', '３' to '3', '４' to '4', '５' to '5', '６' to '6', '７' to '7', '８' to '8', '９' to '9',
        'ℓ' to 'l', 'ı' to 'i', 'İ' to 'I'
    )

    fun normalize(input: String): String {
        var s = Normalizer.normalize(input, Normalizer.Form.NFKC)
        s = zeroWidthRegex.replace(s, "")
        s = controlRegex.replace(s, "")
        // Map confusables to ASCII approximations
        s = buildString(s.length) {
            s.forEach { ch -> append(confusableMap[ch] ?: ch) }
        }
        return s
    }

    fun collapseWhitespace(input: String): String {
        return input.trim().replace(Regex("\\s+"), " ")
    }
}

