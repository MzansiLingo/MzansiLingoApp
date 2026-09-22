package com.example.mzantsilingo.outstanding

data class WordQuestion(
    val xhosaWord: String,
    val correctAnswer: String,
    val options: List<String>
)

data class WordFeederResult(
    val correct: Boolean,
    val xpEarned: Int,
    val newTotalXp: Int
)

class WordFeeder(private var totalXp: Int = 0) {

    fun submit(question: WordQuestion, answer: String): WordFeederResult {
        val correct = answer.equals(question.correctAnswer, ignoreCase = true)
        val earned = if (correct) 10 else 0
        totalXp += earned

        return WordFeederResult(
            correct = correct,
            xpEarned = earned,
            newTotalXp = totalXp
        )
    }

    fun getXp(): Int = totalXp
}
