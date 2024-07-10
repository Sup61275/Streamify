package com.example.streamify.viewmodels

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streamify.MainActivity
import com.example.streamify.model.Folder
import com.example.streamify.model.Video
import com.example.streamify.model.getAllVideos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class VideoViewModel : ViewModel() {
    private val _videoList = MutableLiveData<ArrayList<Video>>()
    val videoList: LiveData<ArrayList<Video>> = _videoList

    private val _folderList = MutableLiveData<ArrayList<Folder>>()
    val folderList: LiveData<ArrayList<Folder>> = _folderList

    private val _searchList = MutableLiveData<ArrayList<Video>>()
    val searchList: LiveData<ArrayList<Video>> = _searchList

    var sortValue = 0

    fun loadVideos(context: Context) {
        _videoList.value = getAllVideos(context)
        _folderList.value = MainActivity.folderList
    }

    fun updateSearchList(query: String) {
        val filteredList = ArrayList<Video>()
        _videoList.value?.let { videos ->
            for (video in videos) {
                if (video.title.lowercase().contains(query.lowercase())) {
                    filteredList.add(video)
                }
            }
        }
        _searchList.value = filteredList
    }

    fun deleteVideo(position: Int) {
        _videoList.value?.removeAt(position)
        _videoList.value = _videoList.value // Trigger observers
    }

    fun renameVideo(position: Int, newName: String, newFile: File) {
        _videoList.value?.get(position)?.let { video ->
            video.title = newName
            video.path = newFile.path
            video.artUri = Uri.fromFile(newFile)
            _videoList.value = _videoList.value // Trigger observers
        }
    }
}