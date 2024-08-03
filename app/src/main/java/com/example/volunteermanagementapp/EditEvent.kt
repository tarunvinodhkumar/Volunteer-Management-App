package com.example.volunteermanagementapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class EditEvent : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var eventId: String
    private lateinit var editEventName: TextInputEditText
    private lateinit var editEventOrganizer: TextInputEditText
    private lateinit var editEventLocation: TextInputEditText
    private lateinit var editEventDescription: TextInputEditText
    private lateinit var editEventStart: TextInputEditText
    private lateinit var editEventEnd: TextInputEditText
    private lateinit var editEventDate: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_event)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        editEventName = findViewById(R.id.et_event_name)
        editEventOrganizer = findViewById(R.id.et_event_organizer)
        editEventLocation = findViewById(R.id.et_event_location)
        editEventDescription = findViewById(R.id.et_event_description)
        editEventStart = findViewById(R.id.et_event_start)
        editEventEnd = findViewById(R.id.et_event_end)
        editEventDate = findViewById(R.id.et_event_date)

        eventId = intent.getStringExtra("event_id") ?: ""

        // Check ownership before allowing edit
        checkEventOwnership(eventId)

        // Set up click listeners for date and time fields
        editEventDate.setOnClickListener {
            showDatePicker()
        }

        editEventStart.setOnClickListener {
            showTimePicker(isStartTime = true)
        }

        editEventEnd.setOnClickListener {
            showTimePicker(isStartTime = false)
        }

        // Handle Save button click
        findViewById<Button>(R.id.btn_save_event).setOnClickListener {
            saveEventChanges()
        }
    }

    private fun checkEventOwnership(eventId: String) {
        db.collection("event_details").document(eventId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val createdBy = document.getString("created_by")
                    val currentUserId = auth.currentUser?.uid

                    if (createdBy == currentUserId) {
                        // User is the creator, allow editing
                        populateEventDetails(document)
                    } else {
                        // User is not the creator, restrict access
                        Log.w("EditEvent", "Unauthorized access attempt")
                        Toast.makeText(this, "Unauthorized access", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity or show a message
                    }
                } else {
                    Log.w("EditEvent", "No such event")
                    Toast.makeText(this, "Event does not exist", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity if the event does not exist
                }
            }
            .addOnFailureListener { e ->
                Log.w("EditEvent", "Error checking event ownership", e)
                Toast.makeText(this, "Error checking event ownership", Toast.LENGTH_SHORT).show()
                finish() // Close the activity on error
            }
    }

    private fun populateEventDetails(document: DocumentSnapshot) {
        editEventName.setText(document.getString("event_name") ?: "")
        editEventOrganizer.setText(document.getString("event_organizer") ?: "")
        editEventLocation.setText(document.getString("event_location") ?: "")
        editEventDescription.setText(document.getString("event_description") ?: "")
        editEventStart.setText(document.getString("event_start") ?: "")
        editEventEnd.setText(document.getString("event_end") ?: "")
        editEventDate.setText(document.getString("event_date") ?: "")
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val formattedMonth = (selectedMonth + 1).toString().padStart(2, '0')
                val formattedDay = selectedDay.toString().padStart(2, '0')
                editEventDate.setText("$selectedYear-$formattedMonth-$formattedDay")
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                val formattedHour = selectedHour.toString().padStart(2, '0')
                val formattedMinute = selectedMinute.toString().padStart(2, '0')
                val time = "$formattedHour:$formattedMinute"

                if (isStartTime) {
                    editEventStart.setText(time)
                } else {
                    editEventEnd.setText(time)
                }
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun saveEventChanges() {
        Log.d("EditEvent", "Event Name: ${editEventName.text}")
        Log.d("EditEvent", "Event Organizer: ${editEventOrganizer.text}")
        Log.d("EditEvent", "Event Location: ${editEventLocation.text}")
        Log.d("EditEvent", "Event Description: ${editEventDescription.text}")
        Log.d("EditEvent", "Event Start Time: ${editEventStart.text}")
        Log.d("EditEvent", "Event End Time: ${editEventEnd.text}")
        Log.d("EditEvent", "Event Date: ${editEventDate.text}")

        val updatedEvent: MutableMap<String, Any> = hashMapOf(
            "event_name" to editEventName.text.toString(),
            "event_organizer" to editEventOrganizer.text.toString(),
            "event_location" to editEventLocation.text.toString(),
            "event_description" to editEventDescription.text.toString(),
            "event_start" to editEventStart.text.toString(),
            "event_end" to editEventEnd.text.toString(),
            "event_date" to editEventDate.text.toString()
        )

        Log.d("EditEvent", "Updating event: $updatedEvent")

        db.collection("event_details").document(eventId)
            .update(updatedEvent)
            .addOnSuccessListener {
                Log.d("EditEvent", "Event updated successfully")
                Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("EditEvent", "Error updating event", e)
                Toast.makeText(this, "Error updating event", Toast.LENGTH_SHORT).show()
            }
    }
}
