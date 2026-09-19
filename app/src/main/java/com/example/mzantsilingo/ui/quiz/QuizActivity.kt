package com.example.mzantsilingo.ui.quiz

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.data.model.QuizQuestion
import com.example.mzantsilingo.data.repository.LessonRepository
import com.example.mzantsilingo.databinding.ActivityQuizBinding
import com.example.mzantsilingo.databinding.DialogRewardBinding
import com.example.mzantsilingo.ui.home.HomeActivity
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LESSON_ID = "extra_lesson_id"
        const val EXTRA_LESSON_TITLE = "extra_lesson_title"
        const val EXTRA_WORD_COUNT = "extra_word_count"
        private const val XP_PER_CORRECT_ANSWER = 5
        private const val XP_LESSON_BONUS = 25
    }

    private lateinit var binding: ActivityQuizBinding
    private val lessonRepository = LessonRepository()

    private var questions: List<QuizQuestion> = emptyList()
    private var currentIndex = 0
    private var correctCount = 0
    private var hasAnswered = false

    private lateinit var lessonId: String
    private var wordCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lessonId = intent.getStringExtra(EXTRA_LESSON_ID) ?: ""
        wordCount = intent.getIntExtra(EXTRA_WORD_COUNT, 4)

        binding.btnBack.setOnClickListener { finish() }

        loadQuiz()
    }

    private fun loadQuiz() {
        lifecycleScope.launch {
            val result = lessonRepository.getQuiz(lessonId)
            result.onSuccess { loadedQuestions ->
                if (loadedQuestions.isEmpty()) {
                    Toast.makeText(this@QuizActivity, "No quiz questions found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@onSuccess
                }
                questions = loadedQuestions
                showQuestion(0)
            }.onFailure {
                Toast.makeText(this@QuizActivity, it.message ?: "Failed to load quiz", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showQuestion(index: Int) {
        hasAnswered = false
        val question = questions[index]

        binding.tvProgress.text = "${index + 1}/${questions.size}"
        binding.progressBar.progress = ((index + 1) * 100) / questions.size
        binding.tvPrompt.text = question.prompt

        val optionButtons = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)

        optionButtons.forEachIndexed { i, button ->
            if (i < question.options.size) {
                button.visibility = android.view.View.VISIBLE
                button.text = question.options[i]
                button.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.WHITE
                )
                button.isEnabled = true
                button.setOnClickListener { onOptionSelected(i, question, button) }
            } else {
                button.visibility = android.view.View.GONE
            }
        }

        binding.tvFeedback.visibility = android.view.View.GONE
        binding.btnNext.visibility = android.view.View.GONE
    }

    private fun onOptionSelected(selectedIndex: Int, question: QuizQuestion, selectedButton: android.widget.Button) {
        if (hasAnswered) return
        hasAnswered = true

        val optionButtons = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)
        val isCorrect = selectedIndex == question.correctAnswerIndex

        // Disable further taps
        optionButtons.forEach { it.isEnabled = false }

        // Highlight the correct answer green always
        optionButtons.getOrNull(question.correctAnswerIndex)?.backgroundTintList =
            android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#C8E6C9"))

        if (isCorrect) {
            correctCount++
            binding.tvFeedback.text = "✅ Correct! +$XP_PER_CORRECT_ANSWER XP Bonus"
            binding.tvFeedback.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"))
            binding.tvFeedback.setTextColor(android.graphics.Color.parseColor("#2E8B4F"))
        } else {
            // Highlight the wrong selection red
            selectedButton.backgroundTintList =
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFCDD2"))
            binding.tvFeedback.text = "❌ Not quite — correct answer highlighted above"
            binding.tvFeedback.setBackgroundColor(android.graphics.Color.parseColor("#FFEBEE"))
            binding.tvFeedback.setTextColor(android.graphics.Color.parseColor("#C62828"))
        }

        binding.tvFeedback.visibility = android.view.View.VISIBLE
        binding.btnNext.visibility = android.view.View.VISIBLE
        binding.btnNext.text = if (currentIndex == questions.size - 1) "Finish" else "Next Question"

        binding.btnNext.setOnClickListener {
            if (currentIndex < questions.size - 1) {
                currentIndex++
                showQuestion(currentIndex)
            } else {
                finishQuiz()
            }
        }
    }

    private fun finishQuiz() {
        val isPerfectScore = correctCount == questions.size
        val xpEarned = (correctCount * XP_PER_CORRECT_ANSWER) + if (isPerfectScore) XP_LESSON_BONUS else 0

        showRewardDialog(isPerfectScore, wordCount, xpEarned)
    }

    private fun showRewardDialog(isPerfectScore: Boolean, wordsLearned: Int, xpEarned: Int) {
        val dialogBinding = DialogRewardBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)

        dialogBinding.tvRewardTitle.text = if (isPerfectScore) "Perfect Score!" else "Lesson Complete!"
        dialogBinding.tvWordsLearned.text = "+$wordsLearned words learned"
        dialogBinding.tvXpEarned.text = "⭐ +$xpEarned XP"

        if (isPerfectScore) {
            dialogBinding.tvAchievement.visibility = android.view.View.VISIBLE
            dialogBinding.tvAchievement.text = "🏅 Achievement Unlocked: First Lesson"
        }

        dialogBinding.btnContinue.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        dialog.show()
    }
}