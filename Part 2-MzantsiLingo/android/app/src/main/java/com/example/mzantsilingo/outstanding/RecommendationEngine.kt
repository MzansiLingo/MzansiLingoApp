package com.example.mzantsilingo.outstanding

data class TopicScore(
    val topic: String,
    val accuracy: Int
)

data class LearningRecommendation(
    val topic: String,
    val message: String
)

object RecommendationEngine {

    fun recommend(scores: List<TopicScore>): LearningRecommendation? {
        if (scores.isEmpty()) return null

        val weakest = scores.minByOrNull { it.accuracy } ?: return null

        val message = when {
            weakest.accuracy < 60 ->
                "Let's practise ${weakest.topic}. This is your main focus today."
            weakest.accuracy < 80 ->
                "A little more practice on ${weakest.topic} will help."
            else ->
                "Keep improving ${weakest.topic} to make it even stronger."
        }

        return LearningRecommendation(
            topic = weakest.topic,
            message = message
        )
    }
}
