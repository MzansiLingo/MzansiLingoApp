
package com.example.mzantsilingo.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.data.local.UserSession
import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.example.mzantsilingo.data.repository.OfflineRepository
import com.example.mzantsilingo.databinding.ActivityProfileBinding
import com.example.mzantsilingo.ui.home.HomeActivity
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val offlineRepository by lazy {
        OfflineRepository(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Return to the Home screen.
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = UserSession.getUserId(this)

        if (userId == null) {
            Toast.makeText(
                this,
                "User session not found.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        lifecycleScope.launch {
            // Load the completed lesson count from the local Room database.
            try {
                val completedCount =
                    offlineRepository.getCompletedLessonCount(userId)

                binding.tvCompletedLessons.text = completedCount.toString()
            } catch (e: Exception) {
                binding.tvCompletedLessons.text = "0"
            }

            // Load the user's profile details from the backend.
            try {
                val response = RetrofitClient.apiService.getUser(userId)

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!

                    binding.tvFullName.text = user.fullName
                    binding.tvUsername.text = "@${user.username}"
                    binding.tvEmail.text = user.email
                    binding.tvTotalXp.text = user.totalXp.toString()
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Unable to load profile.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ProfileActivity,
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
