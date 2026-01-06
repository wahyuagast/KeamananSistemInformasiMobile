package com.wahyuagast.keamanansisteminformasimobile.ui.utils

import java.text.Normalizer

/**
 * Validation and sanitization helpers used by login/register forms.
 *
 * - isEmailValid: uses Android's Patterns for email format checking.
 * - isFieldEmpty: checks for blank input after trimming.
 * - isPasswordValid: improved password rules (Unicode-aware, no whitespace, letters+digits, optional special char requirement).
 * - sanitizeInput: trims, normalizes Unicode (NFKC), and strips control characters.
 */
object ValidationUtils {
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isFieldEmpty(field: String): Boolean {
        return field.trim().isBlank()
    }

    /**
     * Sanitize free-text input from users before sending to server or storing locally.
     * - Trims leading/trailing whitespace
     * - Normalizes Unicode (NFKC) to a canonical form
     * - Removes control characters (category C)
     *
     * This helps prevent weird invisible characters and normalizes visually-similar inputs.
     */
    fun sanitizeInput(input: String): String {
        val trimmed = input.trim()
        val normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFKC)
        // Remove control characters (including zero-width, etc.)
        return normalized.replace(Regex("\\p{C}+"), "")
    }

    /**
     * Password validation rules:
     * - Minimum length: 8
     * - Must contain at least one Unicode letter
     * - Must contain at least one Unicode digit
     * - Must NOT contain whitespace
     * - Optional: require at least one special (non-letter-or-digit) character
     */
    fun isPasswordValid(password: String, requireSpecialChar: Boolean = false): Boolean {
        if (password.length < 8) return false
        if (password.any { it.isWhitespace() }) return false

        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        if (!hasLetter || !hasDigit) return false

        if (requireSpecialChar) {
            val hasSpecial = password.any { !it.isLetterOrDigit() }
            if (!hasSpecial) return false
        }

        return true
    }

    /**
     * Optional helper to validate a username (simple rules):
     * - non-empty after trim
     * - length between 3..50
     * - contains only letters, digits, spaces, hyphen, underscore, dot

    fun isUsernameValid(username: String): Boolean {
    val s = sanitizeInput(username)
    if (s.length !in 3..50) return false
    return s.matches(Regex("^[\\p{L}0-9 ._-]+$"))
    }
     */
}
