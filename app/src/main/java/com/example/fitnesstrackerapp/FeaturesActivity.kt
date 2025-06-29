package com.example.fitnesstrackerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnesstrackerapp.databinding.ActivityFeaturesBinding

class FeaturesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeaturesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
