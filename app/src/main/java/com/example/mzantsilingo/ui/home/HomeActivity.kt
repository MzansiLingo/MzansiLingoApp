package com.example.mzantsilingo.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzantsilingo.R
import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.example.mzantsilingo.databinding.ActivityHomeBinding
import com.example.mzantsilingo.ui.lesson.LessonActivity
import com.example.mzantsilingo.ui.lesson.LessonAdapter
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var lessonAdapter: LessonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLessonsRecyclerView()
        loadLessons()
    }

    private fun setupLessonsRecyclerView() {

        lessonAdapter = LessonAdapter(
            emptyList()
        ) { lesson ->

            // Open the selected lesson.
            val intent = Intent(
                this,
                LessonActivity::class.java
            )

            intent.putExtra("LESSON_ID", lesson.id)

            startActivity(intent)
        }

        binding.rvLessons.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = lessonAdapter
        }
    }

    private fun loadLessons() {

        lifecycleScope.launch {

            try {

                // Request isiXhosa lessons from the REST API.
                val response = RetrofitClient.apiService
                    .getLessons("isiXhosa")

                if (response.isSuccessful && response.body() != null) {

                    val lessons = response.body()!!

                    lessonAdapter.updateLessons(lessons)

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
                    "Could not connect to the server.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}