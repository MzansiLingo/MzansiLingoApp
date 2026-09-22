package com.example.mzantsilingo.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.example.mzantsilingo.databinding.ActivityHomeBinding
import com.example.mzantsilingo.ui.lesson.LessonActivity
import com.example.mzantsilingo.ui.lesson.LessonAdapter
import com.example.mzantsilingo.ui.profile.ProfileActivity
import com.example.mzantsilingo.ui.settings.SettingsActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var lessonAdapter: LessonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Open the Profile screen.
        binding.btnProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        // Open the Settings screen.
        binding.btnSettings.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }

        setupLessonsRecyclerView()
        loadLessons()
    }

    private fun setupLessonsRecyclerView() {

        // Create the lesson adapter.
        lessonAdapter = LessonAdapter(
            emptyList()
        ) { lesson ->

            // Open the selected lesson.
            val intent = Intent(
                this,
                LessonActivity::class.java
            )

            // Send the selected lesson ID to LessonActivity.
            intent.putExtra(
                "LESSON_ID",
                lesson.id
            )

            startActivity(intent)
        }

        // Set the RecyclerView layout manager.
        binding.rvLessons.layoutManager =
            LinearLayoutManager(this)

        // Connect the adapter to the RecyclerView.
        binding.rvLessons.adapter = lessonAdapter
    }

    private fun loadLessons() {

        lifecycleScope.launch {

            try {

                // Request the isiXhosa lessons from the API.
                val response =
                    RetrofitClient.apiService.getLessons("isiXhosa")

                if (response.isSuccessful && response.body() != null) {

                    val lessons = response.body()!!

                    // Update the RecyclerView with the lessons.
                    lessonAdapter.updateLessons(lessons)

                    if (lessons.isEmpty()) {

                        Toast.makeText(
                            this@HomeActivity,
                            "No lessons available.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this@HomeActivity,
                        "Unable to load lessons.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@HomeActivity,
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}