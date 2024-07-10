package com.example.streamify.view.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.streamify.databinding.FoldersViewBinding
import com.example.streamify.model.Folder
import com.example.streamify.view.activities.FoldersActivity


class FoldersAdapter(private val context: Context, private var foldersList: ArrayList<Folder>) :
    RecyclerView.Adapter<FoldersAdapter.MyHolder>() {

    class MyHolder(binding: FoldersViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val folderName = binding.folderNameFV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(FoldersViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val folder = foldersList[position]
        holder.folderName.text = folder.folderName
        holder.root.setOnClickListener {
            val intent = Intent(context, FoldersActivity::class.java)
            intent.putExtra("folderId", folder.id)  // Pass folder ID instead of position
            intent.putExtra("folderName", folder.folderName)  // Pass folder name for the title
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int = foldersList.size

    fun updateFolderList(newList: ArrayList<Folder>) {
        foldersList = newList
        notifyDataSetChanged()
    }
}