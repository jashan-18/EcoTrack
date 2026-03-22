package com.jashan.ecotrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotrack.WAQIClient
import com.example.ecotrack.WeatherApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var frameContainer: View
    lateinit var searchView: SearchView
    lateinit var rvFav: RecyclerView
    lateinit var favAdapter: FavCityAdapter

    val favList = mutableListOf<FavCity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val cardWeather = findViewById<CardView>(R.id.cardWeather)
        val cardCommunity = findViewById<CardView>(R.id.cardCommunity)
        val cardDashboard = findViewById<CardView>(R.id.cardDashboard)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        frameContainer = findViewById(R.id.frameContainer)
        searchView = findViewById(R.id.searchCity)
        rvFav = findViewById(R.id.rvFavCity)

        favAdapter = FavCityAdapter(favList) { city, pos ->
            showDeleteDialog(city, pos)
        }

        rvFav.layoutManager = LinearLayoutManager(this)
        rvFav.adapter = favAdapter

        frameContainer.visibility = View.GONE

        cardWeather.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        cardCommunity.setOnClickListener {
            startActivity(Intent(this, WeatherActivity::class.java))
        }

        cardDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        bottomNav.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> {
                    frameContainer.visibility = View.GONE
                    true
                }

                R.id.nav_profile -> {
                    frameContainer.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameContainer, ProfileActivity())
                        .commit()
                    true
                }

                else -> false
            }
        }

        loadFav()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                query?.let {

                    if (it.isNotEmpty()) {

                        saveFav(it)

                        searchView.setQuery("", false)
                        searchView.clearFocus()

                        searchView.postDelayed({
                            loadFav()
                        }, 300)
                    }
                }

                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
    }

    fun saveFav(city: String) {

        val pref = getSharedPreferences("city", MODE_PRIVATE)
        val set = pref.getStringSet("fav", HashSet())!!.toMutableSet()

        set.add(city)

        pref.edit().putStringSet("fav", set).apply()
    }

    fun removeCity(city: String) {

        val pref = getSharedPreferences("city", MODE_PRIVATE)
        val set = pref.getStringSet("fav", HashSet())!!.toMutableSet()

        set.remove(city)

        pref.edit().putStringSet("fav", set).apply()
    }

    fun loadFav() {

        if (!::favAdapter.isInitialized) return

        favList.clear()

        val pref = getSharedPreferences("city", MODE_PRIVATE)
        val set = pref.getStringSet("fav", HashSet())!!

        set.forEach {

            val fav = FavCity(it, "Loading", "Loading")
            favList.add(fav)

            getWeather(it, fav)
            getAQI(it, fav)
        }

        rvFav.post {
            favAdapter.notifyDataSetChanged()
        }
    }

    fun getWeather(city: String, fav: FavCity) {

        WeatherApiClient.api.getWeather(city, "6403855793987fb1fdfaa8cd9dd4c84d")
            .enqueue(object : Callback<WeatherResponse> {

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    res: Response<WeatherResponse>
                ) {
                    if (res.isSuccessful) {

                        fav.temp = res.body()?.main?.temp.toString() + " °C"

                        rvFav.post {
                            favAdapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {}
            })
    }

    fun getAQI(city: String, fav: FavCity) {

        WAQIClient.api.getAQI(city, "58e0c043ac0595e8014ea17a64f28f6b707bb61a")
            .enqueue(object : Callback<WAQIResponse> {

                override fun onResponse(
                    call: Call<WAQIResponse>,
                    res: Response<WAQIResponse>
                ) {
                    if (res.isSuccessful) {

                        fav.aqi = res.body()?.data?.aqi.toString()

                        rvFav.post {
                            favAdapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<WAQIResponse>, t: Throwable) {}
            })
    }

    fun showDeleteDialog(city: FavCity, position: Int) {

        AlertDialog.Builder(this)
            .setTitle("Delete City")
            .setMessage("Do you want to remove ${city.city}?")
            .setPositiveButton("Yes") { _, _ ->

                removeCity(city.city)

                favList.removeAt(position)
                favAdapter.notifyItemRemoved(position)

            }
            .setNegativeButton("No", null)
            .show()
    }
}