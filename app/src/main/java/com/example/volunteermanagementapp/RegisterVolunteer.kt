package com.example.volunteermanagementapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RegisterVolunteer : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var vr_name: EditText
    private lateinit var vr_email_id: EditText
    private lateinit var vr_number: EditText
    private lateinit var vr_avail_from_time: EditText
    private lateinit var vr_avail_to_time: EditText
    private lateinit var vr_avail_date: EditText
    private lateinit var btn_register_vol: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_volunteer)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        vr_name = findViewById(R.id.vr_name)
        vr_email_id = findViewById(R.id.vr_email_id)
        vr_number = findViewById(R.id.vr_number)
        vr_avail_from_time = findViewById(R.id.vr_avail_from_time)
        vr_avail_to_time = findViewById(R.id.vr_avail_to_time)
        vr_avail_date = findViewById(R.id.vr_avail_date)
        btn_register_vol = findViewById(R.id.btn_register_vol)

        // Set up DatePicker for volunteer date
        vr_avail_date.setOnClickListener {
            showDatePicker()
        }

        // Set up TimePickers for availability times
        vr_avail_from_time.setOnClickListener {
            showTimePicker { time ->
                vr_avail_from_time.setText(time)
            }
        }

        vr_avail_to_time.setOnClickListener {
            showTimePicker { time ->
                vr_avail_to_time.setText(time)
            }
        }

        btn_register_vol.setOnClickListener {
            registerVolunteer()
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
                vr_avail_date.setText(formattedDate)
            },
            year, month, day
        )

        // Show the DatePickerDialog
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

    private fun registerVolunteer() {
        val name = vr_name.text.toString().trim()
        val email = vr_email_id.text.toString().trim()
        val phone = vr_number.text.toString().trim()
        val fromTime = vr_avail_from_time.text.toString().trim()
        val toTime = vr_avail_to_time.text.toString().trim()
        val date = vr_avail_date.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || fromTime.isEmpty() || toTime.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate email format (basic check)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate phone number format (optional)
        if (!phone.matches(Regex("\\d+"))) {
            Toast.makeText(this, "Invalid phone number format", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate date format (basic check)
        try {
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            dateFormat.parse(date)
        } catch (e: java.text.ParseException) {
            Toast.makeText(this, "Invalid date format. Use dd/MM/yyyy", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate time formats
        try {
            val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            timeFormat.parse(fromTime)
            timeFormat.parse(toTime)
        } catch (e: java.text.ParseException) {
            Toast.makeText(this, "Invalid time format. Use HH:mm", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the current user ID
        val creatorId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Create the volunteer object
        val volunteer = hashMapOf(
            "volunteer_name" to name,
            "volunteer_email" to email,
            "volunteer_phone" to phone,
            "volunteer_available_from" to fromTime,
            "volunteer_available_till" to toTime,
            "volunteer_available_date" to date,
            "creatorId" to creatorId // Add creatorId to the volunteer details
        )

        // Add the volunteer to Firestore
        db.collection("volunteer_details")
            .add(volunteer)
            .addOnSuccessListener {
                Toast.makeText(this, "Volunteer Registered Successfully", Toast.LENGTH_SHORT).show()
                // Navigate back to volunteer list activity
                val intent = Intent(this, volunteerList::class.java)
                startActivity(intent)
                finish() // Finish the current activity to remove it from the back stack
            }
            .addOnFailureListener { e ->
                Log.e("RegisterVolunteer", "Error adding information", e)
                Toast.makeText(this, "Error registering volunteer", Toast.LENGTH_SHORT).show()
            }
    }
}
