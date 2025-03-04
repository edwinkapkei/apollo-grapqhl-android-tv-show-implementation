package com.edwinkapkei.tvshows.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.edwinkapkei.tvshows.R
import com.edwinkapkei.tvshows.SearchQuery
import com.edwinkapkei.tvshows.databinding.TvItemBinding
import com.edwinkapkei.tvshows.utilities.Utilities

class SearchListAdapter(private var searchResults: List<SearchQuery.Search>) : RecyclerView.Adapter<SearchListAdapter.ViewHolder>() {

    var endOfListReached: (() -> Unit)? = null
    var onItemClicked: ((SearchQuery.Search) -> Unit)? = null

    class ViewHolder(val binding: TvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val show = searchResults[position]
        val year = Utilities().stringDateToYear(show.premiered ?: "")
        val name = show.name ?: ""
        holder.binding.title.text = name + " (${year})"

        holder.binding.poster.load(show.image) {
            crossfade(true)
            placeholder(R.drawable.television_classic_blue)
        }
        val summary = show.summary ?: ""
        holder.binding.summary.text = android.text.Html.fromHtml(summary).toString()
        holder.binding.rating.text = show.rating ?: "-"

        holder.binding.genre.text = show.genres?.joinToString(separator = ", ")

        holder.binding.root.setOnClickListener {
            onItemClicked?.invoke(show)
        }
    }

    fun updateList(list: List<SearchQuery.Search>) {
        searchResults = list
        notifyDataSetChanged()
    }
}