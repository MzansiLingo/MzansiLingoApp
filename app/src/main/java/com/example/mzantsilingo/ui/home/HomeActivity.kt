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

/**
 * Home screen of the MzansiLingo application.
 *
 * Displays the user's main dashboard and available language lessons.
 */
class HomeActivity : AppCompatActivity() {

    // View binding used to access the views defined in activity_home.xml.
    private lateinit var binding: ActivityHomeBinding

    // Adapter used to display lessons inside the RecyclerView.
    private lateinit var lessonAdapter: LessonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding.
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Open the Profile screen when the profile button is clicked.
        binding.btnProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        // Open the Settings screen when the settings button is clicked.
        binding.btnSettings.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }

        // Configure the lessons RecyclerView.
        setupLessonsRecyclerView()

        // Retrieve the available lessons from the REST API.
        loadLessons()
    }

    /**
     * Sets up the RecyclerView used to display available lessons.
     */
    private fun setupLessonsRecyclerView() {

        // Create the lesson adapter with an initially empty list.
        // The lambda is called when the user selects a lesson.
        lessonAdapter = LessonAdapter(
            emptyList()
        ) { lesson ->

            // Create an Intent to open the selected lesson.
            val intent = Intent(
                this,
                LessonActivity::class.java
            )

            // Pass the selected lesson ID to LessonActivity.
            intent.putExtra(
                "LESSON_ID",
                lesson.id
            )

            // Start the lesson screen.
            startActivity(intent)
        }

        // Set the layout manager that controls how lessons are arranged.
        binding.rvLessons.layoutManager =
            LinearLayoutManager(this)

        // Connect the lesson adapter to the RecyclerView.
        binding.rvLessons.adapter = lessonAdapter
    }

    /**
     * Loads the available isiXhosa lessons from the REST API.
     */
    private fun loadLessons() {

        // Launch the API request inside a lifecycle-aware coroutine.
        lifecycleScope.launch {

            try {

                // Request isiXhosa lessons from the REST API.
                val response =
                    RetrofitClient.apiService.getLessons("isiXhosa")

                // Check that the API request was successful
                // and that a response body was returned.
                if (response.isSuccessful && response.body() != null) {

                    // Retrieve the list of lessons from the response.
                    val lessons = response.body()!!

                    // Update the RecyclerView with the lessons received from the API.
                    lessonAdapter.updateLessons(lessons)

                    // Inform the user if the API returned no lessons.
                    if (lessons.isEmpty()) {

                        Toast.makeText(
                            this@HomeActivity,
                            "No lessons available.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {

                    // Display an error message when the API request fails.
                    Toast.makeText(
                        this@HomeActivity,
                        "Unable to load lessons.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                // Display a connection error if the API request
                // throws an exception.
                Toast.makeText(
                    this@HomeActivity,
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}