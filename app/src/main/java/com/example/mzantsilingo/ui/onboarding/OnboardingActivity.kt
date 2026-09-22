package com.example.mzantsilingo.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mzantsilingo.R
import com.example.mzantsilingo.databinding.ActivityOnboardingBinding
import com.example.mzantsilingo.ui.auth.RegisterActivity

class OnboardingActivity : AppCompatActivity() {

    // Gives us access to the views on the onboarding screen.
    private lateinit var binding: ActivityOnboardingBinding

    // Keeps track of which onboarding page the user is currently viewing.
    private var currentPage = 0

    // The title shown on each onboarding page.
    private val titles = arrayOf(
        "Welcome to MzansiLingo",
        "Learn isiXhosa",
        "Practice Every Day",
        "Start Your Journey"
    )

    // The description shown below each page title.
    private val descriptions = arrayOf(
        "Learn isiXhosa through simple lessons, practice and interactive exercises.",
        "Build your vocabulary with useful words and phrases that you can use in everyday conversations.",
        "Complete interactive exercises and build your knowledge step by step.",
        "Create your account and start learning isiXhosa with MzansiLingo."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the onboarding screen using View Binding.
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show the first onboarding page when the screen opens.
        updatePage()

        // Move to the next page when the Next button is pressed.
        binding.btnNext.setOnClickListener {

            // Check if there are still more onboarding pages to show.
            if (currentPage < titles.lastIndex) {
                currentPage++
                updatePage()
            } else {

                // Open registration after the last onboarding page.
                openRegister()
            }
        }

        // Allow the user to skip the onboarding and go straight to registration.
        binding.btnSkip.setOnClickListener {
            openRegister()
        }
    }

    // Updates the screen with the information for the current page.
    private fun updatePage() {
        binding.tvPageNumber.text = "${currentPage + 1} of ${titles.size}"
        binding.tvTitle.text = titles[currentPage]
        binding.tvDescription.text = descriptions[currentPage]

        // Change the button text on the final page.
        binding.btnNext.text =
            if (currentPage == titles.lastIndex) {
                "Create Account"
            } else {
                "Next"
            }
    }

    // Takes the user to the registration screen.
    private fun openRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }
}