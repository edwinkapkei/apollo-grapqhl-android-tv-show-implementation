package com.edwinkapkei.tvshows.dashboard

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.edwinkapkei.tvshows.R
import com.edwinkapkei.tvshows.ShowDetailsQuery
import com.edwinkapkei.tvshows.apolloClient
import com.edwinkapkei.tvshows.dashboard.adapters.CrewAdapter
import com.edwinkapkei.tvshows.dashboard.adapters.SeasonAdapter
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

        binding.crew.layoutManager = LinearLayoutManager(this@ShowDetails)
        binding.seasons.layoutManager = LinearLayoutManager(this@ShowDetails)

        val year = Utilities().stringDateToYear(intent.getStringExtra("premiered") ?: "")
        val name = intent.getStringExtra("name") ?: ""

        binding.title.text = name + " (${year})"
        binding.genre.text = intent.getStringExtra("genre") ?: ""
        binding.rating.text = intent.getStringExtra("rating") ?: ""
        binding.summary.text = android.text.Html.fromHtml(intent.getStringExtra("summary")
                ?: "").toString()

        lifecycleScope.launchWhenCreated {
            val response = try {
                apolloClient.query(ShowDetailsQuery(showId = intent.getIntExtra("id", 1))).toDeferred().await()
            } catch (e: ApolloException) {
                Log.e("ApolloException", "Failed", e)
                null
            }

            val show = response?.data?.show
            if (show != null) {
                binding.poster.load(show.image) {
                    placeholder(R.drawable.television_classic_blue)
                }

                if (show.seasons != null)
                    binding.seasons.adapter = SeasonAdapter(show.seasons as List<ShowDetailsQuery.Season>)

                if (show.crew != null)
                    binding.crew.adapter = CrewAdapter(show.crew as List<ShowDetailsQuery.Crew>)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        //menu?.getItem(1)?.setIcon(R.drawable.television_classic)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}