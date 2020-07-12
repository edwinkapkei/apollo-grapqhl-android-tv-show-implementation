package com.edwinkapkei.tvshows.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.edwinkapkei.tvshows.dashboard.adapters.GridItemDecoration
import com.edwinkapkei.tvshows.dashboard.adapters.TVListAdapter
import com.edwinkapkei.tvshows.databinding.FragmentHomeBinding
import com.google.gson.JsonArray

private const val FLAG_ARGUMENT = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private var flag: Int? = 0
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var adapter: TVListAdapter = TVListAdapter()

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

        binding.showRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.showRecycler.addItemDecoration(GridItemDecoration(20, 2))
        binding.showRecycler.adapter = adapter

        val list = JsonArray()
        list.add(1)
        list.add(1)
        list.add(1)
        list.add(1)
        list.add(1)
        list.add(1)
        list.add(1)
        list.add(1)
        adapter.updateList(list)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}