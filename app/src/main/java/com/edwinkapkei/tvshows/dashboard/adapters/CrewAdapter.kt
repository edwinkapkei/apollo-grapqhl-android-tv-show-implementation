package com.edwinkapkei.tvshows.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.edwinkapkei.tvshows.R
import com.edwinkapkei.tvshows.ShowDetailsQuery
import com.edwinkapkei.tvshows.databinding.CrewItemBinding
import com.google.gson.JsonObject

class CrewAdapter(private var crew: List<ShowDetailsQuery.Crew>) : RecyclerView.Adapter<CrewAdapter.ViewHolder>() {

    class ViewHolder(val binding: CrewItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return crew.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CrewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = crew[position]

        holder.binding.name.text = member.person?.name

        holder.binding.poster.load(member.person?.image) {
            placeholder(R.drawable.ic_baseline_person_24)
        }

        holder.binding.role.text = member.type
    }
}