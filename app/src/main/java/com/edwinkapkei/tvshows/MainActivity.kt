package com.edwinkapkei.tvshows

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.edwinkapkei.tvshows.dashboard.HomeFragment
import com.edwinkapkei.tvshows.dashboard.ScheduleFragment
import com.edwinkapkei.tvshows.dashboard.utils.BottomBarAdapter
import com.edwinkapkei.tvshows.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var pagerAdapter: BottomBarAdapter

    private val scheduleFragment: Fragment = ScheduleFragment()
    private val homeFragment: Fragment = HomeFragment.newInstance(1)
    private val favoritesFragment: Fragment = HomeFragment.newInstance(2)

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
}