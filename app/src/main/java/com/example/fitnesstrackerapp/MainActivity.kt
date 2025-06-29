package com.example.fitnesstrackerapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutTentangAplikasi: ConstraintLayout = findViewById(R.id.layoutTentangAplikasi)
        val layoutDataset: ConstraintLayout = findViewById(R.id.layoutDataset)
        val layoutFitur: ConstraintLayout = findViewById(R.id.layoutFitur)
        val layoutModel: ConstraintLayout = findViewById(R.id.layoutModel)
        val layoutSimulasi: ConstraintLayout = findViewById(R.id.layoutSimulasi)

        layoutTentangAplikasi.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        layoutDataset.setOnClickListener {
            val intent = Intent(this, DatasetActivity::class.java)
            startActivity(intent)
        }

        layoutFitur.setOnClickListener {
            val intent = Intent(this, FeaturesActivity::class.java)
            startActivity(intent)
        }

        layoutModel.setOnClickListener {
            val intent = Intent(this, ArchitectureModelActivity::class.java)
            startActivity(intent)
        }

        layoutSimulasi.setOnClickListener {
            val intent = Intent(this, SimulationActivity::class.java)
            startActivity(intent)
        }
    }
}