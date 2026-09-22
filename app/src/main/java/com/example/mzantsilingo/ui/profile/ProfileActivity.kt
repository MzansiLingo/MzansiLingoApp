package com.example.mzantsilingo.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.data.local.UserSession
import com.example.mzantsilingo.data.model.api.RetrofitClient
import com.example.mzantsilingo.databinding.ActivityProfileBinding
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserProfile()
    }

    private fun loadUserProfile() {

        // Get the ID of the currently logged-in user.
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
            try {

                // Request the user's profile from the backend.
                val response = RetrofitClient.apiService.getUser(userId)

                if (response.isSuccessful && response.body() != null) {

                    val user = response.body()!!

                    // Display the user's information.
                    binding.tvFullName.text = user.fullName
                    binding.tvUsername.text = "@${user.username}"
                    binding.tvEmail.text = user.email

                    // Display the user's XP.
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