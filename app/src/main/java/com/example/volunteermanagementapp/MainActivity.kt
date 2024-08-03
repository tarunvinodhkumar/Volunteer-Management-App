package com.example.volunteermanagementapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.ProgressBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Show the loading animation (if any)
        val logoImage = findViewById<ImageView>(R.id.logoImage)
        // Load your logo image if necessary

        val loadingBar = findViewById<ProgressBar>(R.id.loadingBar)
        loadingBar.isIndeterminate = true // Set loading bar to indeterminate mode

        // Delay for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // Start LoginActivity after the delay
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }, 300) // 3000 milliseconds = 3 seconds
    }
}