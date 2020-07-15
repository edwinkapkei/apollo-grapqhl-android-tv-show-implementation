package com.edwinkapkei.tvshows.dashboard

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.edwinkapkei.tvshows.SearchQuery
import com.edwinkapkei.tvshows.apolloClient
import com.edwinkapkei.tvshows.dashboard.adapters.SearchListAdapter
import com.edwinkapkei.tvshows.databinding.ActivitySearchBinding
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Search"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        binding.progressbar.visibility = View.GONE

        val term: String = intent.getStringExtra("term") ?: ""

        if (term.isNotEmpty()) {
            if (supportActionBar != null) {
                supportActionBar!!.title = term
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            }
            searchShow(term)
        }

        binding.showRecycler.layoutManager = LinearLayoutManager(this)
    }

    private fun searchShow(term: String) {
        lifecycleScope.launch {
            binding.progressbar.visibility = View.VISIBLE
            binding.showRecycler.visibility = View.GONE

            val response = try {
                apolloClient.query(SearchQuery(name = term)).toDeferred().await()
            } catch (e: ApolloException) {
                Log.e("ApolloException", "Failed", e)
                null
            }

            binding.progressbar.visibility = View.GONE
            binding.showRecycler.visibility = View.VISIBLE
            val searchResult = response?.data?.search?.filterNotNull()
            if (searchResult != null) {
                val adapter = SearchListAdapter(searchResult)
                binding.showRecycler.adapter = adapter
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

}