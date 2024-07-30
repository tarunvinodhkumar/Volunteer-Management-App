package com.example.volunteermanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)

        auth = FirebaseAuth.getInstance()

        // Display a welcome message with the user's email
        val welcomeMessage = findViewById<TextView>(R.id.welcome_message)
        val user = auth.currentUser
        welcomeMessage.text = "Welcome, ${user?.displayName ?: "User"}!"

        // Sign-out button logic
        val signOutButton = findViewById<Button>(R.id.sign_out_button)
        signOutButton.setOnClickListener {
            auth.signOut()
            // Redirect back to the Login activity
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }
}