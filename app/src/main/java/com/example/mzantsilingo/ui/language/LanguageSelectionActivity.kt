package com.example.mzantsilingo.ui.language

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mzantsilingo.R
import com.example.mzantsilingo.databinding.ActivityLanguageSelectionBinding
import com.example.mzantsilingo.ui.home.HomeActivity

/**
 * Allows the user to select the language they want to learn.
 *
 * The selected language is stored temporarily and the user
 * can continue to the main application after making a selection.
 */
class LanguageSelectionActivity : AppCompatActivity() {

    // View binding provides access to the views in activity_language_selection.xml.
    private lateinit var binding: ActivityLanguageSelectionBinding

    // Stores the language selected by the user.
    // An empty value means that no language has been selected yet.
    private var selectedLanguage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the language selection layout using View Binding.
        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle selection of isiXhosa.
        binding.btnIsiXhosa.setOnClickListener {
            selectedLanguage = "isiXhosa"

            // Inform the user that isiXhosa has been selected.
            Toast.makeText(
                this,
                "isiXhosa selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Handle selection of English.
        binding.btnEnglish.setOnClickListener {
            selectedLanguage = "English"

            // Inform the user that English has been selected.
            Toast.makeText(
                this,
                "English selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Handle the Continue button.
        binding.btnContinue.setOnClickListener {

            // Make sure the user has selected a language before continuing.
            if (selectedLanguage.isEmpty()) {

                // Ask the user to select a language.
                Toast.makeText(
                    this,
                    "Please select a language",
                    Toast.LENGTH_SHORT
                ).show()

                // Stop the click event so the user remains on this screen.
                return@setOnClickListener
            }

            // Continue to the main application.
            startActivity(
                Intent(this, HomeActivity::class.java)
            )

            // Close the language selection screen so it is not
            // displayed again when the user presses the Back button.
            finish()
        }
    }
}