package com.philiprehberger.slug

import kotlin.test.Test
import kotlin.test.assertEquals

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
    fun `numbers are preserved`() {
        assertEquals("version-2-0-1", slug("Version 2.0.1"))
    }

    @Test
    fun `no lowercase when disabled`() {
        assertEquals("Hello-World", slug("Hello World") { lowercase = false })
    }
}
