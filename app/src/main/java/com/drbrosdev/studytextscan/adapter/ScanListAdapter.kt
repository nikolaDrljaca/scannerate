package com.drbrosdev.studytextscan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drbrosdev.studytextscan.databinding.ScanListItemBinding
import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.util.dateAsString

class ScanListAdapter(private val onItemClicked: (Scan) -> Unit) :
    ListAdapter<Scan, ScanListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ScanListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    //use viewBinding
    inner class ViewHolder(private val binding: ScanListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.card.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(absoluteAdapterPosition))
                }
            }
        }

        fun bind(scan: Scan) {
            binding.apply {
                textViewDate.text = scan.dateCreated
                textViewContent.text = scan.scanText
                textViewTitle.text = scan.scanText.lines()[0]
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Scan>() {
        override fun areItemsTheSame(oldItem: Scan, newItem: Scan) =
            (oldItem.scanId) == (newItem.scanId)

        override fun areContentsTheSame(oldItem: Scan, newItem: Scan) =
            oldItem == newItem
    }
}