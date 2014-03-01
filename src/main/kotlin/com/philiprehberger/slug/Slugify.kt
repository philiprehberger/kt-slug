package com.philiprehberger.slug

import java.text.Normalizer

/**
 * Configuration for slug generation.
 *
 * @property separator the character used to separate words (default: `"-"`)
 * @property maxLength the maximum length of the generated slug; `0` means no limit (default: `0`)
 * @property lowercase whether to convert the slug to lowercase (default: `true`)
 * @property transliterate whether to transliterate Unicode characters to ASCII (default: `true`)
 * @property customReplacements a map of custom string replacements applied before slugification
 */
class SlugConfig {
    var separator: String = "-"
    var maxLength: Int = 0
    var lowercase: Boolean = true
    var transliterate: Boolean = true
    var customReplacements: Map<String, String> = emptyMap()
}

/**
 * Generates a URL-friendly slug from the given [input] string.
 *
 * The slug generation process:
 * 1. Applies custom replacements
 * 2. Optionally transliterates Unicode to ASCII via NFD normalization
 * 3. Optionally converts to lowercase
 * 4. Replaces non-alphanumeric characters with the separator
 * 5. Collapses consecutive separators
 * 6. Trims leading/trailing separators
 * 7. Optionally truncates to [SlugConfig.maxLength]
 *
 * ```kotlin
 * slug("Hello, World!")           // "hello-world"
 * slug("Cafe\u0301 Latte\u0301") // "cafe-latte"
 * slug("foo bar") { separator = "_" } // "foo_bar"
 * ```
 *
 * @param input the string to slugify
 * @param config optional configuration block
 * @return the generated slug, or an empty string if the input produces no valid characters
 */
fun slug(input: String, config: SlugConfig.() -> Unit = {}): String {
    val cfg = SlugConfig().apply(config)

    if (input.isBlank()) return ""

    var text = input

    // Apply custom replacements
    for ((from, to) in cfg.customReplacements) {
        text = text.replace(from, to)
    }

    // Transliterate: normalize to NFD and strip combining marks
    if (cfg.transliterate) {
        text = Normalizer.normalize(text, Normalizer.Form.NFD)
        text = text.replace(Regex("[\\p{InCombiningDiacriticalMarks}]"), "")
    }

    // Lowercase
    if (cfg.lowercase) {
        text = text.lowercase()
    }

    // Replace non-alphanumeric characters with the separator
    val sep = Regex.escape(cfg.separator)
    text = text.replace(Regex("[^a-zA-Z0-9]+"), cfg.separator)

    // Collapse consecutive separators
    if (cfg.separator.isNotEmpty()) {
        text = text.replace(Regex("$sep{2,}"), cfg.separator)
    }

    // Trim leading/trailing separators
    text = text.trim(cfg.separator[0])

    // Truncate to maxLength (don't cut in the middle of a separator)
    if (cfg.maxLength > 0 && text.length > cfg.maxLength) {
        text = text.substring(0, cfg.maxLength).trimEnd(cfg.separator[0])
    }

    return text
}
