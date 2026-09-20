package com.example.mzantsilingo.ui.lesson

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mzantsilingo.databinding.ActivityLessonCompleteBinding
import com.example.mzantsilingo.ui.home.HomeActivity

class LessonCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLessonCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLessonCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val wordsLearned =
            intent.getIntExtra("WORDS_LEARNED", 0)

        val xpEarned =
            intent.getIntExtra("XP_EARNED", 50)

        binding.tvXpEarned.text =
            "+$xpEarned XP"

        binding.tvWordsLearned.text =
            "$wordsLearned words learned"

        binding.btnContinueHome.setOnClickListener {

            val intent =
                Intent(this, HomeActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)

            finish()
        }
    }
}