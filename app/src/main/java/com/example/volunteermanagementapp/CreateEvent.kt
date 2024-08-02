package com.example.volunteermanagementapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class CreateEvent : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var eventNameEditText: EditText
    private lateinit var eventOrganizerEditText: EditText
    private lateinit var eventLocationEditText: EditText
    private lateinit var eventDescriptionEditText: EditText
    private lateinit var eventStartEditText: EditText
    private lateinit var eventEndEditText: EditText
    private lateinit var etEventDate: TextInputEditText
    private lateinit var createEventButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_event)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        eventNameEditText = findViewById(R.id.et_event_name)
        eventOrganizerEditText = findViewById(R.id.et_event_organizer)
        eventLocationEditText = findViewById(R.id.et_event_location)
        eventDescriptionEditText = findViewById(R.id.et_event_description)
        eventStartEditText = findViewById(R.id.et_event_start)
        eventEndEditText = findViewById(R.id.et_event_end)
        etEventDate = findViewById(R.id.et_event_date)
        createEventButton = findViewById(R.id.btn_create_event)

        // Set up DatePicker for event date
        etEventDate.setOnClickListener {
            showDatePicker()
        }

        createEventButton.setOnClickListener {
            val eventName = eventNameEditText.text.toString().trim()
            val eventOrganizer = eventOrganizerEditText.text.toString().trim()
            val eventLocation = eventLocationEditText.text.toString().trim()
            val eventDescription = eventDescriptionEditText.text.toString().trim()
            val eventStart = eventStartEditText.text.toString().trim()
            val eventEnd = eventEndEditText.text.toString().trim()
            val eventDate = etEventDate.text.toString().trim()

            if (eventName.isNotEmpty() && eventOrganizer.isNotEmpty() && eventLocation.isNotEmpty() && eventDescription.isNotEmpty() && eventStart.isNotEmpty() && eventEnd.isNotEmpty() && eventDate.isNotEmpty()) {
                val event = hashMapOf(
                    "event_name" to eventName,
                    "event_organizer" to eventOrganizer,
                    "event_location" to eventLocation,
                    "event_description" to eventDescription,
                    "event_start" to eventStart,
                    "event_end" to eventEnd,
                    "event_date" to eventDate
                )

                db.collection("event_details")
                    .add(event)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Event Created Successfully", Toast.LENGTH_SHORT).show()
                        // Navigate back to eventlist activity
                        val intent = Intent(this, eventlist::class.java)
                        startActivity(intent)
                        finish() // Finish the current activity to remove it from the back stack
                    }
                    .addOnFailureListener { e ->
                        Log.e("CreateEvent", "Error adding document", e)
                        Toast.makeText(this, "Error creating event", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun showDatePicker() {
        // Get current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                // Format the selected date
                val formattedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                etEventDate.setText(formattedDate)
            },
            year, month, day
        )

        // Show the DatePickerDialog
        datePickerDialog.show()
    }
}