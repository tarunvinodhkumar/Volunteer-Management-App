package com.example.volunteermanagementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.example.volunteermanagementapp.Event
import com.example.volunteermanagementapp.R
import com.example.volunteermanagementapp.eventlist

class EventDetailsActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        db = FirebaseFirestore.getInstance()
        imageView = findViewById(R.id.imageView)

        // Retrieve data from intent
        val eventId = intent.getStringExtra("event_id") ?: ""
        Log.d("EventDetailsActivity", "Event ID: $eventId")

        // Fetch event details from Firestore
        db.collection("event_details").document(eventId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val eventName = document.getString("event_name") ?: ""
                    val eventDate = document.getString("event_date") ?: ""
                    val eventStart = document.getString("event_start") ?: ""
                    val eventEnd = document.getString("event_end") ?: ""
                    val eventLocation = document.getString("event_location") ?: ""
                    val eventOrganizer = document.getString("event_organizer") ?: ""
                    val eventDescription = document.getString("event_description") ?: ""
                    val imageUrl = document.getString("image_url") ?: "" // Assuming single image URL

                    // Display event details
                    displayEventDetails(
                        Event(
                            id = eventId,
                            event_name = eventName,
                            event_date = eventDate,
                            event_start = eventStart,
                            event_end = eventEnd,
                            event_location = eventLocation,
                            event_organizer = eventOrganizer,
                            event_description = eventDescription
                        )
                    )

                    // Load single image
                    Glide.with(this)
                        .load(imageUrl)
                        .into(imageView)

                    // Set up click listener for the image view
                    imageView.setOnClickListener {
                        Log.d("EventDetailsActivity", "ImageView clicked!")
                        // Handle image click if needed
                    }
                } else {
                    Log.d("EventDetailsActivity", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("EventDetailsActivity", "get failed with ", exception)
            }

        val backButton: Button = findViewById(R.id.btnBackToEventList)
        backButton.setOnClickListener {
            navigateToEventList()
        }
    }

    private fun displayEventDetails(event: Event) {
        findViewById<TextView>(R.id.tvEventName).text = event.event_name
        findViewById<TextView>(R.id.tvEventDate).text = "Date: ${event.event_date}"
        findViewById<TextView>(R.id.tvEventTime).text =
            "Time: ${event.event_start} - ${event.event_end}"
        findViewById<TextView>(R.id.tvEventOrganizer).text =
            "Organized by: ${event.event_organizer}"
        findViewById<TextView>(R.id.tvEventLocation).text = "Location: ${event.event_location}"
        findViewById<TextView>(R.id.tvEventDescription).text =
            "Description: ${event.event_description}"
    }

    private fun navigateToEventList() {
        val intent = Intent(this, eventlist::class.java)
        startActivity(intent)
        finish() // Optionally, finish the current activity to remove it from the back stack
    }
}
