package com.example.volunteermanagementapp

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var eventImage: ImageView
    private lateinit var eventName: TextView
    private lateinit var eventDate: TextView
    private lateinit var eventStart: TextView
    private lateinit var eventEnd: TextView
    private lateinit var eventLocation: TextView
    private lateinit var eventOrganizer: TextView
    private lateinit var eventDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_details)

        db = FirebaseFirestore.getInstance()

        eventImage = findViewById(R.id.event_image)
        eventName = findViewById(R.id.event_name)
        eventDate = findViewById(R.id.event_date)
        eventStart = findViewById(R.id.event_start)
        eventEnd = findViewById(R.id.event_end)
        eventLocation = findViewById(R.id.event_location)
        eventOrganizer = findViewById(R.id.event_organizer)
        eventDescription = findViewById(R.id.event_description)

        val eventId = intent.getStringExtra("event_id")
        if (eventId != null) {
            fetchEventDetails(eventId)
        } else {
            Toast.makeText(this, "Event ID is missing", Toast.LENGTH_SHORT).show()
            finish()
        }

        eventLocation.setOnClickListener {
            val location = eventLocation.text.toString()
            openLocationInMaps(location)
        }
    }

    private fun fetchEventDetails(eventId: String) {
        db.collection("event_details").document(eventId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    eventName.text = document.getString("event_name")
                    eventDate.text = document.getString("event_date")
                    eventStart.text = document.getString("event_start")
                    eventEnd.text = document.getString("event_end")
                    eventLocation.text = document.getString("event_location")
                    eventOrganizer.text = document.getString("event_organizer")
                    eventDescription.text = document.getString("event_description")
                    val imageUrl = document.getString("image_url")
                    if (imageUrl != null) {
                        Glide.with(this).load(imageUrl).into(eventImage)
                    }
                } else {
                    Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Log.e("EventDetailsActivity", "Error fetching event details", e)
                Toast.makeText(this, "Error fetching event details", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun openLocationInMaps(location: String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(location)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        val packageManager: PackageManager = packageManager
        val activities = packageManager.queryIntentActivities(mapIntent, 0)
        val isIntentSafe = activities.isNotEmpty()

        Log.d("EventDetailsActivity", "Available activities: ${activities.size}")
        activities.forEach { activity ->
            Log.d("EventDetailsActivity", "Activity: ${activity.activityInfo.packageName}")
        }

        if (isIntentSafe) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(this, "No maps application installed", Toast.LENGTH_SHORT).show()
        }
    }
}