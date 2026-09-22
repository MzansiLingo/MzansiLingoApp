package com.example.mzantsilingo.outstanding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordFeederTest {

    @Test
    fun correctAnswerAwardsXp() {
        val game = WordFeeder()

        val question = WordQuestion(
            xhosaWord = "Molo",
            correctAnswer = "Hello",
            options = listOf("Hello", "Food", "School")
        )

        val result = game.submit(question, "Hello")

        assertTrue(result.correct)
        assertEquals(10, result.xpEarned)
    }

    @Test
    fun wrongAnswerAwardsNoXp() {
        val game = WordFeeder()

        val question = WordQuestion(
            xhosaWord = "Molo",
            correctAnswer = "Hello",
            options = listOf("Hello", "Food", "School")
        )

        val result = game.submit(question, "Food")

        assertTrue(!result.correct)
        assertEquals(0, result.xpEarned)
    }
}
