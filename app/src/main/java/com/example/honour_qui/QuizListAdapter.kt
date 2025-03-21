package com.example.honour_qui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.honour_qui.databinding.ItemQuizQuestionBinding

class QuizListAdapter(
    private val questions: List<QuestionModel>,
    private val onAnswerSelected: (Int, String) -> Unit
) : RecyclerView.Adapter<QuizListAdapter.QuizViewHolder>() {

    inner class QuizViewHolder(private val binding: ItemQuizQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: QuestionModel, position: Int) {
            binding.questionText.text = question.question

            val options = listOf(binding.btn0, binding.btn1, binding.btn2, binding.btn3)

            options.forEachIndexed { index, button ->
                button.text = question.options[index]
                button.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.Gray)
                )
                button.setOnClickListener {
                    onAnswerSelected(position, button.text.toString())
                    resetButtonColors(options)
                    button.setBackgroundColor(
                        ContextCompat.getColor(binding.root.context, R.color.Yellow)
                    )
                }
            }
        }

        private fun resetButtonColors(buttons: List<Button>) {
            buttons.forEach {
                it.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.Gray)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = ItemQuizQuestionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(questions[position], position)
    }

    override fun getItemCount(): Int = questions.size
}
