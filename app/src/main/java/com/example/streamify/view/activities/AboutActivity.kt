package com.example.streamify.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.streamify.MainActivity
import com.example.streamify.R
import com.example.streamify.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Streamify)
        val binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "About"
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.aboutText.text =
            "Hello"
    }
}