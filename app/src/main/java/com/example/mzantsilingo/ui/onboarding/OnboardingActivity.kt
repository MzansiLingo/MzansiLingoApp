package com.example.mzantsilingo.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mzantsilingo.R
import com.example.mzantsilingo.databinding.ActivityOnboardingBinding
import com.example.mzantsilingo.ui.auth.RegisterActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
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

        binding.btnNext.setOnClickListener {
            if (currentPage < titles.lastIndex) {
                currentPage++
                updatePage()
            } else {
                openRegister()
            }
        }

        // Skip the onboarding and go straight to the Register screen.
        // (After registering, the user logs in, and then reaches Home.)
        binding.btnSkip.setOnClickListener {
            openRegister()
        }
    }

    private fun updatePage() {
        binding.tvPageNumber.text =
            getString(R.string.page_of_format, currentPage + 1, titles.size)
        binding.tvTitle.setText(titles[currentPage])
        binding.tvDescription.setText(descriptions[currentPage])

        binding.btnNext.setText(
            if (currentPage == titles.lastIndex) R.string.register_title
            else R.string.onboarding_next
        )
    }

    private fun openRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }
}