package com.philiprehberger.slug

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlugifyTest {

    @Test
    fun `basic ASCII string`() {
        assertEquals("hello-world", slug("Hello, World!"))
    }

    @Test
    fun `unicode accents are transliterated`() {
        assertEquals("cafe-latte", slug("Caf\u00e9 Latt\u00e9"))
    }

    @Test
    fun `german umlauts are stripped`() {
        assertEquals("uber-grun", slug("\u00dcber Gr\u00fcn"))
    }

    @Test
    fun `max length truncation`() {
        val result = slug("This is a very long title for a slug") { maxLength = 15 }
        assertEquals("this-is-a-very", result)
    }

    @Test
    fun `custom separator`() {
        assertEquals("hello_world", slug("Hello World") { separator = "_" })
    }

    @Test
    fun `custom separator with dot`() {
        assertEquals("hello.world", slug("Hello World") { separator = "." })
    }

    @Test
    fun `custom separator with tilde`() {
        assertEquals("hello~world", slug("Hello World") { separator = "~" })
    }

    @Test
    fun `custom replacements`() {
        val result = slug("C++ Programming") {
            customReplacements = mapOf("++" to "plus-plus")
        }
        assertEquals("cplus-plus-programming", result)
    }

    @Test
    fun `consecutive spaces collapse`() {
        assertEquals("a-b-c", slug("  a    b    c  "))
    }

    @Test
    fun `empty input returns empty string`() {
        assertEquals("", slug(""))
    }

    @Test
    fun `blank input returns empty string`() {
        assertEquals("", slug("   "))
    }

    @Test
    fun `special characters only returns empty string`() {
        assertEquals("", slug("!@#\$%^&*()"))
    }

    @Test
    fun `all special characters with spaces returns empty string`() {
        assertEquals("", slug(" !!! @@@ ### "))
    }

    @Test
    fun `numbers are preserved`() {
        assertEquals("version-2-0-1", slug("Version 2.0.1"))
    }

    @Test
    fun `no lowercase when disabled`() {
        assertEquals("Hello-World", slug("Hello World") { lowercase = false })
    }

    // --- Consecutive separator collapsing ---

    @Test
    fun `consecutive hyphens in input are collapsed`() {
        assertEquals("foo-bar", slug("foo---bar"))
    }

    @Test
    fun `consecutive separators from special chars are collapsed`() {
        assertEquals("a-b", slug("a!!!b"))
    }

    @Test
    fun `mixed separators and spaces collapse`() {
        assertEquals("hello-world", slug("hello - - - world"))
    }

    // --- Very long strings ---

    @Test
    fun `very long string produces valid slug`() {
        val longInput = "a".repeat(600)
        val result = slug(longInput)
        assertEquals(600, result.length)
        assertTrue(result.all { it == 'a' })
    }

    @Test
    fun `very long string with words`() {
        val longInput = (1..100).joinToString(" ") { "word$it" }
        val result = slug(longInput)
        assertTrue(result.length > 500)
        assertFalse(result.contains("--"))
        assertTrue(result.startsWith("word1-"))
    }

    @Test
    fun `very long string with maxLength`() {
        val longInput = (1..100).joinToString(" ") { "word$it" }
        val result = slug(longInput) { maxLength = 20 }
        assertTrue(result.length <= 20)
        assertFalse(result.endsWith("-"))
    }

    // --- Mixed Unicode scripts ---

    @Test
    fun `chinese characters are stripped with transliteration`() {
        val result = slug("hello \u4f60\u597d world")
        assertEquals("hello-world", result)
    }

    @Test
    fun `arabic with numbers`() {
        val result = slug("\u0645\u0631\u062d\u0628\u0627 123")
        assertEquals("123", result)
    }

    @Test
    fun `mixed latin and japanese`() {
        val result = slug("Tokyo \u6771\u4eac 2024")
        assertEquals("tokyo-2024", result)
    }

    @Test
    fun `cyrillic is stripped with transliteration`() {
        val result = slug("\u041f\u0440\u0438\u0432\u0435\u0442 world")
        assertEquals("world", result)
    }

    @Test
    fun `korean with latin`() {
        val result = slug("K-pop \ud55c\uad6d music")
        assertEquals("k-pop-music", result)
    }

    @Test
    fun `emoji characters are stripped`() {
        val result = slug("hello \ud83d\ude00 world")
        assertEquals("hello-world", result)
    }

    // --- Normalization form ---

    @Test
    fun `NFKD normalization decomposes compatibility characters`() {
        // fi ligature should decompose to "fi" with NFKD
        val result = slug("\ufb01nance") { normalizationForm = NormalizationForm.NFKD }
        assertEquals("finance", result)
    }

    @Test
    fun `NFD normalization default works`() {
        assertEquals("cafe", slug("Caf\u00e9") { normalizationForm = NormalizationForm.NFD })
    }

    @Test
    fun `NFKC normalization works`() {
        val result = slug("\ufb01nance") { normalizationForm = NormalizationForm.NFKC }
        assertEquals("finance", result)
    }

    // --- isValidSlug ---

    @Test
    fun `valid slug returns true`() {
        assertTrue(isValidSlug("hello-world"))
    }

    @Test
    fun `valid slug with numbers`() {
        assertTrue(isValidSlug("version-2-0-1"))
    }

    @Test
    fun `valid slug numbers only`() {
        assertTrue(isValidSlug("12345"))
    }

    @Test
    fun `empty string is not valid slug`() {
        assertFalse(isValidSlug(""))
    }

    @Test
    fun `uppercase is not valid slug`() {
        assertFalse(isValidSlug("Hello-World"))
    }

    @Test
    fun `consecutive separators is not valid slug`() {
        assertFalse(isValidSlug("hello--world"))
    }

    @Test
    fun `leading separator is not valid slug`() {
        assertFalse(isValidSlug("-hello-world"))
    }

    @Test
    fun `trailing separator is not valid slug`() {
        assertFalse(isValidSlug("hello-world-"))
    }

    @Test
    fun `spaces are not valid in slug`() {
        assertFalse(isValidSlug("hello world"))
    }

    @Test
    fun `special characters are not valid in slug`() {
        assertFalse(isValidSlug("hello!world"))
    }

    @Test
    fun `valid slug with custom separator`() {
        assertTrue(isValidSlug("hello_world", separator = "_"))
    }

    @Test
    fun `hyphen invalid when underscore is separator`() {
        assertFalse(isValidSlug("hello-world", separator = "_"))
    }

    @Test
    fun `consecutive custom separators are not valid`() {
        assertFalse(isValidSlug("hello__world", separator = "_"))
    }

    // --- slugifyAll ---

    @Test
    fun `slugifyAll with multiple strings`() {
        val result = slugifyAll(listOf("Hello World", "Foo Bar", "Caf\u00e9 Latt\u00e9"))
        assertEquals(listOf("hello-world", "foo-bar", "cafe-latte"), result)
    }

    @Test
    fun `slugifyAll with empty list`() {
        assertEquals(emptyList(), slugifyAll(emptyList()))
    }

    @Test
    fun `slugifyAll with config`() {
        val result = slugifyAll(listOf("Hello World", "Foo Bar")) { separator = "_" }
        assertEquals(listOf("hello_world", "foo_bar"), result)
    }

    @Test
    fun `slugifyAll preserves order`() {
        val inputs = listOf("Zebra", "Apple", "Mango")
        val result = slugifyAll(inputs)
        assertEquals(listOf("zebra", "apple", "mango"), result)
    }

    @Test
    fun `slugifyAll handles blanks`() {
        val result = slugifyAll(listOf("Hello", "   ", "World"))
        assertEquals(listOf("hello", "", "world"), result)
    }
}
