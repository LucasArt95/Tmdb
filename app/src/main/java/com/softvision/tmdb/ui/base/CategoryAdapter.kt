package com.softvision.tmdb.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.softvision.model.media.Movie
import com.softvision.model.media.TmdbItem
import com.softvision.model.media.TvShow
import com.softvision.tmdb.databinding.ItemTmdbEntryBinding


class CategoryAdapter(private val onItemClicked: (View, TmdbItem) -> Unit) : ListAdapter<TmdbItem, CategoryAdapter.TmdbItemViewHolder>(TmdbItemDiffCallback()) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TmdbItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTmdbEntryBinding.inflate(inflater, parent, false)
        return TmdbItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TmdbItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long {
        return when (val item = getItem(position)) {
            is Movie -> item.itemId
            is TvShow -> -item.itemId
            else -> super.getItemId(position)
        }
    }

    inner class TmdbItemViewHolder(private val binding: ItemTmdbEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cardView.setOnClickListener { view ->
                binding.viewData?.let { onItemClicked(view, it) }
            }
        }

        fun bind(item: TmdbItem) {
            binding.viewData = item
        }
    }

    class TmdbItemDiffCallback : DiffUtil.ItemCallback<TmdbItem>() {
        override fun areItemsTheSame(oldItem: TmdbItem, newItem: TmdbItem) =
            oldItem.itemId == newItem.itemId

        override fun areContentsTheSame(oldItem: TmdbItem, newItem: TmdbItem) =
            oldItem == newItem
    }
}

