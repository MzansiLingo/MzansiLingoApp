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

    private val titles = arrayOf(
        "Welcome to MzansiLingo",
        "Learn isiXhosa",
        "Practice Every Day",
        "Start Your Journey"
    )

    private val descriptions = arrayOf(
        "Learn isiXhosa through simple lessons, practice and interactive exercises.",
        "Build your vocabulary with useful words and phrases that you can use in everyday conversations.",
        "Complete interactive exercises and build your knowledge step by step.",
        "Create your account and start learning isiXhosa with MzansiLingo."
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

        binding.btnSkip.setOnClickListener {
            openRegister()
        }
    }

    private fun updatePage() {
        binding.tvPageNumber.text = "${currentPage + 1} of ${titles.size}"
        binding.tvTitle.text = titles[currentPage]
        binding.tvDescription.text = descriptions[currentPage]

        binding.btnNext.text =
            if (currentPage == titles.lastIndex) {
                "Create Account"
            } else {
                "Next"
            }
    }

    private fun openRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }
}