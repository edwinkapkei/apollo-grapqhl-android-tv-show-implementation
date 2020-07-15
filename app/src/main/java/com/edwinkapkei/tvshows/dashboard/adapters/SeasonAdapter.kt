package com.edwinkapkei.tvshows.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edwinkapkei.tvshows.ShowDetailsQuery
import com.edwinkapkei.tvshows.databinding.SeasonItemBinding

class SeasonAdapter(private var seasons: List<ShowDetailsQuery.Season>) : RecyclerView.Adapter<SeasonAdapter.ViewHolder>() {

    class ViewHolder(val binding: SeasonItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return seasons.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SeasonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val season = seasons[position]

        holder.binding.season.text = "Season " + season.number.toString() + ": " + season.episodes.toString() + " episodes"
    }
}