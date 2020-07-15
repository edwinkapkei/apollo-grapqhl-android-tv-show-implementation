package com.edwinkapkei.tvshows

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
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
            binding.search.setText(term)
            searchShow(term)
        }

        binding.showRecycler.layoutManager = LinearLayoutManager(this)

        binding.search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.search.hideKeyboard()
                searchShow(v.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
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

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }
}