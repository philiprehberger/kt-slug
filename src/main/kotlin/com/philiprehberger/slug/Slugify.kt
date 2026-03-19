package com.philiprehberger.slug

import java.text.Normalizer

/**
 * Unicode normalization form used during slug generation.
 */
public enum class NormalizationForm {
    /** Canonical Decomposition, followed by Canonical Composition. */
    NFC,
    /** Canonical Decomposition. */
    NFD,
    /** Compatibility Decomposition, followed by Canonical Composition. */
    NFKC,
    /** Compatibility Decomposition. */
    NFKD;

    internal fun toJavaForm(): Normalizer.Form = when (this) {
        NFC -> Normalizer.Form.NFC
        NFD -> Normalizer.Form.NFD
        NFKC -> Normalizer.Form.NFKC
        NFKD -> Normalizer.Form.NFKD
    }
}

/**
 * Configuration for slug generation.
 *
 * @property separator the character used to separate words (default: `"-"`)
 * @property maxLength the maximum length of the generated slug; `0` means no limit (default: `0`)
 * @property lowercase whether to convert the slug to lowercase (default: `true`)
 * @property transliterate whether to transliterate Unicode characters to ASCII (default: `true`)
 * @property normalizationForm the Unicode normalization form to use (default: [NormalizationForm.NFD])
 * @property customReplacements a map of custom string replacements applied before slugification
 */
public class SlugConfig {
    public var separator: String = "-"
    public var maxLength: Int = 0
    public var lowercase: Boolean = true
    public var transliterate: Boolean = true
    public var normalizationForm: NormalizationForm = NormalizationForm.NFD
    public var customReplacements: Map<String, String> = emptyMap()
}

/**
 * Generates a URL-friendly slug from the given [input] string.
 *
 * The slug generation process:
 * 1. Applies custom replacements
 * 2. Applies Unicode normalization using the configured [SlugConfig.normalizationForm]
 * 3. Optionally strips combining marks when [SlugConfig.transliterate] is enabled
 * 4. Optionally converts to lowercase
 * 5. Replaces non-alphanumeric characters with the separator
 * 6. Collapses consecutive separators
 * 7. Trims leading/trailing separators
 * 8. Optionally truncates to [SlugConfig.maxLength]
 *
 * ```kotlin
 * slug("Hello, World!")                        // "hello-world"
 * slug("Caf\u00e9 Latt\u00e9")                // "cafe-latte"
 * slug("foo bar") { separator = "_" }          // "foo_bar"
 * ```
 *
 * @param input the string to slugify
 * @param config optional configuration block
 * @return the generated slug, or an empty string if the input produces no valid characters
 */
public fun slug(input: String, config: SlugConfig.() -> Unit = {}): String {
    val cfg = SlugConfig().apply(config)

    if (input.isBlank()) return ""

    var text = input

    // Apply custom replacements
    for ((from, to) in cfg.customReplacements) {
        text = text.replace(from, to)
    }

    // Normalize Unicode using the configured form
    text = Normalizer.normalize(text, cfg.normalizationForm.toJavaForm())

    // Strip combining marks when transliterating
    if (cfg.transliterate) {
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
    if (cfg.separator.isNotEmpty()) {
        text = text.trim(cfg.separator[0])
    }

    // Truncate to maxLength (don't cut in the middle of a separator)
    if (cfg.maxLength > 0 && text.length > cfg.maxLength) {
        text = text.substring(0, cfg.maxLength).trimEnd(cfg.separator[0])
    }

    return text
}

/**
 * Generates slugs for a list of strings using the same configuration.
 *
 * ```kotlin
 * slugifyAll(listOf("Hello World", "Foo Bar"))
 * // ["hello-world", "foo-bar"]
 * ```
 *
 * @param inputs the list of strings to slugify
 * @param config optional configuration block applied to all inputs
 * @return a list of generated slugs
 */
public fun slugifyAll(inputs: List<String>, config: SlugConfig.() -> Unit = {}): List<String> {
    return inputs.map { slug(it, config) }
}

/**
 * Checks whether the given [value] is already a valid slug.
 *
 * A valid slug consists only of lowercase alphanumeric characters and the given [separator],
 * does not start or end with the separator, and contains no consecutive separators.
 *
 * ```kotlin
 * isValidSlug("hello-world")     // true
 * isValidSlug("Hello-World")     // false (uppercase)
 * isValidSlug("hello--world")    // false (consecutive separators)
 * isValidSlug("-hello-world")    // false (leading separator)
 * isValidSlug("hello_world", separator = "_") // true
 * ```
 *
 * @param value the string to validate
 * @param separator the separator character to allow (default: `"-"`)
 * @return `true` if the string is a valid slug
 */
public fun isValidSlug(value: String, separator: String = "-"): Boolean {
    if (value.isEmpty()) return false

    val sep = Regex.escape(separator)

    // Must contain only lowercase alphanumeric characters and the separator
    if (!value.matches(Regex("[a-z0-9$sep]+"))) return false

    // Must not start or end with the separator
    if (separator.isNotEmpty() && (value.startsWith(separator) || value.endsWith(separator))) return false

    // Must not contain consecutive separators
    if (separator.isNotEmpty() && value.contains(Regex("$sep{2,}"))) return false

    return true
}
