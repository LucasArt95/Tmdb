package com.softvision.tmdb.ui.items.base.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.databinding.ItemGenreEntryBinding
import com.softvision.tmdb.ui.base.CategoryAdapter

class GenreAdapter<T : TmdbItem> :
    ListAdapter<GenreData<T>, GenreAdapter<T>.GenreItemViewHolder>(createCallback()) {

    private var genreAdapterListener: GenreAdapterListener? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGenreEntryBinding.inflate(inflater, parent, false)
        return GenreItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long =
        getItem(position).genre.id

    fun setGenreAdapterListener(genreAdapterListener: GenreAdapterListener) {
        this.genreAdapterListener = genreAdapterListener
    }

    inner class GenreItemViewHolder(private val binding: ItemGenreEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val adapter = CategoryAdapter { _, item ->
            genreAdapterListener?.onItemClicked(item)
        }

        init {
            binding.rvTmdbItems.adapter = adapter
            val linearLayoutManager =
                LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)
            binding.rvTmdbItems.layoutManager = linearLayoutManager
        }

        fun bind(item: GenreData<T>) {
            binding.tvGenreName.text = item.genre.name
            adapter.submitList(item.items)
        }
    }

    interface GenreAdapterListener {

        fun onItemClicked(tmdbItem: TmdbItem)
    }


    companion object {

        private fun <T : TmdbItem> createCallback(): DiffUtil.ItemCallback<GenreData<T>> {
            return object : DiffUtil.ItemCallback<GenreData<T>>() {
                override fun areItemsTheSame(
                    oldItem: GenreData<T>,
                    newItem: GenreData<T>,
                ): Boolean {
                    return oldItem.currentPage == newItem.currentPage && oldItem.genre.id == newItem.genre.id
                }

                override fun areContentsTheSame(
                    oldItem: GenreData<T>,
                    newItem: GenreData<T>,
                ): Boolean {
                    return oldItem == newItem
                }

            }
        }
    }

}
