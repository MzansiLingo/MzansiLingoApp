package com.example.mzantsilingo.outstanding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationEngineTest {

    @Test
    fun recommendsWeakestTopic() {
        val result = RecommendationEngine.recommend(
            listOf(
                TopicScore("Greetings", 90),
                TopicScore("Numbers", 45),
                TopicScore("Food", 80)
            )
        )

        assertEquals("Numbers", result?.topic)
    }

    @Test
    fun givesPracticeMessageForLowScore() {
        val result = RecommendationEngine.recommend(
            listOf(TopicScore("Numbers", 45))
        )

        assertTrue(result?.message?.contains("Numbers") == true)
    }
}
