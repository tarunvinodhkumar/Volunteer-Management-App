package com.example.volunteermanagementapp

import EventDetails
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var tvEventName: TextView
    private lateinit var tvOrganizedBy: TextView
    private lateinit var tvFromDate: TextView
    private lateinit var tvToDate: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvDescription: TextView
    private lateinit var event: EventDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        // Initialize views
        tvEventName = findViewById(R.id.tvEventName)
        tvOrganizedBy = findViewById(R.id.tvOrganizedBy)
        tvFromDate = findViewById(R.id.tvFromDate)
        tvToDate = findViewById(R.id.tvToDate)
        tvLocation = findViewById(R.id.tvLocation)
        tvDescription = findViewById(R.id.tvDescription)

        // Get the event object from the Intent
        event = intent.getSerializableExtra("event") as EventDetails

        // Set event details
        tvEventName.text = event.event_name
        tvOrganizedBy.text = "Organized by: ${event.event_organizer}"
        tvFromDate.text = "From: ${event.event_start}"
        tvToDate.text = "To: ${event.event_end}"
        tvLocation.text = "Location: ${event.event_location}"
        tvDescription.text = event.event_description

        // Fetch additional details from Firebase if necessary
        fetchEventDetails(event.event_id)
    }

    private fun fetchEventDetails(eventId: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("events").child(eventId)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val eventDetails = dataSnapshot.getValue(EventDetails::class.java)
                eventDetails?.let {
                    tvEventName.text = it.event_name
                    tvOrganizedBy.text = "Organized by: ${it.event_organizer}"
                    tvFromDate.text = "From: ${it.event_start}"
                    tvToDate.text = "To: ${it.event_end}"
                    tvLocation.text = "Location: ${it.event_location}"
                    tvDescription.text = it.event_description
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Log the error message
                Log.e("EventDetailsActivity", "Database error: ${databaseError.message}")

                // Show an error message to the user
                Toast.makeText(this@EventDetailsActivity, "Failed to load event details. Please try again.", Toast.LENGTH_SHORT).show()

                // Navigate to a safe state (e.g., main activity or previous screen)
                finish() // Close the current activity to navigate back
            }
        })
    }
}
