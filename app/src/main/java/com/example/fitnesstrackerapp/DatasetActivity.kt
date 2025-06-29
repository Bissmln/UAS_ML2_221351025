package com.example.fitnesstrackerapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.InputStreamReader

class DatasetActivity : AppCompatActivity() {

    // URL dataset Kaggle - ganti dengan URL yang sesuai
    private val kaggleDatasetUrl = "https://www.kaggle.com/datasets/fajobgiua/fitness-tracker-data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dataset)

        val backButton: FrameLayout = findViewById(R.id.backButtonContainer)
        backButton.setOnClickListener {
            finish()
        }

        // Setup link Kaggle
        setupKaggleLink()

        val (headers, dataColumns) = readCsvData()
        if (headers.isNotEmpty() && dataColumns.isNotEmpty()) {
            populateTable(headers, dataColumns)
        }
    }

    private fun setupKaggleLink() {
        val datasetSource: TextView = findViewById(R.id.datasetSource)
        datasetSource.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(kaggleDatasetUrl))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Tidak dapat membuka link", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readCsvData(): Pair<List<String>, List<List<String>>> {
        val headers = mutableListOf<String>()
        val dataRows = mutableListOf<List<String>>()

        val inputStream = resources.openRawResource(R.raw.fitness_tracker)
        val reader = BufferedReader(InputStreamReader(inputStream))

        try {
            var line = reader.readLine()
            headers.addAll(line.split(","))

            line = reader.readLine()
            while (line != null) {
                dataRows.add(line.split(","))
                line = reader.readLine()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            reader.close()
        }

        val dataColumns = mutableListOf<List<String>>()
        if (dataRows.isNotEmpty()) {
            for (i in headers.indices) {
                val column = dataRows.map { row -> if (i < row.size) row[i] else "" }
                dataColumns.add(column)
            }
        }

        return Pair(headers, dataColumns)
    }

    private fun populateTable(headers: List<String>, dataColumns: List<List<String>>) {
        val tableContainer: LinearLayout = findViewById(R.id.tableContainerLayout)
        tableContainer.removeAllViews()

        for (i in headers.indices) {
            val header = headers[i]
            val columnData = dataColumns[i]

            val columnLayout = createColumnLayout(this)

            columnLayout.addView(createTextView(this, header, isHeader = true))
            columnLayout.addView(createSeparator(this))

            for (cellData in columnData) {
                columnLayout.addView(createTextView(this, cellData, isHeader = false))
            }

            tableContainer.addView(columnLayout)
        }
    }

    private fun createColumnLayout(context: Context): LinearLayout {
        val layout = LinearLayout(context)
        val params = LinearLayout.LayoutParams(
            dpToPx(120),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, dpToPx(8), 0)
        layout.layoutParams = params
        layout.orientation = LinearLayout.VERTICAL
        layout.background = ContextCompat.getDrawable(context, R.drawable.bg_card_column)
        layout.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
        return layout
    }

    private fun createTextView(context: Context, text: String, isHeader: Boolean): TextView {
        val textView = TextView(context)
        textView.text = text.replace("_", " ")
        textView.gravity = Gravity.CENTER_HORIZONTAL
        textView.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
        if (isHeader) {
            textView.setTypeface(null, android.graphics.Typeface.BOLD)
            textView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }
        return textView
    }

    private fun createSeparator(context: Context): View {
        val view = View(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(1)
        )
        params.setMargins(0, dpToPx(4), 0, dpToPx(4))
        view.layoutParams = params
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.material_dynamic_neutral60))
        return view
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}