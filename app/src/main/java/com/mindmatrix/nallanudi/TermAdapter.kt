package com.mindmatrix.nallanudi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindmatrix.nallanudi.data.Term
import com.mindmatrix.nallanudi.databinding.ItemTermBinding

class TermAdapter(
    private val onTtsClick: (String) -> Unit,
    private val onSaveClick: (Term) -> Unit
) : RecyclerView.Adapter<TermAdapter.TermViewHolder>() {

    private var terms: List<Term> = emptyList()

    fun submitList(newTerms: List<Term>) {
        terms = newTerms
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermViewHolder {
        val binding = ItemTermBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TermViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TermViewHolder, position: Int) {
        holder.bind(terms[position])
    }

    override fun getItemCount(): Int = terms.size

    inner class TermViewHolder(private val binding: ItemTermBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(term: Term) {
            binding.tvEnglishTerm.text = term.englishTerm
            binding.tvKannadaExplanation.text = term.kannadaExplanation
            binding.tvExample.text = term.example
            binding.tvSubject.text = term.subject
            
            val starIcon = if (term.isSaved) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
            binding.btnSave.setImageResource(starIcon)

            binding.btnTts.setOnClickListener {
                onTtsClick(term.englishTerm)
            }

            binding.btnSave.setOnClickListener {
                onSaveClick(term)
            }
        }
    }
}
