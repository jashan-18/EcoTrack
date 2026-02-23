package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private var progressStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        progressBar = findViewById(R.id.progressBar)

        Thread {
            while (progressStatus < 100) {
                progressStatus += 2
                progressBar.progress = progressStatus
                Thread.sleep(40)
            }

            Handler(Looper.getMainLooper()).post {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }.start()
    }
}