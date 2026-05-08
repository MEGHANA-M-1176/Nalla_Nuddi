package com.mindmatrix.nallanudi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindmatrix.nallanudi.data.Term
import com.mindmatrix.nallanudi.databinding.ItemFlashcardBinding

class FlashcardAdapter(
    private val onTtsClick: (String) -> Unit
) : RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>() {

    private var terms: List<Term> = emptyList()

    fun submitList(newTerms: List<Term>) {
        terms = newTerms
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val binding = ItemFlashcardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FlashcardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        holder.bind(terms[position])
    }

    override fun getItemCount(): Int = terms.size

    inner class FlashcardViewHolder(private val binding: ItemFlashcardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(term: Term) {
            binding.tvEnglishTerm.text = term.englishTerm
            binding.tvKannadaExplanation.text = term.kannadaExplanation
            binding.tvExample.text = term.example

            binding.btnTts.setOnClickListener {
                onTtsClick(term.englishTerm)
            }
        }
    }
}
