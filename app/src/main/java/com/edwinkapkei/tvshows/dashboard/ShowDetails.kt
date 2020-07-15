package com.edwinkapkei.tvshows.dashboard

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo.exception.ApolloException
import com.edwinkapkei.tvshows.R
import com.edwinkapkei.tvshows.ShowDetailsQuery
import com.edwinkapkei.tvshows.apolloClient
import com.edwinkapkei.tvshows.databinding.ActivityShowDetailsBinding
import com.edwinkapkei.tvshows.utilities.Utilities

class ShowDetails : AppCompatActivity() {

    private lateinit var binding: ActivityShowDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Show Details"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        val year = Utilities().stringDateToYear(intent.getStringExtra("premiered") ?: "")
        val name = intent.getStringExtra("name") ?: ""

        binding.title.text = name + " (${year})"
        binding.genre.text = intent.getStringExtra("genre") ?: ""
        binding.rating.text = intent.getStringExtra("rating") ?: ""
        binding.summary.text = android.text.Html.fromHtml(intent.getStringExtra("summary")
                ?: "").toString()

        lifecycleScope.launchWhenCreated {
            val response = try {
                apolloClient.query(ShowDetailsQuery(showId = intent.getIntExtra("id", 1)))
            } catch (e: ApolloException) {
                Log.e("ApolloException", "Failed", e)
                null
            }

            if (response != null) {

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