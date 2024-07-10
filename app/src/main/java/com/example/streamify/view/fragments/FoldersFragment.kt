package com.example.streamify.view.fragments

import FolderViewModel
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamify.MainActivity
import com.example.streamify.R
import com.example.streamify.databinding.FragmentFoldersBinding
import com.example.streamify.view.adapters.FoldersAdapter


class FoldersFragment : Fragment() {
    private lateinit var binding: FragmentFoldersBinding
    private lateinit var viewModel: FolderViewModel
    private lateinit var folderAdapter: FoldersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireContext().theme.applyStyle(R.style.Theme_Streamify, true)
        binding = FragmentFoldersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FolderViewModel::class.java)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadFolders(requireContext())
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        folderAdapter = FoldersAdapter(requireContext(), ArrayList())
        binding.FoldersRV.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            layoutManager = gridLayoutManager
            adapter = folderAdapter
            addItemDecoration(SpacingItemDecoration(10))
        }
    }

    private fun observeViewModel() {
        viewModel.folderList.observe(viewLifecycleOwner) { folders ->
            folderAdapter.updateFolderList(folders)
            binding.totalFolders.text = "Total Folders: ${folders.size}"
        }
    }



    class SpacingItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.left = space
            outRect.right = space
            outRect.bottom = space
            if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1) {
                outRect.top = space
            }
        }
    }
}