package com.example.mzantsilingo.ui.lesson

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.data.model.Word
import com.example.mzantsilingo.data.repository.LessonRepository
import com.example.mzantsilingo.databinding.ActivityLessonBinding
import com.example.mzantsilingo.ui.quiz.QuizActivity
import kotlinx.coroutines.launch

class LessonActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LESSON_ID = "extra_lesson_id"
        const val EXTRA_LESSON_TITLE = "extra_lesson_title"
        private const val XP_PER_WORD = 5
    }

    private lateinit var binding: ActivityLessonBinding
    private val lessonRepository = LessonRepository()

    private var words: List<Word> = emptyList()
    private var currentIndex = 0

    private lateinit var lessonId: String
    private var lessonTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lessonId = intent.getStringExtra(EXTRA_LESSON_ID) ?: ""
        lessonTitle = intent.getStringExtra(EXTRA_LESSON_TITLE) ?: ""

        binding.btnBack.setOnClickListener { finish() }

        binding.btnNextWord.setOnClickListener {
            if (currentIndex < words.size - 1) {
                currentIndex++
                showWord(currentIndex)
            } else {
                startQuiz()
            }
        }

        loadWords()
    }

    private fun loadWords() {
        lifecycleScope.launch {
            val result = lessonRepository.getWords(lessonId)
            result.onSuccess { loadedWords ->
                if (loadedWords.isEmpty()) {
                    Toast.makeText(this@LessonActivity, "No words in this lesson", Toast.LENGTH_SHORT).show()
                    finish()
                    return@onSuccess
                }
                words = loadedWords
                showWord(0)
            }.onFailure {
                Toast.makeText(this@LessonActivity, it.message ?: "Failed to load lesson", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showWord(index: Int) {
        val word = words[index]

        binding.tvTerm.text = word.term
        binding.tvTranslation.text = word.translation
        binding.tvPronunciation.text = word.pronunciation

        binding.tvProgress.text = "${index + 1}/${words.size}"
        binding.progressBar.progress = ((index + 1) * 100) / words.size
        binding.tvNewWordLabel.text = "NEW WORD · +$XP_PER_WORD XP"

        binding.btnNextWord.text =
            if (index == words.size - 1) "Start Quiz →" else "Next Word →"
    }

    private fun startQuiz() {
        val intent = Intent(this, QuizActivity::class.java).apply {
            putExtra(QuizActivity.EXTRA_LESSON_ID, lessonId)
            putExtra(QuizActivity.EXTRA_LESSON_TITLE, lessonTitle)
            putExtra(QuizActivity.EXTRA_WORD_COUNT, words.size)
        }
        startActivity(intent)
        finish()
    }
}