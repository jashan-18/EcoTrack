package com.jashan.ecotrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var frameContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val cardWeather = findViewById<CardView>(R.id.cardWeather)
        val cardCommunity = findViewById<CardView>(R.id.cardCommunity)
        val cardDashboard = findViewById<CardView>(R.id.cardDashboard)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        frameContainer = findViewById(R.id.frameContainer)

        // Initially hide fragment container
        frameContainer.visibility = View.GONE

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

        bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {

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
    }
}