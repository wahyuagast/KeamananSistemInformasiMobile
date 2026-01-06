package com.wahyuagast.keamanansisteminformasimobile.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class InputSanitizerTest {

    @Test
    fun sanitize_trimsAndCollapsesWhitespace() {
        val input = "  Hello   World \n"
        val out = InputSanitizer.sanitizeForApi(input)
        assertEquals("Hello World", out)
    }

    @Test
    fun sanitize_removesScriptTags() {
        val input = "<script>alert('x')</script>hello"
        val out = InputSanitizer.sanitizeForApi(input)
        assertEquals("hello", out)
    }

    @Test
    fun sanitize_removesAngleBrackets() {
        val input = "<b>bold</b>"
        val out = InputSanitizer.sanitizeForApi(input)
        assertEquals("bold", out)
    }

    @Test
    fun sanitize_removesControlChars() {
        val input = "hello\u0000world"
        val out = InputSanitizer.sanitizeForApi(input)
        assertEquals("helloworld", out)
    }
}

