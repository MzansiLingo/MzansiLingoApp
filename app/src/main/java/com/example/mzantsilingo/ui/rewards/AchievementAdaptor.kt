package com.example.mzantsilingo.ui.rewards

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mzantsilingo.data.model.Achievement
import com.example.mzantsilingo.databinding.ItemAchievementBinding

class AchievementAdapter(
    private var achievements: List<Achievement> = emptyList()
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    inner class AchievementViewHolder(val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]

        holder.binding.tvBadgeTitle.text = achievement.title

        if (achievement.isUnlocked) {
            holder.binding.tvBadgeIcon.text = iconForBadge(achievement.title)
            holder.binding.cardBadge.setCardBackgroundColor(Color.parseColor("#FFF8E1"))
            holder.binding.tvBadgeTitle.setTextColor(Color.parseColor("#000000"))
            holder.binding.root.alpha = 1.0f
        } else {
            holder.binding.tvBadgeIcon.text = "🔒"
            holder.binding.cardBadge.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
            holder.binding.tvBadgeTitle.setTextColor(Color.parseColor("#999999"))
            holder.binding.root.alpha = 0.6f
        }
    }

    override fun getItemCount(): Int = achievements.size

    fun updateData(newAchievements: List<Achievement>) {
        achievements = newAchievements
        notifyDataSetChanged()
    }

    private fun iconForBadge(title: String): String = when {
        title.contains("Word", ignoreCase = true) -> "📖"
        title.contains("Lesson", ignoreCase = true) -> "✅"
        title.contains("XP", ignoreCase = true) -> "⚡"
        title.contains("Quiz", ignoreCase = true) -> "🎯"
        title.contains("Explorer", ignoreCase = true) -> "🧭"
        else -> "🏅"
    }
}
