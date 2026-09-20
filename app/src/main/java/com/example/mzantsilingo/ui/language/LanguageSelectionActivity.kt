package com.example.mzantsilingo.ui.language

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mzantsilingo.R
import com.example.mzantsilingo.databinding.ActivityLanguageSelectionBinding
import com.example.mzantsilingo.ui.home.HomeActivity

class LanguageSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageSelectionBinding

    private var selectedLanguage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIsiXhosa.setOnClickListener {
            selectedLanguage = "isiXhosa"

            Toast.makeText(
                this,
                "isiXhosa selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnEnglish.setOnClickListener {
            selectedLanguage = "English"

            Toast.makeText(
                this,
                "English selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnContinue.setOnClickListener {

            if (selectedLanguage.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please select a language",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Continue to the main application.
            startActivity(
                Intent(this, HomeActivity::class.java)
            )

            finish()
        }
    }
}