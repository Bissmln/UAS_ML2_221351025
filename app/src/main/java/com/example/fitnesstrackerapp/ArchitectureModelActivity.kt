package com.example.fitnesstrackerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnesstrackerapp.databinding.ActivityArchitectureModelBinding

class ArchitectureModelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArchitectureModelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArchitectureModelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
