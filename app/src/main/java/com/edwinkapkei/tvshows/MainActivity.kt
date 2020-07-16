package com.edwinkapkei.tvshows

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.edwinkapkei.tvshows.dashboard.FavoritesFragment
import com.edwinkapkei.tvshows.dashboard.HomeFragment
import com.edwinkapkei.tvshows.dashboard.ScheduleFragment
import com.edwinkapkei.tvshows.dashboard.SearchActivity
import com.edwinkapkei.tvshows.dashboard.utils.BottomBarAdapter
import com.edwinkapkei.tvshows.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var pagerAdapter: BottomBarAdapter

    private val scheduleFragment: Fragment = ScheduleFragment()
    private val homeFragment: Fragment = HomeFragment.newInstance(1)
    private val favoritesFragment: Fragment = FavoritesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.app_name)
        }

        binding.viewpager.offscreenPageLimit = 2
        binding.viewpager.setPagingEnabled(false)

        pagerAdapter = BottomBarAdapter(supportFragmentManager)
        pagerAdapter.addFragments(homeFragment)
        pagerAdapter.addFragments(favoritesFragment)
        pagerAdapter.addFragments(scheduleFragment)

        binding.viewpager.adapter = pagerAdapter
        binding.viewpager.currentItem = 0
        binding.navigation.selectedItemId = R.id.navigation_home

        binding.navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    binding.viewpager.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_favorites -> {
                    binding.viewpager.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_schedule -> {
                    binding.viewpager.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView? = searchItem?.actionView as SearchView

        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                intent.putExtra("term", query)
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_logout){
            SessionManager().logout(this)
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}