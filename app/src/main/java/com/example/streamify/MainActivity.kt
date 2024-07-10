package com.example.streamify

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.streamify.databinding.ActivityMainBinding

import com.example.streamify.model.Folder
import com.example.streamify.model.Video
import com.example.streamify.model.checkForInternet
import com.example.streamify.model.getAllVideos
import com.example.streamify.view.activities.UrlActivity
import com.example.streamify.view.fragments.FoldersFragment
import com.example.streamify.view.fragments.VideosFragment
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var currentFragment: Fragment

    companion object {
        lateinit var videoList: ArrayList<Video>
        lateinit var folderList: ArrayList<Folder>
        lateinit var searchList: ArrayList<Video>
        var search: Boolean = false
        var sortValue: Int = 0
        val sortList = arrayOf(
            MediaStore.Video.Media.DATE_ADDED + " DESC",
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.TITLE + " DESC",
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.SIZE + " DESC"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Set status bar and app bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.claude_background)
        binding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.claude_background))

        sortValue = getSharedPreferences("Sorting", MODE_PRIVATE).getInt("sortValue", 0)

        // Set navigation bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.claude_lighter_grey)

        binding.toolbar.findViewById<AppCompatImageView>(R.id.sortIcon).setOnClickListener {
            showSortDialog()
        }

        if (requestRuntimePermission()) {
            folderList = ArrayList()
            videoList = getAllVideos(this)
            setFragment(VideosFragment())
        } else {
            folderList = ArrayList()
            videoList = ArrayList()
        }

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.videoView -> setFragment(VideosFragment())
                R.id.foldersView -> setFragment(FoldersFragment())
                R.id.navYoutube -> openYoutube()
                R.id.navUrl -> startActivity(Intent(this, UrlActivity::class.java))
                R.id.exitNav -> showExitDialog()
            }
            true
        }
    }
    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit Application")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
    private fun showSortDialog() {
        val menuItems = arrayOf(
            "Latest",
            "Oldest",
            "Name(A to Z)",
            "Name(Z to A)",
            "File Size(Smallest)",
            "File Size(Largest)"
        )
        var tempSortValue = sortValue
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Sort By")
            .setPositiveButton("OK") { _, _ ->
                if (tempSortValue != sortValue) {
                    sortValue = tempSortValue
                    val sortEditor = getSharedPreferences("Sorting", MODE_PRIVATE).edit()
                    sortEditor.putInt("sortValue", sortValue)
                    sortEditor.apply()

                    // Refresh the current fragment to apply new sorting
                    refreshCurrentFragment()
                }
            }
            .setSingleChoiceItems(menuItems, sortValue) { _, pos ->
                tempSortValue = pos
            }
            .create()
        dialog.show()

        // Set the OK button color to white
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
    }
    private fun refreshCurrentFragment() {
        when (currentFragment) {
            is VideosFragment -> setFragment(VideosFragment())
            is FoldersFragment -> setFragment(FoldersFragment())
            // Add other fragment types if needed
        }
    }

    private fun setFragment(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentFL, fragment)
            .commit()
    }

    private fun openYoutube() {
        if (checkForInternet(this)) {
            val url = "https://youtube.com/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } else {
            Snackbar.make(binding.root, "Internet Not Connected", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showMoreOptions() {
        val options = arrayOf("About", "Exit")
        AlertDialog.Builder(this)
            .setItems(options) { _, which ->
                when (which) {

                    1 -> finish()
                }
            }
            .show()
    }

    private fun requestRuntimePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_VIDEO), 13)
                return false
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 14)
                return false
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13 || requestCode == 14) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                folderList = ArrayList()
                videoList = getAllVideos(this)
                setFragment(VideosFragment())
            } else {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Storage Permission Needed!!",
                    Snackbar.LENGTH_LONG
                ).setAction("OK") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_VIDEO), 13)
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 14)
                    }
                }.show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        (currentFragment as? VideosFragment)?.adapter?.onResult(requestCode, resultCode)
    }
}