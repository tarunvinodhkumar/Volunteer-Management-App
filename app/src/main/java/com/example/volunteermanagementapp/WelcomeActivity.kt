package com.example.volunteermanagementapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

        // Volunteer button logic
        val volunteerButton = findViewById<Button>(R.id.volunteer_button)
        volunteerButton.setOnClickListener {
            // Start Volunteer Activity
            startActivity(Intent(this, eventlist::class.java))
        }
//
//        // Event Manager button logic
//        val eventManagerButton = findViewById<Button>(R.id.event_manager_button)
//        eventManagerButton.setOnClickListener {
//            // Start Event Manager Activity
//            startActivity(Intent(this, EventManagerActivity::class.java))
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_actionbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                auth.signOut()
                // Redirect back to the Login activity
                startActivity(Intent(this, Login::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}