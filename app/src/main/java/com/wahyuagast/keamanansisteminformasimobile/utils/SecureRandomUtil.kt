package com.wahyuagast.keamanansisteminformasimobile.utils

import java.security.SecureRandom
import java.util.UUID

/**
 * Secure random number generation utilities.
 *
 * IMPORTANT: Never use java.util.Random for security-sensitive operations
 * (tokens, session IDs, cryptographic keys, nonces).
 *
 * Why? java.util.Random is predictable (PRNG with 48-bit seed).
 * An attacker who observes a few outputs can predict future values.
 *
 * Always use:
 * - java.security.SecureRandom (cryptographically strong PRNG)
 * - java.util.UUID.randomUUID() (uses SecureRandom internally)
 *
 * MobSF Warning Context:
 * If MobSF flags "insecure random" in third-party libraries (h6/b.java, etc.),
 * those are obfuscated SDK files you cannot modify. Document that YOUR code
 * uses SecureRandom and show this utility class as proof.
 */
object SecureRandomUtil {

    private val secureRandom = SecureRandom()

    /**
     * Generate a cryptographically secure random byte array.
     * Use for: session tokens, CSRF tokens, encryption keys.
     *
     * Example:
     * ```kotlin
     * val token = SecureRandomUtil.generateRandomBytes(32)
     * val tokenHex = token.joinToString("") { "%02x".format(it) }
     * ```
     */
    fun generateRandomBytes(length: Int): ByteArray {
        val bytes = ByteArray(length)
        secureRandom.nextBytes(bytes)
        return bytes
    }

    /**
     * Generate a cryptographically secure random hex string.
     * Use for: API tokens, file upload IDs, audit log correlation IDs.
     *
     * Example:
     * ```kotlin
     * val sessionToken = SecureRandomUtil.generateSecureToken(32) // 64 hex chars
     * ```
     */
    fun generateSecureToken(byteLength: Int = 32): String {
        val bytes = generateRandomBytes(byteLength)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Generate a secure random UUID (Type 4).
     * This internally uses SecureRandom and is safe for unique IDs.
     *
     * Use for: temporary file names, request IDs, correlation IDs.
     *
     * Example:
     * ```kotlin
     * val fileId = SecureRandomUtil.generateUUID()
     * val tempFile = File(cacheDir, "upload_$fileId.tmp")
     * ```
     */
    fun generateUUID(): String = UUID.randomUUID().toString()

    /**
     * Generate a secure random integer in range [0, bound).
     * Use when you need a random number for non-cryptographic purposes
     * but want to avoid the predictability of java.util.Random.
     *
     * Example:
     * ```kotlin
     * val randomIndex = SecureRandomUtil.secureRandomInt(listSize)
     * ```
     */
    fun secureRandomInt(bound: Int): Int {
        require(bound > 0) { "Bound must be positive" }
        return secureRandom.nextInt(bound)
    }

    /**
     * Generate a secure random alphanumeric string.
     * Use for: OTP codes, verification codes, short tokens.
     *
     * Example:
     * ```kotlin
     * val otp = SecureRandomUtil.generateAlphanumeric(6) // e.g., "A3x9Z2"
     * ```
     */
    fun generateAlphanumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars[secureRandomInt(chars.length)] }
            .joinToString("")
    }
}

