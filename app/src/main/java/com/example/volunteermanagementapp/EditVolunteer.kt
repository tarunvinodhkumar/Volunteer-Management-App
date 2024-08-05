package com.example.volunteermanagementapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditVolunteer : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var volunteerName: EditText
    private lateinit var volunteerEmail: EditText
    private lateinit var volunteerPhone: EditText
    private lateinit var availableDate: EditText
    private lateinit var availableFrom: EditText
    private lateinit var availableTill: EditText
    private lateinit var updateButton: Button

    private lateinit var volunteerId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_volunteer)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        volunteerName = findViewById(R.id.editVolunteerName)
        volunteerEmail = findViewById(R.id.editVolunteerEmail)
        volunteerPhone = findViewById(R.id.editVolunteerPhone)
        availableDate = findViewById(R.id.editAvailableDate)
        availableFrom = findViewById(R.id.editAvailableFrom)
        availableTill = findViewById(R.id.editAvailableTill)
        updateButton = findViewById(R.id.updateButton)

        // Retrieve data from intent
        val intent = intent
        volunteerId = intent.getStringExtra("volunteer_id") ?: ""

        // Set existing values
        volunteerName.setText(intent.getStringExtra("volunteer_name"))
        volunteerEmail.setText(intent.getStringExtra("volunteer_email"))
        volunteerPhone.setText(intent.getStringExtra("volunteer_phone"))
        availableDate.setText(intent.getStringExtra("volunteer_available_date"))
        availableFrom.setText(intent.getStringExtra("volunteer_available_from"))
        availableTill.setText(intent.getStringExtra("volunteer_available_till"))

        updateButton.setOnClickListener {
            checkUserAccessAndUpdate()
        }
    }

    private fun checkUserAccessAndUpdate() {
        // Fetch the current user's ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Reference to the specific volunteer document
        val docRef = db.collection("volunteer_details").document(volunteerId)

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // Get the creatorId from the document
                val creatorId = document.getString("creatorId")

                // Log creatorId and currentUserId for debugging
                Log.d("EditVolunteer", "Creator ID: $creatorId")
                Log.d("EditVolunteer", "Current User ID: $currentUserId")

                if (creatorId == currentUserId) {
                    // User is authorized to update the document
                    updateVolunteer()
                } else {
                    // Handle unauthorized access
                    Toast.makeText(this, "You do not have permission to edit this volunteer", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Handle the case where the document does not exist
                Toast.makeText(this, "Volunteer not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("EditVolunteer", "Error fetching document", e)
            Toast.makeText(this, "Error fetching document", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateVolunteer() {
        val name = volunteerName.text.toString()
        val email = volunteerEmail.text.toString()
        val phone = volunteerPhone.text.toString()
        val date = availableDate.text.toString()
        val from = availableFrom.text.toString()
        val till = availableTill.text.toString()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || date.isEmpty() || from.isEmpty() || till.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val volunteer = mapOf(
            "volunteer_name" to name,
            "volunteer_email" to email,
            "volunteer_phone" to phone,
            "volunteer_available_date" to date,
            "volunteer_available_from" to from,
            "volunteer_available_till" to till
        )

        // Reference to the specific volunteer document
        val docRef = db.collection("volunteer_details").document(volunteerId)

        // Update the document
        docRef.update(volunteer)
            .addOnSuccessListener {
                Toast.makeText(this, "Volunteer updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating volunteer: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
