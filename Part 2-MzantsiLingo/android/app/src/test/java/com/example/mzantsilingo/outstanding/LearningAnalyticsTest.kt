package com.example.mzantsilingo.outstanding

import org.junit.Assert.assertEquals
import org.junit.Test

class LearningAnalyticsTest {

    @Test
    fun calculatesStrongestAndWeakestTopics() {
        val analytics = LearningAnalyticsCalculator.calculate(
            wordsLearned = 25,
            xp = 120,
            scores = listOf(
                TopicScore("Greetings", 95),
                TopicScore("Numbers", 50)
            ),
            streakDays = 3
        )

        assertEquals("Greetings", analytics.strongestTopic)
        assertEquals("Numbers", analytics.needsPracticeTopic)
        assertEquals(72, analytics.quizAccuracy)
    }
}
