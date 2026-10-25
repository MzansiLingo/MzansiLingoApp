package com.example.mzantsilingo.ui.lesson

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mzantsilingo.R
import com.example.mzantsilingo.databinding.ActivityLessonCompleteBinding
import com.example.mzantsilingo.ui.home.HomeActivity

class LessonCompleteActivity : AppCompatActivity() {

    // Gives us access to the views on the lesson completion screen.
    private lateinit var binding: ActivityLessonCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLessonCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the number of words learned and the XP earned from the previous screen.
        val wordsLearned = intent.getIntExtra("WORDS_LEARNED", 0)
        val xpEarned = intent.getIntExtra("XP_EARNED", 50)

        // Show the XP earned and how many words the user learned.
        binding.tvXpEarned.text = "+$xpEarned XP"
        binding.tvWordsLearned.text = getString(R.string.words_learned_format, wordsLearned)

        // Take the user back to the Home screen.
        binding.btnContinueHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)

            // Clear the previous screens and return to the existing Home screen.
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)
            finish()
        }
    }
}
