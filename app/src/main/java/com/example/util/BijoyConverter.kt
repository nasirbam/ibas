package com.example.util

/**
 * Converts Unicode Bengali text into Bijoy ANSI (SutonnyMJ) encoding so users can paste
 * official letters and pay bills directly into Microsoft Word using the SutonnyMJ font.
 */
object BijoyConverter {

    private val vowelMap = mapOf(
        'অ' to "A", 'আ' to "Av", 'ই' to "B", 'ঈ' to "C", 'উ' to "D", 'ঊ' to "E",
        'ঋ' to "F", 'এ' to "G", 'ঐ' to "H", 'ও' to "I", 'ঔ' to "J"
    )

    private val consonantMap = mapOf(
        'ক' to "K", 'খ' to "L", 'গ' to "M", 'ঘ' to "N", 'ঙ' to "O",
        'চ' to "P", 'ছ' to "Q", 'জ' to "R", 'ঝ' to "S", 'ঞ' to "T",
        'ট' to "U", 'ঠ' to "V", 'ড' to "W", 'ঢ' to "X", 'ণ' to "Y",
        'ত' to "Z", 'থ' to "_", 'দ' to "`", 'ধ' to "a", 'ন' to "b",
        'প' to "c", 'ফ' to "d", 'ব' to "e", 'ভ' to "f", 'ম' to "g",
        'য' to "h", 'র' to "i", 'ল' to "j", 'শ' to "k", 'ষ' to "l",
        'স' to "m", 'হ' to "n", 'ড়' to "o", 'ঢ়' to "p", 'য়' to "q",
        'ৎ' to "r", 'ং' to "s", 'ঃ' to "t", 'ঁ' to "u"
    )

    private val digitMap = mapOf(
        '০' to '0', '১' to '1', '২' to '2', '৩' to '3', '৪' to '4',
        '৫' to '5', '৬' to '6', '৭' to '7', '৮' to '8', '৯' to '9',
        '।' to '|'
    )

    private val postKarMap = mapOf(
        'া' to "v", 'ী' to "x", 'ু' to "z", 'ূ' to "~", 'ৃ' to "„"
    )

    private val preKarMap = mapOf(
        'ি' to "w", 'ে' to "†", 'ৈ' to "‰"
    )

    private val juktakkhorMap = mapOf(
        "ক্ষ" to "¶", "ক্ত" to "³", "ক্র" to "µ", "ক্ল" to "K¬", "ক্স" to "·",
        "গু" to "¸", "গ্র" to "MÖ", "গ্ল" to "M¬", "ঙ্ক" to "•", "ঙ্গ" to "½",
        "চ্চ" to "”P", "চ্ছ" to "”Q", "জ্জ" to "¾", "জ্ঞ" to "Á", "ঞ্চ" to "Â",
        "ঞ্জ" to "Ä", "ট্ট" to "Æ", "ড্ড" to "Ç", "ণ্ট" to "È", "ণ্ঠ" to "É",
        "ণ্ড" to "Ê", "ত্ত" to "Ë", "ত্থ" to "Ì", "ত্র" to "Î", "ত্ন" to "Zœ",
        "ত্ব" to "Z¡", "দ্ম" to "Ù", "দ্ব" to "Ø", "দ্ধ" to "×", "দ্র" to "`ª",
        "ধ্ব" to "a¡", "ন্ত" to "š—", "ন্থ" to "š’", "ন্দ" to "›`", "ন্ধ" to "Ü",
        "ন্ন" to "bœ", "ন্ব" to "š^", "ন্স" to "Ý", "প্ট" to "Þ", "প্ত" to "ß",
        "প্প" to "à", "প্র" to "cÖ", "প্ল" to "c¬", "ব্জ" to "â", "ব্দ" to "ã",
        "ব্ধ" to "ä", "ব্র" to "eª", "ব্ল" to "e¬", "ভ্র" to "å", "ম্ন" to "gœ",
        "ম্প" to "¤ú", "ম্ফ" to "¤•", "ম্ব" to "¤^", "ম্ভ" to "¤¢", "ম্ম" to "¤§",
        "ম্র" to "gª", "ল্ক" to "é", "ল্গ" to "ê", "ল্প" to "í", "ল্ল" to "jø",
        "শ্চ" to "ð", "শ্ম" to "k¥", "শ্র" to "kÖ", "শ্ল" to "k¬", "ষ্ক" to "®‹",
        "ষ্ট" to "ó", "ষ্ঠ" to "ô", "ষ্ণ" to "ò", "ষ্প" to "®ú", "ষ্ম" to "®§",
        "স্ক" to "¯‹", "স্ত" to "¯—", "স্থ" to "¯’", "স্ন" to "mœ", "স্প" to "¯ú",
        "স্ফ" to "ù", "স্ব" to "¯^", "স্ম" to "¯§", "স্র" to "mª", "হ্ম" to "þ",
        "হ্র" to "nª", "হ্ল" to "n¬"
    )

    fun toSutonnyMJ(unicode: String): String {
        var text = unicode
            .replace("\u200C", "")
            .replace("\u200D", "")

        // Normalize split kar
        text = text
            .replace("ো", "ো")
            .replace("ৌ", "ৌ")

        val sb = StringBuilder()
        var i = 0
        while (i < text.length) {
            // Check 3-char juktakkhor
            if (i + 3 <= text.length) {
                val tri = text.substring(i, i + 3)
                val mapped = juktakkhorMap[tri]
                if (mapped != null) {
                    // Check if followed by pre-kar
                    if (i + 3 < text.length && preKarMap.containsKey(text[i + 3])) {
                        sb.append(preKarMap[text[i + 3]])
                        sb.append(mapped)
                        i += 4
                        continue
                    }
                    sb.append(mapped)
                    i += 3
                    continue
                }
            }

            val ch = text[i]
            // Check pre-kar after single consonant
            if (i + 1 < text.length && preKarMap.containsKey(text[i + 1]) && consonantMap.containsKey(ch)) {
                sb.append(preKarMap[text[i + 1]])
                sb.append(consonantMap[ch])
                i += 2
                continue
            }

            // Reph (র্ + consonant)
            if (i + 2 < text.length && ch == 'র' && text[i + 1] == '্') {
                val target = text[i + 2]
                val mappedTarget = consonantMap[target] ?: target.toString()
                if (i + 3 < text.length && preKarMap.containsKey(text[i + 3])) {
                    sb.append(preKarMap[text[i + 3]])
                    sb.append(mappedTarget)
                    sb.append("©")
                    i += 4
                    continue
                }
                sb.append(mappedTarget)
                sb.append("©")
                i += 3
                continue
            }

            // Ja-phala (্য) & Ra-phala (্র)
            if (i + 1 < text.length && ch == '্') {
                when (text[i + 1]) {
                    'য' -> { sb.append("¨"); i += 2; continue }
                    'র' -> { sb.append("ª"); i += 2; continue }
                    'ব' -> { sb.append("¡"); i += 2; continue }
                }
            }

            when {
                vowelMap.containsKey(ch) -> sb.append(vowelMap[ch])
                consonantMap.containsKey(ch) -> sb.append(consonantMap[ch])
                postKarMap.containsKey(ch) -> sb.append(postKarMap[ch])
                ch == 'ৗ' -> sb.append("Š")
                ch == '্' -> sb.append("&")
                digitMap.containsKey(ch) -> sb.append(digitMap[ch])
                else -> sb.append(ch)
            }
            i++
        }
        return sb.toString()
    }
}
