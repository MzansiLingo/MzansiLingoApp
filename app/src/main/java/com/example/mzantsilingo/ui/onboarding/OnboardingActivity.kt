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

    // String resource IDs (not text) so the text follows the app language.
    private val titles = intArrayOf(
        R.string.onboarding_title_1,
        R.string.onboarding_title_2,
        R.string.onboarding_title_3,
        R.string.onboarding_title_4
    )

    private val descriptions = intArrayOf(
        R.string.onboarding_desc_1,
        R.string.onboarding_desc_2,
        R.string.onboarding_desc_3,
        R.string.onboarding_desc_4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updatePage()

        // Move to the next page when the Next button is pressed.
        binding.btnNext.setOnClickListener {
            if (currentPage < titles.lastIndex) {
                currentPage++
                updatePage()
            } else {
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
        binding.tvPageNumber.text =
            getString(R.string.page_of_format, currentPage + 1, titles.size)
        binding.tvTitle.setText(titles[currentPage])
        binding.tvDescription.setText(descriptions[currentPage])

        // Change the button text on the final page.
        binding.btnNext.setText(
            if (currentPage == titles.lastIndex) R.string.register_title
            else R.string.onboarding_next
        )
    }

    // Takes the user to the registration screen.
    private fun openRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }
}
