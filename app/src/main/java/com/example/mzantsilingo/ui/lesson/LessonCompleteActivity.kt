package com.example.mzantsilingo.ui.lesson

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mzantsilingo.databinding.ActivityLessonCompleteBinding
import com.example.mzantsilingo.ui.home.HomeActivity

class LessonCompleteActivity : AppCompatActivity() {

    // Gives us access to the views on the lesson completion screen.
    private lateinit var binding: ActivityLessonCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the lesson completion screen using View Binding.
        binding = ActivityLessonCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the number of words learned from the previous screen.
        val wordsLearned =
            intent.getIntExtra("WORDS_LEARNED", 0)

        // Get the amount of XP earned from the previous screen.
        val xpEarned =
            intent.getIntExtra("XP_EARNED", 50)

        // Show the XP earned by the user.
        binding.tvXpEarned.text =
            "+$xpEarned XP"

        // Show how many words the user learned.
        binding.tvWordsLearned.text =
            "$wordsLearned words learned"

        // Take the user back to the Home screen.
        binding.btnContinueHome.setOnClickListener {

            val intent =
                Intent(this, HomeActivity::class.java)

            // Clear the previous screens and return to the existing Home screen.
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)

            // Close the lesson completion screen.
            finish()
        }
    }
}