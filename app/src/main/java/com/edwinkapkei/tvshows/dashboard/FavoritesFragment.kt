package com.edwinkapkei.tvshows.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.edwinkapkei.tvshows.GetFavoriteShowsQuery
import com.edwinkapkei.tvshows.SessionManager
import com.edwinkapkei.tvshows.apolloClient
import com.edwinkapkei.tvshows.dashboard.adapters.FavoritesListAdapter
import com.edwinkapkei.tvshows.databinding.FragmentScheduleBinding

class FavoritesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    val shows = mutableListOf<GetFavoriteShowsQuery.Favorite>()
    val adapter = FavoritesListAdapter(shows)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.showRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.showRecycler.adapter = adapter
        binding.swipeRefreshLayout.setOnRefreshListener(this)

        adapter.onItemClicked = { show ->
            val intent = Intent(requireContext(), ShowDetails::class.java)
            intent.putExtra("id", show.id)
            intent.putExtra("name", show.name)
            intent.putExtra("premiered", show.premiered)
            intent.putExtra("summary", show.summary)
            intent.putExtra("rating", show.rating)
            intent.putExtra("genre", show.genres?.joinToString(separator = ", "))
            intent.putExtra("favorite", true)
            intent.putExtra("flag", 1)
            startActivity(intent)
        }

        loadItems()
    }

    private fun loadItems() {
        lifecycleScope.launchWhenResumed {
            val response = try {
                apolloClient.query(GetFavoriteShowsQuery(userId = SessionManager.getUserId(requireContext()))).toDeferred().await()
            } catch (e: ApolloException) {
                null
            }

            val newShows = response?.data?.favorites?.filterNotNull()
            binding.progressbar.visibility = View.GONE
            binding.swipeRefreshLayout.isRefreshing = false
            if (newShows != null) {
                if (newShows.isNotEmpty()) {
                    binding.emptyContainer.emptyItem.visibility = View.GONE
                    shows.clear()
                    shows.addAll(newShows)
                    adapter.notifyDataSetChanged()
                } else {
                    binding.emptyContainer.emptyItem.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRefresh() {
        loadItems()
    }
}