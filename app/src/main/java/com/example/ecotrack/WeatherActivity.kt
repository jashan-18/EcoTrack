package com.jashan.ecotrack
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class WeatherActivity : AppCompatActivity() {

    private lateinit var etCity: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvTemp: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvWind: TextView
    private lateinit var recyclerForecast: RecyclerView

    private val API_KEY = "79eb5e1a5f8adf2b6f8c183410786f7b"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        etCity = findViewById(R.id.etCityWeather)
        btnSearch = findViewById(R.id.btnSearchWeather)
        tvTemp = findViewById(R.id.tvTemp)
        tvHumidity = findViewById(R.id.tvHumidityWeather)
        tvDescription = findViewById(R.id.tvDescription)
        tvWind = findViewById(R.id.tvWind)
        recyclerForecast = findViewById(R.id.recyclerForecast)

        recyclerForecast.layoutManager = LinearLayoutManager(this)

        btnSearch.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchCurrentWeather(city)
                fetch5DayForecast(city)
            } else {
                Toast.makeText(this, "Enter city name", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun fetchCurrentWeather(city: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)

        val call = service.getWeather(city, API_KEY, "metric")

        call.enqueue(object : Callback<WeatherResponse> {

            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    val data = response.body()!!

                    tvTemp.text = "Temperature: ${data.main.temp} °C"
                    tvHumidity.text = "Humidity: ${data.main.humidity} %"
                    tvDescription.text = "Condition: ${data.weather[0].description}"
                    tvWind.text = "Wind Speed: ${data.wind.speed} m/s"

                } else {
                    Toast.makeText(
                        this@WeatherActivity,
                        "City not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(
                    this@WeatherActivity,
                    "Network Error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

   
    private fun fetch5DayForecast(city: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)

        val call = service.get5DayForecast(city, API_KEY, "metric")

        call.enqueue(object : Callback<ForecastResponse> {

            override fun onResponse(
                call: Call<ForecastResponse>,
                response: Response<ForecastResponse>
            ) {

                if (response.isSuccessful && response.body() != null) {


                    val dailyList = response.body()!!.list
                        .filterIndexed { index, _ -> index % 8 == 0 }

                    recyclerForecast.adapter = ForecastAdapter(dailyList)

                } else {
                    Toast.makeText(
                        this@WeatherActivity,
                        "Forecast not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Toast.makeText(
                    this@WeatherActivity,
                    "Forecast Error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}