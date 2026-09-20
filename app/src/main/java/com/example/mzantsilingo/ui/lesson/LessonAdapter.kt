package com.example.mzantsilingo.ui.lesson

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mzantsilingo.databinding.ItemLessonBinding
import com.example.mzantsilingo.data.model.Lesson
class LessonAdapter(
    private var lessons: List<Lesson>,
    private val onLessonClicked: (Lesson) -> Unit
) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    inner class LessonViewHolder(
        private val binding: ItemLessonBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lesson: Lesson) {
            binding.tvLessonTitle.text = lesson.title
            binding.tvLessonDescription.text = lesson.description

            binding.root.setOnClickListener {
                onLessonClicked(lesson)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LessonViewHolder {

        val binding = ItemLessonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LessonViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LessonViewHolder,
        position: Int
    ) {
        holder.bind(lessons[position])
    }

    override fun getItemCount(): Int = lessons.size

    fun updateLessons(newLessons: List<Lesson>) {
        lessons = newLessons
        notifyDataSetChanged()
    }
}