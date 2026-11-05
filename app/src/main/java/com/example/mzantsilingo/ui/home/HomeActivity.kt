package com.example.mzantsilingo.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzantsilingo.R
import com.example.mzantsilingo.data.local.SettingsPreferences
import com.example.mzantsilingo.data.local.UserSession
import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.example.mzantsilingo.data.repository.LoadResult
import com.example.mzantsilingo.data.repository.OfflineRepository
import com.example.mzantsilingo.data.sync.SyncScheduler
import com.example.mzantsilingo.databinding.ActivityHomeBinding
import com.example.mzantsilingo.ui.lesson.LessonActivity
import com.example.mzantsilingo.ui.lesson.LessonAdapter
import com.example.mzantsilingo.ui.profile.ProfileActivity
import com.example.mzantsilingo.ui.settings.SettingsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Home screen of the MzansiLingo application.
 *
 * Shows the user's total XP and the available lessons. Lessons come from the
 * REST API and are saved on the device (Room) so they still show without internet.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var lessonAdapter: LessonAdapter

    // Offline feature: loads lessons from the API or from the saved copy.
    private val offlineRepository by lazy { OfflineRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        setupLessonsRecyclerView()
    }

    // Reload every time the screen is shown, so a new XP total, a changed learning
    // language or a restored connection is picked up straight away.
    override fun onResume() {
        super.onResume()

        // Upload any XP that was earned while offline.
        SyncScheduler.schedule(this)

        loadXp()
        loadLessons()
    }

    private fun setupLessonsRecyclerView() {
        lessonAdapter = LessonAdapter(emptyList()) { lesson ->
            val intent = Intent(this, LessonActivity::class.java)
            intent.putExtra("LESSON_ID", lesson.id)
            intent.putExtra("LESSON_TITLE", lesson.title)
            startActivity(intent)
        }

        binding.rvLessons.layoutManager = LinearLayoutManager(this)
        binding.rvLessons.adapter = lessonAdapter
    }

    // Shows the user's total XP in the XP card.
    private fun loadXp() {
        val userId = UserSession.getUserId(this) ?: return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getXp(userId)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    binding.tvXp.text = getString(R.string.xp_format, body.totalXp)
                }
            } catch (e: Exception) {
                // Offline: keep the value that is already shown.
            }
        }
    }

    /**
     * Loads the lessons for the learning language chosen in Settings.
     * With no connection, the saved lessons are shown instead.
     */
    private fun loadLessons() {
        val prefs = SettingsPreferences(this)
        val language = prefs.learningLanguage
        val cacheEnabled = prefs.offlineLessons

        lifecycleScope.launch {
            when (val result = offlineRepository.loadLessons(language, cacheEnabled)) {

                is LoadResult.Loaded -> {
                    lessonAdapter.updateLessons(result.data)

                    if (result.fromCache) {
                        Snackbar.make(binding.root, R.string.offline_showing_saved, Snackbar.LENGTH_LONG).show()
                    } else if (result.data.isEmpty()) {
                        Toast.makeText(this@HomeActivity, "No lessons available.", Toast.LENGTH_SHORT).show()
                    } else if (cacheEnabled) {
                        // Online with Offline Lessons on: also save the words of every lesson.
                        offlineRepository.prefetchWords(result.data)
                    }
                }

                is LoadResult.Failed -> {
                    lessonAdapter.updateLessons(emptyList())
                    Snackbar.make(binding.root, R.string.offline_no_saved, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}