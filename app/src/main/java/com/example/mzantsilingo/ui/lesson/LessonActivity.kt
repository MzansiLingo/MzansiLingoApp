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

/**
 * Displays the content of a selected language lesson.
 *
 * The activity retrieves lesson words from the REST API, allows the
 * user to move through the words, and provides Text-to-Speech
 * functionality for pronunciation.
 */
class LessonActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    // View binding provides access to the views in activity_lesson.xml.
    private lateinit var binding: ActivityLessonBinding

    // Stores the list of vocabulary words retrieved from the API.
    private var words: List<Word> = emptyList()

    // Keeps track of the word currently being displayed.
    private var currentWordIndex = 0

    // Android Text-to-Speech engine used to pronounce isiXhosa words.
    private lateinit var textToSpeech: TextToSpeech

    // Indicates whether the Text-to-Speech engine has been successfully initialised.
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the lesson layout using View Binding.
        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialise Android's built-in Text-to-Speech engine.
        textToSpeech = TextToSpeech(this, this)

        // Retrieve the lesson ID passed from HomeActivity.
        val lessonId = intent.getIntExtra("LESSON_ID", -1)

        // Check whether a valid lesson ID was provided.
        if (lessonId == -1) {

            // Inform the user that the selected lesson could not be found.
            Toast.makeText(
                this,
                "Lesson could not be found.",
                Toast.LENGTH_SHORT
            ).show()

            // Close the activity because there is no valid lesson to display.
            finish()
            return
        }

        // Configure the lesson buttons.
        setupButtons()

        // Retrieve the lesson words from the REST API.
        loadWords(lessonId)
    }

    /**
     * Called when the Text-to-Speech engine has finished initialising.
     *
     * @param status Indicates whether the Text-to-Speech engine was initialised successfully.
     */
    override fun onInit(status: Int) {

        // Check whether Text-to-Speech initialisation was successful.
        if (status == TextToSpeech.SUCCESS) {

            // Try to configure Text-to-Speech for isiXhosa used in South Africa.
            val result = textToSpeech.setLanguage(
                Locale("xh", "ZA")
            )

            // Determine whether the requested language is available.
            ttsReady =
                result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED

            // Use the device's default language if isiXhosa speech is unavailable.
            if (!ttsReady) {

                Toast.makeText(
                    this,
                    "isiXhosa speech is not available. Using default speech.",
                    Toast.LENGTH_SHORT
                ).show()

                // Fall back to the device's default language.
                textToSpeech.language = Locale.getDefault()
                ttsReady = true
            }

        } else {

            // Inform the user if the Text-to-Speech engine could not initialise.
            Toast.makeText(
                this,
                "Text-to-Speech could not be initialised.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Sets up the buttons used to navigate and interact with lesson words.
     */
    private fun setupButtons() {

        // Handle the "I've learned this" / "Complete Lesson" button.
        binding.btnLearned.setOnClickListener {

            // Do nothing if no lesson words have been loaded.
            if (words.isEmpty()) {
                return@setOnClickListener
            }

            // Move to the next word if the current word is not the last one.
            if (currentWordIndex < words.lastIndex) {

                currentWordIndex++

                // Display the next word.
                showCurrentWord()

            } else {

                // Complete the lesson when the final word has been reached.
                completeLesson()
            }
        }

        // Handle the pronunciation button.
        binding.btnListen.setOnClickListener {

            // Only attempt to speak when words are available
            // and Text-to-Speech has been successfully initialised.
            if (words.isNotEmpty() && ttsReady) {

                // Retrieve the word currently displayed on screen.
                val currentWord =
                    words[currentWordIndex]

                // Pronounce the selected isiXhosa word.
                speakWord(currentWord.xhosaWord)
            }
        }
    }

    /**
     * Uses Android Text-to-Speech to pronounce a word.
     *
     * @param word The word that should be spoken.
     */
    private fun speakWord(word: String) {

        // Speak the supplied word and replace any speech already in the queue.
        textToSpeech.speak(
            word,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "MzansiLingoWord"
        )
    }

    /**
     * Retrieves the vocabulary words for the selected lesson from the REST API.
     *
     * @param lessonId ID of the lesson whose words should be loaded.
     */
    private fun loadWords(lessonId: Int) {

        // Display the loading indicator while the API request is running.
        binding.loadingProgress.visibility =
            android.view.View.VISIBLE

        // Launch the API request inside a lifecycle-aware coroutine.
        lifecycleScope.launch {

            try {

                // Request the words belonging to the selected lesson.
                val response =
                    RetrofitClient.apiService.getWords(lessonId)

                // Hide the loading indicator after receiving the response.
                binding.loadingProgress.visibility =
                    android.view.View.GONE

                // Check whether the API request was successful
                // and returned a response body.
                if (response.isSuccessful && response.body() != null) {

                    // Store the returned vocabulary words.
                    words = response.body()!!

                    // Check whether the lesson contains any words.
                    if (words.isEmpty()) {

                        Toast.makeText(
                            this@LessonActivity,
                            "No words found for this lesson.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Close the activity because there is no lesson content to display.
                        finish()
                        return@launch
                    }

                    // Start from the first word in the lesson.
                    currentWordIndex = 0

                    // Display the first word.
                    showCurrentWord()

                } else {

                    // Inform the user when the API could not provide lesson content.
                    Toast.makeText(
                        this@LessonActivity,
                        "Unable to load lesson content.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                // Hide the loading indicator if a connection error occurs.
                binding.loadingProgress.visibility =
                    android.view.View.GONE

                // Inform the user that the application could not reach the server.
                Toast.makeText(
                    this@LessonActivity,
                    "Could not connect to the server.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Updates the screen with the currently selected vocabulary word.
     */
    private fun showCurrentWord() {

        // Retrieve the word currently being displayed.
        val currentWord =
            words[currentWordIndex]

        // Display the isiXhosa word.
        binding.tvXhosaWord.text =
            currentWord.xhosaWord

        // Display the English meaning of the word.
        binding.tvEnglishMeaning.text =
            currentWord.englishMeaning

        // Convert the zero-based index into a user-friendly word number.
        val currentNumber =
            currentWordIndex + 1

        // Determine the total number of words in the lesson.
        val totalWords =
            words.size

        // Display the current word position to the user.
        binding.tvWordCounter.text =
            "Word $currentNumber of $totalWords"

        // Calculate lesson progress as a percentage.
        val progress =
            ((currentNumber.toFloat() / totalWords) * 100).toInt()

        // Update the lesson progress bar.
        binding.lessonProgress.progress =
            progress

        // Change the button text when the final word is displayed.
        if (currentWordIndex == words.lastIndex) {

            binding.btnLearned.text =
                "Complete Lesson"

        } else {

            // Keep the normal button text while there are more words to learn.
            binding.btnLearned.text =
                "I've learned this"
        }
    }

    /**
     * Completes the current lesson and awards XP to the logged-in user.
     */
    private fun completeLesson() {

        // Retrieve the currently logged-in user's ID from the local session.
        val userId = UserSession.getUserId(this)

        // Check whether a logged-in user session exists.
        if (userId == null) {

            // Inform the user that their session is no longer available.
            Toast.makeText(
                this,
                "User session not found. Please log in again.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Launch the XP update request inside a lifecycle-aware coroutine.
        lifecycleScope.launch {

            try {

                // Award 50 XP to the logged-in user.
                val response = RetrofitClient.apiService.addXp(
                    userId,
                    AddXpRequest(xp = 50)
                )

                // Check whether the XP update was successful.
                if (response.isSuccessful && response.body() != null) {

                    // Create an Intent to open the lesson completion screen.
                    val intent = android.content.Intent(
                        this@LessonActivity,
                        LessonCompleteActivity::class.java
                    )

                    // Pass the number of words learned to the completion screen.
                    intent.putExtra(
                        "WORDS_LEARNED",
                        words.size
                    )

                    // Pass the amount of XP earned to the completion screen.
                    intent.putExtra(
                        "XP_EARNED",
                        50
                    )

                    // Open the lesson completion screen.
                    startActivity(intent)

                    // Close the current lesson screen.
                    finish()

                } else {

                    // Inform the user if the XP could not be saved.
                    Toast.makeText(
                        this@LessonActivity,
                        "Could not save your XP. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                // Inform the user if the application cannot connect to the server.
                Toast.makeText(
                    this@LessonActivity,
                    "Could not connect to the server.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Releases the Text-to-Speech resources when the activity is destroyed.
     */
    override fun onDestroy() {

        // Check that the Text-to-Speech engine has been initialised
        // before attempting to stop and shut it down.
        if (::textToSpeech.isInitialized) {

            // Stop any speech that is currently playing.
            textToSpeech.stop()

            // Release the Text-to-Speech resources.
            textToSpeech.shutdown()
        }

        super.onDestroy()
    }
}