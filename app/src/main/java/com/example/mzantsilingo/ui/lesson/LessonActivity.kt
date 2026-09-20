package com.example.mzantsilingo.ui.lesson

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.data.model.Word
import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.example.mzantsilingo.databinding.ActivityLessonBinding
import kotlinx.coroutines.launch
import java.util.Locale
import com.example.mzantsilingo.data.local.UserSession
import com.example.mzantsilingo.data.model.AddXpRequest

class LessonActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityLessonBinding

    private var words: List<Word> = emptyList()
    private var currentWordIndex = 0

    private lateinit var textToSpeech: TextToSpeech
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialise Android's built-in Text-to-Speech engine.
        textToSpeech = TextToSpeech(this, this)

        val lessonId = intent.getIntExtra("LESSON_ID", -1)

        if (lessonId == -1) {
            Toast.makeText(
                this,
                "Lesson could not be found.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setupButtons()
        loadWords(lessonId)
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            // Try to use the isiXhosa language.
            val result = textToSpeech.setLanguage(
                Locale("xh", "ZA")
            )

            ttsReady =
                result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED

            if (!ttsReady) {
                Toast.makeText(
                    this,
                    "isiXhosa speech is not available. Using default speech.",
                    Toast.LENGTH_SHORT
                ).show()

                textToSpeech.language = Locale.getDefault()
                ttsReady = true
            }

        } else {

            Toast.makeText(
                this,
                "Text-to-Speech could not be initialised.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupButtons() {

        binding.btnLearned.setOnClickListener {

            if (words.isEmpty()) {
                return@setOnClickListener
            }

            if (currentWordIndex < words.lastIndex) {

                currentWordIndex++

                showCurrentWord()

            } else {

                completeLesson()
            }
        }

        binding.btnListen.setOnClickListener {

            if (words.isNotEmpty() && ttsReady) {

                val currentWord =
                    words[currentWordIndex]

                speakWord(currentWord.xhosaWord)
            }
        }
    }

    private fun speakWord(word: String) {

        textToSpeech.speak(
            word,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "MzansiLingoWord"
        )
    }

    private fun loadWords(lessonId: Int) {

        binding.loadingProgress.visibility =
            android.view.View.VISIBLE

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.apiService.getWords(lessonId)

                binding.loadingProgress.visibility =
                    android.view.View.GONE

                if (response.isSuccessful && response.body() != null) {

                    words = response.body()!!

                    if (words.isEmpty()) {

                        Toast.makeText(
                            this@LessonActivity,
                            "No words found for this lesson.",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                        return@launch
                    }

                    currentWordIndex = 0

                    showCurrentWord()

                } else {

                    Toast.makeText(
                        this@LessonActivity,
                        "Unable to load lesson content.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                binding.loadingProgress.visibility =
                    android.view.View.GONE

                Toast.makeText(
                    this@LessonActivity,
                    "Could not connect to the server.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showCurrentWord() {

        val currentWord =
            words[currentWordIndex]

        binding.tvXhosaWord.text =
            currentWord.xhosaWord

        binding.tvEnglishMeaning.text =
            currentWord.englishMeaning

        val currentNumber =
            currentWordIndex + 1

        val totalWords =
            words.size

        binding.tvWordCounter.text =
            "Word $currentNumber of $totalWords"

        val progress =
            ((currentNumber.toFloat() / totalWords) * 100).toInt()

        binding.lessonProgress.progress =
            progress

        if (currentWordIndex == words.lastIndex) {

            binding.btnLearned.text =
                "Complete Lesson"

        } else {

            binding.btnLearned.text =
                "I've learned this"
        }
    }

    private fun completeLesson() {

        val userId = UserSession.getUserId(this)

        if (userId == null) {
            Toast.makeText(
                this,
                "User session not found. Please log in again.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        lifecycleScope.launch {

            try {

                // Award 50 XP to the logged-in user.
                val response = RetrofitClient.apiService.addXp(
                    userId,
                    AddXpRequest(xp = 50)
                )

                if (response.isSuccessful && response.body() != null) {

                    val intent = android.content.Intent(
                        this@LessonActivity,
                        LessonCompleteActivity::class.java
                    )

                    intent.putExtra(
                        "WORDS_LEARNED",
                        words.size
                    )

                    intent.putExtra(
                        "XP_EARNED",
                        50
                    )

                    startActivity(intent)

                    finish()

                } else {

                    Toast.makeText(
                        this@LessonActivity,
                        "Could not save your XP. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@LessonActivity,
                    "Could not connect to the server.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {

        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }

        super.onDestroy()
    }
}