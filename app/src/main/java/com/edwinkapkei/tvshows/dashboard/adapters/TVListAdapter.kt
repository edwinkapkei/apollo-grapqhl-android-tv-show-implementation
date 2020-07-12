package com.edwinkapkei.tvshows.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edwinkapkei.tvshows.R
import com.edwinkapkei.tvshows.databinding.TvItemBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class TVListAdapter : RecyclerView.Adapter<TVListAdapter.ViewHolder>() {

    private var list = JsonArray()
    var endOfListReached: (() -> Unit)? = null
    var onItemClicked: ((JsonObject) -> Unit)? = null

    class ViewHolder(val binding: TvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return list.size()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val show = list.get(position).asJsonObject
        if (position % 2 == 0) {
            holder.binding.title.text = "Wonder Woman: 1984(2020-"
            holder.binding.poster.setImageResource(R.drawable.wonder_woman)
        } else {
            holder.binding.title.text = "Under the Dome(2012-2015)"
            holder.binding.poster.setImageResource(R.drawable.under)
        }

    }

    fun updateList(list: JsonArray) {
        this.list = list
        notifyDataSetChanged()
    }
}