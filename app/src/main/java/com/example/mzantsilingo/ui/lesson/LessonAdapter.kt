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

    // Holds the views for each lesson item.
    inner class LessonViewHolder(
        private val binding: ItemLessonBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // Put the lesson information into the item layout.
        fun bind(lesson: Lesson) {
            binding.tvLessonTitle.text = lesson.title
            binding.tvLessonDescription.text = lesson.description

            // Open the selected lesson when the user taps the item.
            binding.root.setOnClickListener {
                onLessonClicked(lesson)
            }
        }
    }

    // Create a new lesson item using the item_lesson layout.
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

    // Bind the lesson at the current position to the ViewHolder.
    override fun onBindViewHolder(
        holder: LessonViewHolder,
        position: Int
    ) {
        holder.bind(lessons[position])
    }

    // Return the number of lessons that need to be displayed.
    override fun getItemCount(): Int = lessons.size

    // Replace the current lessons with the new list from the API.
    fun updateLessons(newLessons: List<Lesson>) {
        lessons = newLessons

        // Refresh the RecyclerView so the new lessons are displayed.
        notifyDataSetChanged()
    }
}