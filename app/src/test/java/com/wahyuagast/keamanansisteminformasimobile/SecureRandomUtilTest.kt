package com.wahyuagast.keamanansisteminformasimobile

import com.wahyuagast.keamanansisteminformasimobile.utils.SecureRandomUtil
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SecureRandomUtil to demonstrate secure random number generation.
 *
 * Purpose: Show that the app uses cryptographically secure random generation
 * for security audits (MobSF, code review, etc.).
 */
class SecureRandomUtilTest {

    @Test
    fun generateRandomBytes_returnsCorrectLength() {
        val bytes = SecureRandomUtil.generateRandomBytes(32)
        assertEquals(32, bytes.size)
    }

    @Test
    fun generateRandomBytes_producesUnpredictableOutput() {
        val bytes1 = SecureRandomUtil.generateRandomBytes(16)
        val bytes2 = SecureRandomUtil.generateRandomBytes(16)

        // Two random byte arrays should not be equal
        assertFalse(bytes1.contentEquals(bytes2))
    }

    @Test
    fun generateSecureToken_returnsHexString() {
        val token = SecureRandomUtil.generateSecureToken(16)

        // 16 bytes = 32 hex characters
        assertEquals(32, token.length)

        // Should only contain hex characters (0-9, a-f)
        assertTrue(token.matches(Regex("[0-9a-f]+")))
    }

    @Test
    fun generateSecureToken_producesUniqueTokens() {
        val token1 = SecureRandomUtil.generateSecureToken()
        val token2 = SecureRandomUtil.generateSecureToken()

        // Tokens should be different (collision probability is negligible)
        assertNotEquals(token1, token2)
    }

    @Test
    fun generateUUID_returnsValidFormat() {
        val uuid = SecureRandomUtil.generateUUID()

        // UUID format: 8-4-4-4-12 hex digits with hyphens
        assertTrue(uuid.matches(Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")))
    }

    @Test
    fun generateUUID_producesUniqueIdentifiers() {
        val uuid1 = SecureRandomUtil.generateUUID()
        val uuid2 = SecureRandomUtil.generateUUID()

        assertNotEquals(uuid1, uuid2)
    }

    @Test
    fun generateAlphanumeric_returnsCorrectLength() {
        val code = SecureRandomUtil.generateAlphanumeric(6)
        assertEquals(6, code.length)
    }

    @Test
    fun generateAlphanumeric_containsOnlyValidCharacters() {
        val code = SecureRandomUtil.generateAlphanumeric(20)

        // Should only contain letters and digits
        assertTrue(code.matches(Regex("[A-Za-z0-9]+")))
    }

    @Test
    fun generateAlphanumeric_producesUniqueCodes() {
        val code1 = SecureRandomUtil.generateAlphanumeric(8)
        val code2 = SecureRandomUtil.generateAlphanumeric(8)

        assertNotEquals(code1, code2)
    }

    @Test
    fun secureRandomInt_staysWithinBounds() {
        val bound = 100

        // Test multiple times to ensure consistency
        repeat(50) {
            val random = SecureRandomUtil.secureRandomInt(bound)
            assertTrue(random >= 0)
            assertTrue(random < bound)
        }
    }

    @Test
    fun secureRandomInt_producesVariedValues() {
        val values = mutableSetOf<Int>()

        // Generate 100 random values in range [0, 1000)
        repeat(100) {
            values.add(SecureRandomUtil.secureRandomInt(1000))
        }

        // Should have produced many different values (at least 50 unique)
        assertTrue("Expected varied random values, got ${values.size} unique values",
                   values.size >= 50)
    }
}

