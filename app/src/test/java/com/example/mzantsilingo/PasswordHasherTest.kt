package com.example.mzantsilingo

import com.example.mzantsilingo.util.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun `same password produces same hash`() {
        val hash1 = PasswordHasher.hash("mypassword123")
        val hash2 = PasswordHasher.hash("mypassword123")
        assertEquals(hash1, hash2)
    }

    @Test
    fun `different passwords produce different hashes`() {
        val hash1 = PasswordHasher.hash("mypassword123")
        val hash2 = PasswordHasher.hash("differentpassword")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `hash output is 64 hex characters for SHA-256`() {
        val hash = PasswordHasher.hash("test")
        assertEquals(64, hash.length)
    }
}