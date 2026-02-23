package com.example.ecotrack

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var etCity: EditText
    private lateinit var btnSearchCity: Button
    private lateinit var switchDarkMode: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressBar: ProgressBar

    private lateinit var tvTemperature: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvAQI: TextView
    private lateinit var tvPrediction: TextView
    private lateinit var tvHealthStatus: TextView
    private lateinit var lineChart: LineChart

    private val aqiEntries = ArrayList<Entry>()
    private var index = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initViews()
        setupDarkMode()
        setupChart()
        createNotificationChannel()
        requestNotificationPermission()

        getCurrentLocation()
    }

    private fun initViews() {

        etCity = findViewById(R.id.etCity)
        btnSearchCity = findViewById(R.id.btnSearchCity)
        switchDarkMode = findViewById(R.id.switchDarkMode)
        progressBar = findViewById(R.id.progressBar)

        tvTemperature = findViewById(R.id.tvTemperature)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvAQI = findViewById(R.id.tvAQI)
        tvPrediction = findViewById(R.id.tvPrediction)
        tvHealthStatus = findViewById(R.id.tvHealthStatus)
        lineChart = findViewById(R.id.lineChart)

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnSearchCity.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchAQIData(city)
            } else {
                Toast.makeText(this, "Enter city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 📍 Auto Location
    private fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {

                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )

                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality
                    if (city != null) {
                        etCity.setText(city)
                        fetchAQIData(city)
                    }
                }
            }
        }
    }

    // 🌍 WAQI API
    private fun fetchAQIData(city: String) {

        progressBar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.waqi.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WAQIService::class.java)

        val call = service.getCityAQI(
            city.lowercase(),
            "58e0c043ac0595e8014ea17a64f28f6b707bb61a"
        )

        call.enqueue(object : Callback<WAQIResponse> {

            override fun onResponse(
                call: Call<WAQIResponse>,
                response: Response<WAQIResponse>
            ) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.status == "ok") {

                    val data = response.body()!!.data

                    val realAQI = data.aqi
                    val temperature = data.iaqi?.t?.v ?: 0.0
                    val humidity = data.iaqi?.h?.v ?: 0.0

                    tvAQI.text = "AQI: $realAQI"
                    tvTemperature.text = "Temperature: $temperature °C"
                    tvHumidity.text = "Humidity: $humidity %"

                    val predictedAQI = realAQI + (humidity / 20).toInt()
                    tvPrediction.text = "Predicted AQI: $predictedAQI"

                    tvHealthStatus.text = getHealthStatus(realAQI)

                    updateGraph(realAQI)

                    if (realAQI > 150) {
                        showAQINotification(realAQI)
                    }

                } else {
                    tvAQI.text = "City not found"
                }
            }

            override fun onFailure(call: Call<WAQIResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                tvAQI.text = "Error fetching data"
            }
        })
    }

    private fun getHealthStatus(aqi: Int): String {
        return when {
            aqi <= 50 -> "Good 😊"
            aqi <= 100 -> "Moderate 🙂"
            aqi <= 150 -> "Unhealthy for Sensitive ⚠"
            aqi <= 200 -> "Unhealthy 🚨"
            aqi <= 300 -> "Very Unhealthy ☠"
            else -> "Hazardous ☢"
        }
    }

    private fun setupChart() {
        lineChart.description.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    }

    private fun updateGraph(aqi: Int) {

        aqiEntries.add(Entry(index++, aqi.toFloat()))

        val dataSet = LineDataSet(aqiEntries, "AQI Trend")
        dataSet.setDrawValues(false)
        dataSet.color = Color.parseColor("#2E7D32")
        dataSet.lineWidth = 3f

        lineChart.data = LineData(dataSet)
        lineChart.invalidate()
    }

    private fun setupDarkMode() {

        sharedPreferences = getSharedPreferences("EcoTrackPrefs", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("darkMode", false)

        switchDarkMode.isChecked = isDarkMode

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("darkMode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "AQI_CHANNEL",
                "AQI Alerts",
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun showAQINotification(aqi: Int) {
        val builder = NotificationCompat.Builder(this, "AQI_CHANNEL")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚠ Air Quality Alert")
            .setContentText("AQI is $aqi")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(this).notify(1001, builder.build())
    }
}