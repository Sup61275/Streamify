import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context
import android.provider.MediaStore
import com.example.streamify.model.Folder

class FolderViewModel : ViewModel() {
    private val _folderList = MutableLiveData<ArrayList<Folder>>()
    val folderList: LiveData<ArrayList<Folder>> = _folderList

    fun loadFolders(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val folders = getFolderList(context)
            _folderList.postValue(folders)
        }
    }

    private fun getFolderList(context: Context): ArrayList<Folder> {
        val tempFolderList = ArrayList<Folder>()
        val projection = arrayOf(
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_ID
        )
        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME} ASC"
        )
        cursor?.use {
            val folderNameIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val folderIdIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val folderIds = HashSet<String>()
            while (it.moveToNext()) {
                val folderId = it.getString(folderIdIndex) ?: continue
                val folderName = it.getString(folderNameIndex) ?: "Unknown"
                if (folderIds.add(folderId)) {
                    tempFolderList.add(Folder(id = folderId, folderName = folderName))
                }
            }
        }
        return tempFolderList
    }
}