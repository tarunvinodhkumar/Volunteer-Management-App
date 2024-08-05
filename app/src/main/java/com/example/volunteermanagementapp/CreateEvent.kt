package com.example.volunteermanagementapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class CreateEvent : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    private lateinit var eventNameEditText: EditText
    private lateinit var eventOrganizerEditText: EditText
    private lateinit var eventLocationEditText: EditText
    private lateinit var eventDescriptionEditText: EditText
    private lateinit var eventStartEditText: EditText
    private lateinit var eventEndEditText: EditText
    private lateinit var etEventDate: TextInputEditText
    private lateinit var createEventButton: Button
    private lateinit var addPhotosButton: Button

    private val PICK_PHOTO_REQUEST = 1
    private val photoUris = mutableListOf<Uri>()
    private val photoUrls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_event)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        initializeUI()

        // Check if user is authenticated
        if (auth.currentUser == null) {
            navigateToLogin()
            return
        }

        // Set up DatePicker for event date
        etEventDate.setOnClickListener {
            showDatePicker()
        }

        // Set up TimePickers for event start and end times
        eventStartEditText.setOnClickListener {
            showTimePicker { time ->
                eventStartEditText.setText(time)
            }
        }

        eventEndEditText.setOnClickListener {
            showTimePicker { time ->
                eventEndEditText.setText(time)
            }
        }

        createEventButton.setOnClickListener {
            createEvent()
        }

        // Set up photo upload button
        addPhotosButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple photo selection
            startActivityForResult(intent, PICK_PHOTO_REQUEST)
        }
    }

    private fun initializeUI() {
        eventNameEditText = findViewById(R.id.et_event_name)
        eventOrganizerEditText = findViewById(R.id.et_event_organizer)
        eventLocationEditText = findViewById(R.id.et_event_location)
        eventDescriptionEditText = findViewById(R.id.et_event_description)
        eventStartEditText = findViewById(R.id.et_event_start)
        eventEndEditText = findViewById(R.id.et_event_end)
        etEventDate = findViewById(R.id.et_event_date)
        createEventButton = findViewById(R.id.btn_create_event)
        addPhotosButton = findViewById(R.id.btn_add_photos)
    }

    private fun createEvent() {
        val eventName = eventNameEditText.text.toString().trim()
        val eventOrganizer = eventOrganizerEditText.text.toString().trim()
        val eventLocation = eventLocationEditText.text.toString().trim()
        val eventDescription = eventDescriptionEditText.text.toString().trim()
        val eventStart = eventStartEditText.text.toString().trim()
        val eventEnd = eventEndEditText.text.toString().trim()
        val eventDate = etEventDate.text.toString().trim()

        if (validateInputs(eventName, eventOrganizer, eventLocation, eventDescription, eventStart, eventEnd, eventDate)) {
            val userId = auth.currentUser?.uid ?: return
            val event = hashMapOf(
                "event_name" to eventName,
                "event_organizer" to eventOrganizer,
                "event_location" to eventLocation,
                "event_description" to eventDescription,
                "event_start" to eventStart,
                "event_end" to eventEnd,
                "event_date" to eventDate,
                "created_by" to userId, // Store the creator's user ID
                "image_urls" to photoUrls // Store the list of photo URLs
            )

            db.collection("event_details")
                .add(event)
                .addOnSuccessListener {
                    Toast.makeText(this, "Event Created Successfully", Toast.LENGTH_SHORT).show()
                    navigateToEventList()
                }
                .addOnFailureListener { e ->
                    Log.e("CreateEvent", "Error adding document", e)
                    Toast.makeText(this, "Error creating event. Please try again.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun validateInputs(vararg inputs: String): Boolean {
        return inputs.all { it.isNotEmpty() }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, Login::class.java))
        finish()
    }

    private fun navigateToEventList() {
        val intent = Intent(this, eventlist::class.java)
        startActivity(intent)
        finish() // Finish the current activity to remove it from the back stack
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                auth.signOut()
                navigateToLogin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                etEventDate.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(onTimeSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                onTimeSet(formattedTime)
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == RESULT_OK) {
            val clipData = data?.clipData
            if (clipData != null) {
                // Handle multiple photos
                for (i in 0 until clipData.itemCount) {
                    val imageUri: Uri = clipData.getItemAt(i).uri
                    photoUris.add(imageUri)
                    uploadPhotoToFirebase(imageUri)
                }
            } else {
                // Handle single photo
                val imageUri: Uri? = data?.data
                imageUri?.let { uploadPhotoToFirebase(it) }
            }
        }
    }

    private fun uploadPhotoToFirebase(imageUri: Uri) {
        val storageRef = storage.reference.child("photos/${System.currentTimeMillis()}.jpg")
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                photoUrls.add(uri.toString()) // Add the photo URL to the list
                Toast.makeText(this, "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("CreateEvent", "Error uploading photo", e)
            Toast.makeText(this, "Error uploading photo. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
