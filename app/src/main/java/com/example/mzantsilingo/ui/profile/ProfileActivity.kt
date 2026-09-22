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

    // Gives us access to the views on the profile screen.
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the profile screen using View Binding.
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the details of the logged-in user.
        loadUserProfile()
    }

    // Gets the user's details from the backend and displays them on the screen.
    private fun loadUserProfile() {

        // Get the ID of the currently logged-in user.
        val userId = UserSession.getUserId(this)

        // Stop if there is no active user session.
        if (userId == null) {
            Toast.makeText(
                this,
                "User session not found.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Run the API request in a coroutine so it doesn't block the app.
        lifecycleScope.launch {

            try {

                // Ask the backend for the user's profile information.
                val response = RetrofitClient.apiService.getUser(userId)

                // Check that the request was successful and returned user data.
                if (response.isSuccessful && response.body() != null) {

                    val user = response.body()!!

                    // Show the user's basic information on the profile screen.
                    binding.tvFullName.text = user.fullName
                    binding.tvUsername.text = "@${user.username}"
                    binding.tvEmail.text = user.email

                    // Show the user's total XP.
                    binding.tvTotalXp.text = user.totalXp.toString()

                } else {

                    // Let the user know if the profile couldn't be loaded.
                    Toast.makeText(
                        this@ProfileActivity,
                        "Unable to load profile.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                // Show an error if the app can't connect to the backend.
                Toast.makeText(
                    this@ProfileActivity,
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}