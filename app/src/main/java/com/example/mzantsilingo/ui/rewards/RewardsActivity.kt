package com.example.mzantsilingo.ui.rewards

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mzantsilingo.data.local.SettingsPreferences
import com.example.mzantsilingo.data.repository.RewardsRepository
import com.example.mzantsilingo.databinding.ActivityRewardsBinding
import kotlinx.coroutines.launch

class RewardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardsBinding
    private val rewardsRepository = RewardsRepository()
    private val adapter = AchievementAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvAchievements.layoutManager = GridLayoutManager(this, 3)
        binding.rvAchievements.adapter = adapter

        loadAchievements()
    }

    private fun loadAchievements() {
        val userId = SettingsPreferences(this).userId
        if (userId == null) {
            Toast.makeText(this, "Please log in to view rewards", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.loadingIndicator.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = rewardsRepository.getAchievements(userId)
            binding.loadingIndicator.visibility = View.GONE

            result.onSuccess { achievements ->
                adapter.updateData(achievements)
            }.onFailure {
                Toast.makeText(this@RewardsActivity, it.message ?: "Failed to load rewards", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
