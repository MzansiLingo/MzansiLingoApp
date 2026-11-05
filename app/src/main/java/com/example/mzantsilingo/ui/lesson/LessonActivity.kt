package com.example.mzantsilingo.ui.lesson

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.R
import com.example.mzantsilingo.data.local.SettingsPreferences
import com.example.mzantsilingo.data.local.UserSession
import com.example.mzantsilingo.data.model.Word
import com.example.mzantsilingo.data.repository.LoadResult
import com.example.mzantsilingo.data.repository.OfflineRepository
import com.example.mzantsilingo.data.repository.XpResult
import com.example.mzantsilingo.data.sync.SyncScheduler
import com.example.mzantsilingo.databinding.ActivityLessonBinding
import com.example.mzantsilingo.ui.auth.LoginActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Shows the words of one lesson, one at a time.
 *
 * - Words come from the REST API, or from the copy saved on the device when offline.
 * - The user earns 10 XP for every word in the lesson.
 * - If the XP cannot be uploaded (no connection), it is saved and uploaded later.
 */
class LessonActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    companion object {
        // XP earned for every word in a lesson (a 5-word lesson gives 50 XP).
        const val XP_PER_WORD = 10
    }

    private lateinit var binding: ActivityLessonBinding

    // The words of the lesson, always in lesson order.
    private var words: List<Word> = emptyList()

    // The word currently being displayed.
    private var currentWordIndex = 0

    private var lessonId: Int = -1
    private var lessonTitle: String = "your lesson"
    private var lessonCompleted = false
    private var reminderShown = false
    // True while the XP is being saved, so the button cannot be pressed twice.
    private var saving = false

    // Android Text-to-Speech engine used to pronounce the words.
    private lateinit var textToSpeech: TextToSpeech
    private var ttsReady = false

    // Loads words from the API or the saved copy, and queues XP when offline.
    private val offlineRepository by lazy { OfflineRepository(this) }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Permission result will be handled when we post the notification.
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LessonNotificationHelper.createNotificationChannel(this)
        requestNotificationPermission()

        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textToSpeech = TextToSpeech(this, this)

        lessonId = intent.getIntExtra("LESSON_ID", -1)

        if (lessonId == -1) {
            Toast.makeText(this, "Lesson could not be found.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Show the real lesson name (for example "Numbers 1-5") instead of a fixed title.
        intent.getStringExtra("LESSON_TITLE")?.let {
            lessonTitle = it
            binding.tvLessonTitle.text = it
        }

        setupButtons()
        loadWords(lessonId)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale("xh", "ZA"))

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
            Toast.makeText(this, "Text-to-Speech could not be initialised.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupButtons() {

        // "I've learned this" moves to the next word. On the last word it completes the lesson.
        binding.btnLearned.setOnClickListener {
            if (words.isEmpty() || saving) {
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
                speakWord(words[currentWordIndex].xhosaWord)
            }
        }
    }

    private fun speakWord(word: String) {
        textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, "MzansiLingoWord")
    }

    /**
     * Loads the words of the lesson. They come from the REST API, or from the
     * saved copy when there is no connection.
     */
    private fun loadWords(lessonId: Int) {

        binding.loadingProgress.visibility = View.VISIBLE
        val cacheEnabled = SettingsPreferences(this).offlineLessons

        lifecycleScope.launch {

            val result = offlineRepository.loadWords(lessonId, cacheEnabled)

            binding.loadingProgress.visibility = View.GONE

            when (result) {

                is LoadResult.Loaded -> {
                    // Always show the words in lesson order.
                    words = result.data.sortedBy { it.id }

                    if (words.isEmpty()) {
                        Toast.makeText(
                            this@LessonActivity,
                            "No words found for this lesson.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return@launch
                    }

                    if (result.fromCache) {
                        Snackbar.make(binding.root, R.string.offline_showing_saved, Snackbar.LENGTH_LONG).show()
                    }

                    currentWordIndex = intent.getIntExtra("RESUME_WORD_INDEX", 0)
                        .coerceIn(0, words.lastIndex)
                    showCurrentWord()
                }

                is LoadResult.Failed -> {
                    Snackbar.make(binding.root, R.string.offline_no_saved, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    // Shows the current word, "Word 2 of 5" and the progress bar.
    private fun showCurrentWord() {
        val currentWord = words[currentWordIndex]
        val currentNumber = currentWordIndex + 1
        val totalWords = words.size

        binding.tvXhosaWord.text = currentWord.xhosaWord
        binding.tvEnglishMeaning.text = currentWord.englishMeaning
        binding.tvWordCounter.text = getString(R.string.lesson_word_progress, currentNumber, totalWords)
        binding.lessonProgress.progress = ((currentNumber.toFloat() / totalWords) * 100).toInt()

        updateButton()
    }

    // The button says "I've learned this", or "Complete Lesson" on the last word.
    private fun updateButton() {
        binding.btnLearned.isEnabled = !saving
        binding.btnLearned.text = when {
            saving -> getString(R.string.lesson_saving)
            currentWordIndex == words.lastIndex -> getString(R.string.lesson_complete_button)
            else -> getString(R.string.lesson_learned)
        }
    }

    /**
     * Completes the lesson and awards 10 XP for every word.
     *
     * The lesson always completes: if the XP cannot be uploaded right now it is
     * saved on the device and uploaded automatically when the connection is back.
     */
    private fun completeLesson() {

        val userId = UserSession.getUserId(this)

        if (userId == null) {
            // Nobody is logged in: send the user to the login screen.
            Toast.makeText(this, R.string.msg_session_missing, Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        val xpEarned = words.size * XP_PER_WORD

        saving = true
        updateButton()

        lifecycleScope.launch {
            // Save lesson completion locally first.
            offlineRepository.recordLessonCompleted(
                userId = userId,
                lessonId = lessonId
            )

            // Then award XP using the existing online/offline flow.
            when (offlineRepository.addXp(userId, xpEarned)) {

                XpResult.SYNCED -> openCompleteScreen(xpEarned)

                XpResult.QUEUED -> {
                    // No connection (or the server is busy): upload the XP later.
                    SyncScheduler.schedule(this@LessonActivity)
                    Toast.makeText(this@LessonActivity, R.string.offline_xp_saved, Toast.LENGTH_LONG).show()
                    openCompleteScreen(xpEarned)
                }

                XpResult.FAILED -> {
                    saving = false
                    updateButton()
                    Toast.makeText(this@LessonActivity, R.string.msg_xp_failed, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    // Opens the "Lesson Complete" screen and closes the lesson.
    private fun openCompleteScreen(xpEarned: Int) {
        lessonCompleted = true

        val intent = Intent(this, LessonCompleteActivity::class.java)
        intent.putExtra("WORDS_LEARNED", words.size)
        intent.putExtra("XP_EARNED", xpEarned)
        startActivity(intent)
        finish()
    }
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (
            words.isNotEmpty() &&
            lessonId != -1 &&
            !saving &&
            !lessonCompleted &&
            !reminderShown
        ) {
            reminderShown = true

            LessonNotificationHelper.showLessonReminder(
                context = this,
                lessonId = lessonId,
                lessonTitle = lessonTitle,
                wordIndex = currentWordIndex
            )
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