package com.example.streamify.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.streamify.MainActivity
import com.example.streamify.R
import com.example.streamify.databinding.FragmentVideosBinding
import com.example.streamify.model.getAllVideos
import com.example.streamify.view.activities.PlayerActivity
import com.example.streamify.view.adapters.VideoAdapter
import com.example.streamify.viewmodels.VideoViewModel


class VideosFragment : Fragment() {
    private lateinit var binding: FragmentVideosBinding
    private lateinit var videoViewModel: VideoViewModel
    lateinit var adapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireContext().theme.applyStyle(R.style.Theme_Streamify, true)
        binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoViewModel = ViewModelProvider(this).get(VideoViewModel::class.java)
        setupRecyclerView()
        setupObservers()
        setupListeners()

        videoViewModel.loadVideos(requireContext())
    }

    private fun setupRecyclerView() {
        binding.VideoRV.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = VideoAdapter(requireContext(), ArrayList())
        }
        adapter = binding.VideoRV.adapter as VideoAdapter
    }

    private fun setupObservers() {
        videoViewModel.videoList.observe(viewLifecycleOwner) { videos ->
            adapter.updateList(videos)
            binding.totalVideos.text = "Total Videos: ${videos.size}"
        }

        videoViewModel.searchList.observe(viewLifecycleOwner) { searchResults ->
            adapter.updateList(searchResults)
        }
    }

    private fun setupListeners() {
        binding.root.setOnRefreshListener {
            videoViewModel.loadVideos(requireContext())
            binding.root.isRefreshing = false
        }

        binding.nowPlayingBtn.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("class", "NowPlaying")
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_view, menu)
        val searchView = menu.findItem(R.id.searchView)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    videoViewModel.updateSearchList(newText)
                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.position != -1) binding.nowPlayingBtn.visibility = View.VISIBLE
    }
}