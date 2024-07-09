package com.example.streamify.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.streamify.MainActivity
import com.example.streamify.R
import com.example.streamify.databinding.FragmentFoldersBinding
import com.example.streamify.view.adapters.FoldersAdapter


class FoldersFragment : Fragment() {

    private lateinit var binding: FragmentFoldersBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireContext().theme.applyStyle(MainActivity.themesList[MainActivity.themeIndex], true)
        binding = FragmentFoldersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.FoldersRV.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = FoldersAdapter(requireContext(), MainActivity.folderList)
        }
        binding.totalFolders.text = "Total Folders: ${MainActivity.folderList.size}"
    }
}