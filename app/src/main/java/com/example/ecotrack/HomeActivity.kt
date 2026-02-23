package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Cards
        val cardWeather = findViewById<CardView>(R.id.cardWeather)
        val cardCommunity = findViewById<CardView>(R.id.cardCommunity)
        val cardDashboard = findViewById<CardView>(R.id.cardDashboard)

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Set Home selected by default
        bottomNav.selectedItemId = R.id.nav_home

        // Card Clicks
        cardWeather.setOnClickListener {
            startActivity(Intent(this, WeatherActivity::class.java))
        }

        cardCommunity.setOnClickListener {
            startActivity(Intent(this, CommunityActivity::class.java))
        }

        cardDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        // Bottom Navigation Clicks
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    // Already in Home
                    true
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
}