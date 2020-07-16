package com.edwinkapkei.tvshows.dashboard

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.edwinkapkei.tvshows.*
import com.edwinkapkei.tvshows.dashboard.adapters.CrewAdapter
import com.edwinkapkei.tvshows.dashboard.adapters.GridItemDecoration
import com.edwinkapkei.tvshows.dashboard.adapters.SeasonAdapter
import com.edwinkapkei.tvshows.databinding.ActivityShowDetailsBinding
import com.edwinkapkei.tvshows.utilities.Utilities
import com.google.android.material.snackbar.Snackbar

class ShowDetails : AppCompatActivity() {

    private lateinit var binding: ActivityShowDetailsBinding
    private var showId: Int = 0
    private var addToFavorite: Boolean = false
    private var addToSchedule: Boolean = false

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

        showId = intent.getIntExtra("id", 1)


        binding.crew.layoutManager = GridLayoutManager(this@ShowDetails, 2)
        binding.crew.addItemDecoration(GridItemDecoration(20, 2))

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
                apolloClient.query(ShowDetailsQuery(showId = showId)).toDeferred().await()
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

        checkShowStatus()
    }

    private fun checkShowStatus() {
        lifecycleScope.launchWhenCreated {
            val query = CheckShowStatusQuery(userId = SessionManager.getUserId(this@ShowDetails), showId = showId.toString());
            val response = try {
                apolloClient.query(query).toDeferred().await()
            } catch (e: ApolloException) {
                null
            }

            if (response != null)
                if (response.data?.checkShowStatus?.success!!) {
                    addToFavorite = response.data?.checkShowStatus?.favorite!!
                    addToSchedule = response.data?.checkShowStatus?.scheduled!!
                    invalidateOptionsMenu()
                }
        }
    }

    private fun addToFavorites() {
        lifecycleScope.launchWhenCreated {
            val mutation = AddFavoriteMutation(userId = SessionManager.getUserId(this@ShowDetails), showId = showId.toString(), addToFavorites = !addToFavorite);
            val response = try {
                apolloClient.mutate(mutation).toDeferred().await()
            } catch (e: ApolloException) {
                null
            }

            if (response != null) {
                if (response.data?.addFavorite?.success!!) {
                    addToFavorite = response.data?.addFavorite?.flag!!
                }
                Snackbar.make(binding.rootLayout, response.data?.addFavorite?.message.toString(), Snackbar.LENGTH_SHORT).show()

            } else {
                addToFavorite = false
                Snackbar.make(binding.rootLayout, getString(R.string.trouble), Snackbar.LENGTH_LONG).show()
            }
            invalidateOptionsMenu()
        }
    }

    private fun addToSchedule() {
        lifecycleScope.launchWhenCreated {
            val mutation = AddScheduleMutation(userId = SessionManager.getUserId(this@ShowDetails), showId = showId.toString(), addToSchedule = !addToSchedule);
            val response = try {
                apolloClient.mutate(mutation).toDeferred().await()
            } catch (e: ApolloException) {
                null
            }

            if (response != null) {
                if (response.data?.addSchedule?.success!!) {
                    addToSchedule = response.data?.addSchedule?.flag!!
                }
                Snackbar.make(binding.rootLayout, response.data?.addSchedule?.message.toString(), Snackbar.LENGTH_SHORT).show()
            } else {
                addToSchedule = false
                Snackbar.make(binding.rootLayout, getString(R.string.trouble), Snackbar.LENGTH_LONG).show()
            }
            invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        if (addToFavorite) {
            menu?.getItem(0)?.setIcon(R.drawable.heart)
        } else {
            menu?.getItem(0)?.setIcon(R.drawable.heart_outline_white)
        }

        if (addToSchedule) {
            menu?.getItem(1)?.setIcon(R.drawable.calendar_check)
        } else {
            menu?.getItem(1)?.setIcon(R.drawable.calendar_plus)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.action_favorite -> {
                addToFavorites()
            }
            R.id.action_schedule -> {
                addToSchedule()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}