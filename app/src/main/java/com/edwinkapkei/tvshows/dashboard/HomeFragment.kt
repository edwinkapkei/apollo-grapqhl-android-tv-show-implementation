package com.edwinkapkei.tvshows.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.edwinkapkei.tvshows.ShowListQuery
import com.edwinkapkei.tvshows.apolloClient
import com.edwinkapkei.tvshows.dashboard.adapters.TVListAdapter
import com.edwinkapkei.tvshows.databinding.FragmentHomeBinding
import kotlinx.coroutines.channels.Channel


private const val FLAG_ARGUMENT = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var flag: Int? = 0
    private var page: Int = 0
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var list: ShowListQuery.Show? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            flag = it.getInt(FLAG_ARGUMENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putInt(FLAG_ARGUMENT, param1)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shows = mutableListOf<ShowListQuery.Show>()
        val adapter = TVListAdapter(shows)
        binding.showRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.showRecycler.adapter = adapter

        val channel = Channel<Unit>(Channel.CONFLATED)

        channel.offer(Unit)
        adapter.endOfListReached = {
            channel.offer(Unit)
        }

        lifecycleScope.launchWhenResumed {
            if (page == 1)
                binding.progressbar.visibility = View.VISIBLE
            for (item in channel) {
                val response = try {
                    apolloClient.query(ShowListQuery(page = page)).toDeferred().await()
                } catch (e: ApolloException) {
                    null
                }

                val newShows = response?.data?.shows?.filterNotNull()
                binding.progressbar.visibility = View.GONE
                if (newShows != null) {
                    shows.addAll(newShows)
                    adapter.notifyDataSetChanged()
                }

                page++
            }

            channel.close();
        }

        adapter.onItemClicked = { show ->
            val intent = Intent(requireContext(), ShowDetails::class.java)
            intent.putExtra("id", show.id)
            intent.putExtra("name", show.name)
            intent.putExtra("premiered", show.premiered)
            intent.putExtra("summary", show.summary)
            intent.putExtra("rating", show.rating)
            intent.putExtra("genre", show.genres?.joinToString(separator = ", "))
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}