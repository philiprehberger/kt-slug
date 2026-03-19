package com.philiprehberger.slug

/**
 * Generates a unique slug by appending an incrementing suffix if the base slug already exists.
 *
 * Calls [exists] to check whether a candidate slug is taken. If the base slug is available,
 * it is returned as-is. Otherwise, suffixes `-1`, `-2`, etc. are appended until a unique
 * slug is found.
 *
 * ```kotlin
 * val slug = uniqueSlug("hello-world") { candidate ->
 *     database.slugExists(candidate)
 * }
 * // returns "hello-world", "hello-world-1", "hello-world-2", etc.
 * ```
 *
 * @param base the base slug to start from
 * @param config optional slug configuration applied to [base]
 * @param exists a suspend function that returns `true` if the candidate slug is already taken
 * @return a unique slug string
 */
public suspend fun uniqueSlug(
    base: String,
    config: SlugConfig.() -> Unit = {},
    exists: suspend (String) -> Boolean,
): String {
    val baseSlug = slug(base, config)
    if (!exists(baseSlug)) return baseSlug

    var counter = 1
    while (true) {
        val candidate = "$baseSlug-$counter"
        if (!exists(candidate)) return candidate
        counter++
    }
}
