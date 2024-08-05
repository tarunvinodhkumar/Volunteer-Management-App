package com.example.volunteermanagementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class volunteerList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var volunteerArrayList: ArrayList<Volunteer>
    private lateinit var volunteerAdapter: VolunteerAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var spinnerSort: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.volunteers_list)

        auth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        volunteerArrayList = arrayListOf()
        volunteerAdapter = VolunteerAdapter(volunteerArrayList, ::onEditVolunteer, ::onDeleteVolunteer)
        recyclerView.adapter = volunteerAdapter

        // Set up the toggle button group
        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.materialButtonToggleGroup)
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.events_toggle -> {
                        // Navigate to EventListActivity
                        val intent = Intent(this, eventlist::class.java)
                        startActivity(intent)
                    }
                    R.id.volunteers_toggle -> {
                        // Navigate to VolunteerListActivity
                        val intent = Intent(this, volunteerList::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

        // Set up the Spinner for sorting
        spinnerSort = findViewById(R.id.spinner_sort)
        val sortOptions = resources.getStringArray(R.array.sort_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSort.adapter = adapter

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> sortByName()
                    1 -> sortByDate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        eventChangeListener()

        // Set up the Create Event button
        val createEventButton = findViewById<Button>(R.id.createevent_button)
        createEventButton.setOnClickListener {
            // Start Create Event Activity
            startActivity(Intent(this, CreateEvent::class.java))
        }
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

    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("volunteer_details").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val volunteer = dc.document.toObject(Volunteer::class.java)
                        volunteer.id = dc.document.id
                        volunteerArrayList.add(volunteer)
                    }
                }

                volunteerAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun sortByName() {
        volunteerArrayList.sortBy { it.volunteer_name }
        volunteerAdapter.notifyDataSetChanged()
    }

    private fun sortByDate() {
        volunteerArrayList.sortBy { it.volunteer_available_date }
        volunteerAdapter.notifyDataSetChanged()
    }

    private fun onEditVolunteer(volunteer: Volunteer) {
        // Handle edit action
    }

    private fun onDeleteVolunteer(volunteer: Volunteer) {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            Log.w("Delete Volunteer", "Current user ID is null")
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("volunteer_details").document(volunteer.id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val createdBy = document.getString("created_by")
                    Log.d("Delete Volunteer", "Created by: $createdBy, Current user: $currentUserId")

                    if (createdBy == currentUserId) {
                        // User is the creator, allow deletion
                        db.collection("volunteer_details").document(volunteer.id)
                            .delete()
                            .addOnSuccessListener {
                                // Remove from the local list and update the adapter
                                volunteerArrayList.remove(volunteer)
                                volunteerAdapter.notifyDataSetChanged()
                                Toast.makeText(this, "Volunteer deleted successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.w("Delete Volunteer", "Error deleting document", e)
                                Toast.makeText(this, "Error deleting volunteer", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // User is not the creator, restrict access
                        Log.w("Delete Volunteer", "Unauthorized delete attempt by user $currentUserId for volunteer created by $createdBy")
                        Toast.makeText(this, "Unauthorized access", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.w("Delete Volunteer", "No such volunteer document")
                    Toast.makeText(this, "Volunteer does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.w("Delete Volunteer", "Error checking volunteer ownership", e)
                Toast.makeText(this, "Error checking volunteer ownership", Toast.LENGTH_SHORT).show()
            }
    }
}