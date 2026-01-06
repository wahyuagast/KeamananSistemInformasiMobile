package com.wahyuagast.keamanansisteminformasimobile.utils

/**
 * Minimal client-side input sanitation helper.
 * Purpose: reduce accidental script/HTML tag injection and control characters before
 * sending user input to the server or displaying it in the app. This is NOT a
 * substitute for server-side validation and escaping — server must validate and
 * sanitize/escape all input as the primary defense.
 */
object InputSanitizer {
    /**
     * Basic sanitize for text fields: trims, removes script tags and angle brackets,
     * and collapses repeated whitespace. Keeps characters safe for display and transport.
     */
    fun sanitizeForApi(value: String?): String {
        if (value == null) return ""
        // 1) Trim and collapse whitespace
        var v = value.trim().replace(Regex("\\s+"), " ")
        // 2) Remove any <script>...</script> blocks (case-insensitive)
        v = v.replace(Regex("(?i)<script.*?>.*?</script>", RegexOption.DOT_MATCHES_ALL), "")
        // 3) Remove leftover angle brackets to avoid HTML insertion
        v = v.replace("<", "").replace(">", "")
        // 4) Remove control characters (except newline/tab if desired)
        v = v.replace(Regex("[\\x00-\\x1F\\x7F]"), "")
        return v
    }

    @Suppress("unused")
    fun sanitizeForDisplay(value: String?): String = sanitizeForApi(value)
}

/**
 * Sanitize visible/display string. For Compose Text this is typically unnecessary
 * because Text renders plain text, but if you ever render HTML you must escape it
 * on the server or use proper HTML-safe rendering.
 */
