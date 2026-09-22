package com.example.mzantsilingo.outstanding

data class LearningAnalytics(
    val wordsLearned: Int,
    val xp: Int,
    val quizAccuracy: Int,
    val strongestTopic: String,
    val needsPracticeTopic: String,
    val streakDays: Int
)

object LearningAnalyticsCalculator {

    fun calculate(
        wordsLearned: Int,
        xp: Int,
        scores: List<TopicScore>,
        streakDays: Int
    ): LearningAnalytics {

        val strongest = scores.maxByOrNull { it.accuracy }
        val weakest = scores.minByOrNull { it.accuracy }

        return LearningAnalytics(
            wordsLearned = wordsLearned,
            xp = xp,
            quizAccuracy = if (scores.isEmpty()) 0 else scores.map { it.accuracy }.average().toInt(),
            strongestTopic = strongest?.topic ?: "Not enough data",
            needsPracticeTopic = weakest?.topic ?: "Not enough data",
            streakDays = streakDays
        )
    }
}
