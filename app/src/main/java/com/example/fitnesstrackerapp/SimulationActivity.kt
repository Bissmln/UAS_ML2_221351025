package com.example.fitnesstrackerapp

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.DecimalFormat

class SimulationActivity : AppCompatActivity() {

    private lateinit var etSteps: EditText
    private lateinit var etHeartRate: EditText
    private lateinit var etCaloriesBurned: EditText
    private lateinit var etBMI: EditText
    private lateinit var etActiveMinutes: EditText
    private lateinit var btnCheckPrediction: Button
    private lateinit var tvPredictedActivity: TextView
    private lateinit var tvRecommendedDiet: TextView
    private lateinit var tvDailyCalorieNeeds: TextView

    private var tflite: Interpreter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulation)

        initViews()
        loadModel()
        setupClickListeners()
    }

    private fun initViews() {
        try {

            etSteps = findViewById(R.id.etSteps)
            etHeartRate = findViewById(R.id.etHeartRate)
            etCaloriesBurned = findViewById(R.id.etCaloriesBurned)
            etBMI = findViewById(R.id.etBMI)
            etActiveMinutes = findViewById(R.id.etActiveMinutes)
            btnCheckPrediction = findViewById(R.id.btnCheckPrediction)
            tvPredictedActivity = findViewById(R.id.tvPredictedActivity)
            tvRecommendedDiet = findViewById(R.id.tvRecommendedDiet)
            tvDailyCalorieNeeds = findViewById(R.id.tvDailyCalorieNeeds)
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing views: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun loadModel() {
        try {
            val modelFile = loadModelFile()
            tflite = Interpreter(modelFile)
            Toast.makeText(this, "Model loaded successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading model: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = assets.openFd("Fitness_tracker.tflite")
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun setupClickListeners() {
        try {

            findViewById<Button>(R.id.btnBack)?.setOnClickListener {
                finish()
            }

            btnCheckPrediction.setOnClickListener {
                if (validateInputs()) {
                    makePrediction()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error setting up click listeners: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun validateInputs(): Boolean {
        val inputs = listOf(etSteps, etHeartRate, etCaloriesBurned, etBMI, etActiveMinutes)
        val labels = listOf("Steps", "Heart Rate", "Calories Burned", "BMI", "Active Minutes")

        for (i in inputs.indices) {
            if (inputs[i].text.toString().trim().isEmpty()) {
                Toast.makeText(this, "${labels[i]} is required", Toast.LENGTH_SHORT).show()
                inputs[i].requestFocus()
                return false
            }
            try {
                inputs[i].text.toString().toFloat()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid number for ${labels[i]}", Toast.LENGTH_SHORT).show()
                inputs[i].requestFocus()
                return false
            }
        }
        return true
    }

    private fun makePrediction() {
        if (tflite == null) {
            Toast.makeText(this, "Model is not loaded yet.", Toast.LENGTH_LONG).show()
            return
        }

        try {

            val featureMeans = floatArrayOf(10686.716f, 122.428f, 438.082f, 24.2336f, 106.878f)
            val featureScales = floatArrayOf(5312.433f, 33.153f, 219.957f, 3.431f, 53.111f)
            val rawSteps = etSteps.text.toString().toFloat()
            val rawHeartRate = etHeartRate.text.toString().toFloat()
            val rawCalories = etCaloriesBurned.text.toString().toFloat()
            val rawBMI = etBMI.text.toString().toFloat()
            val rawActiveMinutes = etActiveMinutes.text.toString().toFloat()

            val inputData = floatArrayOf(
                (rawSteps - featureMeans[0]) / featureScales[0],
                (rawHeartRate - featureMeans[1]) / featureScales[1],
                (rawCalories - featureMeans[2]) / featureScales[2],
                (rawBMI - featureMeans[3]) / featureScales[3],
                (rawActiveMinutes - featureMeans[4]) / featureScales[4]
            )

            val normalizedInput = Array(1) { inputData }
            val outputData = Array(1) { FloatArray(3) }

            tflite?.run(normalizedInput, outputData)

            val predictions = outputData[0]
            val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: 0
            val confidence = predictions[maxIndex]

            Log.d("PredictionDebug", "Probabilities: ${predictions.joinToString()}")
            Log.d("PredictionDebug", "Max Index: $maxIndex")

            val activities = arrayOf("Resting", "Running", "Walking")
            val predictedActivity = activities[maxIndex]

            updateResults(predictedActivity, confidence)

        } catch (e: Exception) {
            Toast.makeText(this, "Error making prediction: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun updateResults(activity: String, confidence: Float) {
        try {
            tvPredictedActivity.text = activity

            val bmi = etBMI.text.toString().toFloat()

            val dietRecommendation = when {
                activity == "Running" && bmi < 25 -> "High protein and moderate carbs"
                activity == "Running" && bmi >= 25 -> "High protein and low carbs"
                activity == "Walking" && bmi < 25 -> "Balanced protein and moderate carbs"
                activity == "Walking" && bmi >= 25 -> "Moderate protein and low carbs"
                activity == "Resting" && bmi < 25 -> "Light meals with balanced nutrients"
                activity == "Resting" && bmi >= 25 -> "Low calorie with high fiber"
                else -> "Balanced diet with moderate protein"
            }
            tvRecommendedDiet.text = dietRecommendation

            val baseCalories = when {
                bmi < 18.5 -> 2200
                bmi < 25 -> 2000
                bmi < 30 -> 1800
                else -> 1600
            }

            val activityMultiplier = when (activity) {
                "Running" -> 1.4f
                "Walking" -> 1.2f
                "Resting" -> 1.0f
                else -> 1.1f // Default multiplier
            }
            val dailyCalories = (baseCalories * activityMultiplier).toInt()
            tvDailyCalorieNeeds.text = "$dailyCalories kcal"

            val df = DecimalFormat("#.##")
            Toast.makeText(this, "Prediction confidence: ${df.format(confidence * 100)}%", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error updating results: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tflite?.close()
    }
}