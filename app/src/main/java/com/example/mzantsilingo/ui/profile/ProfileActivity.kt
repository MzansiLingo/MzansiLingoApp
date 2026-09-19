package com.example.mzantsilingo.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.data.local.SettingsPreferences
import com.example.mzantsilingo.data.model.ActivityItem
import com.example.mzantsilingo.data.model.User
import com.example.mzantsilingo.data.repository.ProfileRepository
import com.example.mzantsilingo.databinding.ActivityProfileBinding
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val profileRepository = ProfileRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfile()
    }

    private fun loadProfile() {
        val userId = SettingsPreferences(this).userId
        if (userId == null) {
            Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            profileRepository.getUser(userId)
                .onSuccess { bindUser(it) }
                .onFailure {
                    Toast.makeText(this@ProfileActivity, it.message ?: "Failed to load profile", Toast.LENGTH_SHORT).show()
                }

            profileRepository.getRecentActivity(userId)
                .onSuccess { bindActivity(it) }
                .onFailure {
                    // Activity list is non-critical — fail quietly rather than blocking the profile
                }
        }
    }

    private fun bindUser(user: User) {
        binding.tvFullName.text = user.fullName
        binding.tvEmail.text = user.email
        binding.tvAvatarInitial.text = user.fullName.firstOrNull()?.uppercase() ?: "?"
        binding.tvTotalXp.text = user.totalXp.toString()
        binding.tvStreak.text = user.streakDays.toString()

        binding.tvLessonsCount.text = "—"
        binding.tvWordsCount.text = "—"
    }

    private fun bindActivity(items: List<ActivityItem>) {
        binding.llRecentActivity.removeAllViews()

        if (items.isEmpty()) {
            binding.llRecentActivity.addView(makeRow("No activity yet — start a lesson!"))
            return
        }

        items.forEach { item ->
            binding.llRecentActivity.addView(makeRow("✅ ${item.description}"))
        }
    }

    private fun makeRow(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 13f
            setPadding(0, 16, 0, 16)
            gravity = Gravity.CENTER_VERTICAL
            setTextColor(Color.parseColor("#333333"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }
}