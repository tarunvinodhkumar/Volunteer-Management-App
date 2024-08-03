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

        btn_register_vol.setOnClickListener {
            val txt_vr_name = vr_name.text.toString().trim()
            val txt_vr_email_id = vr_email_id.text.toString().trim()
            val txt_vr_number = vr_number.text.toString().trim()
            val txt_vr_avail_from_time = vr_avail_from_time.text.toString().trim()
            val txt_vr_avail_to_time = vr_avail_to_time.text.toString().trim()
            val txt_vvr_avail_date = vr_avail_date.text.toString().trim()


            if (txt_vr_name.isNotEmpty() && txt_vr_email_id.isNotEmpty() && txt_vr_number.isNotEmpty() && txt_vr_avail_from_time.isNotEmpty() && txt_vr_avail_to_time.isNotEmpty() && txt_vvr_avail_date.isNotEmpty()) {
                val vol_info = hashMapOf(
                    "volunteer_name" to txt_vr_name,
                    "volunteer_email" to txt_vr_email_id,
                    "volunteer_phone" to txt_vr_number,
                    "volunteer_available_from" to txt_vr_avail_from_time,
                    "volunteer_available_till" to txt_vr_avail_to_time,
                    "volunteer_available_date" to txt_vvr_avail_date
                )

                db.collection("volunteer_details")
                    .add(vol_info)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Volunteer Registered Successfully", Toast.LENGTH_SHORT).show()
                        // Navigate back to volunteerlist activity
                        val intent = Intent(this, volunteerList::class.java)
                        startActivity(intent)
                        finish() // Finish the current activity to remove it from the back stack
                    }
                    .addOnFailureListener { e ->
                        Log.e("RegisterVolunteer", "Error adding information", e)
                        Toast.makeText(this, "Error registering volunteer", Toast.LENGTH_SHORT).show()
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
                vr_avail_date.setText(formattedDate)

            },
            year, month, day
        )

        // Show the DatePickerDialog
        datePickerDialog.show()
    }
}