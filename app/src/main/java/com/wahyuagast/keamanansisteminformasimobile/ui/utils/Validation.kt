package com.wahyuagast.keamanansisteminformasimobile.ui.utils

object ValidationUtils {
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isFieldEmpty(field: String): Boolean {
        return field.isBlank()
    }

    fun isPasswordValid(password: String): Boolean {
        if (password.length < 8) return false
        val charRegex = ".*[a-zA-Z].*".toRegex()
        val digitRegex = ".*[0-9].*".toRegex()
        return charRegex.matches(password) && digitRegex.matches(password)
    }
}
