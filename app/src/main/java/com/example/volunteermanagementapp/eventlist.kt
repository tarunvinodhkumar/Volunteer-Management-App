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

class eventlist : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventArrayList: ArrayList<Event>
    private lateinit var eventAdapter: EventAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var spinnerSort: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.events_list)

        auth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        eventArrayList = arrayListOf()
        eventAdapter = EventAdapter(eventArrayList, ::onEditEvent, ::onDeleteEvent)
        recyclerView.adapter = eventAdapter

        // Set up the toggle button group
        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.materialButtonToggleGroup)
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.events_toggle -> {
                        // Navigate to EventsActivity
                        val intent = Intent(this, eventlist::class.java)
                        startActivity(intent)
                    }
                    R.id.volunteers_toggle -> {
                        // Navigate to VolunteersActivity
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
        val regVolunteerButton = findViewById<Button>(R.id.btn_register_vol)
        regVolunteerButton.setOnClickListener {
            // Start Register Volunteer Activity
            startActivity(Intent(this, RegisterVolunteer::class.java))
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
        db.collection("event_details").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val event = dc.document.toObject(Event::class.java)
                        event.id = dc.document.id
                        eventArrayList.add(event)
                    }
                }

                eventAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun sortByName() {
        eventArrayList.sortBy { it.event_name }
        eventAdapter.notifyDataSetChanged()
    }

    private fun sortByDate() {
        eventArrayList.sortBy { it.event_date }
        eventAdapter.notifyDataSetChanged()
    }

    private fun onEditEvent(event: Event) {
        val intent = Intent(this, EditEvent::class.java)
        intent.putExtra("event_id", event.id)
        intent.putExtra("event_name", event.event_name)
        intent.putExtra("event_date", event.event_date)
        intent.putExtra("event_start", event.event_start)
        intent.putExtra("event_end", event.event_end)
        intent.putExtra("event_location", event.event_location)
        intent.putExtra("event_organizer", event.event_organizer)
        intent.putExtra("event_description", event.event_description)
        startActivity(intent)
    }

    private fun onDeleteEvent(event: Event) {
        val currentUserId = auth.currentUser?.uid

        db.collection("event_details").document(event.id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val createdBy = document.getString("created_by")

                    if (createdBy == currentUserId) {
                        // User is the creator, allow deletion
                        db.collection("event_details").document(event.id)
                            .delete()
                            .addOnSuccessListener {
                                // Remove from the local list and update the adapter
                                eventArrayList.remove(event)
                                eventAdapter.notifyDataSetChanged()
                                Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.w("Delete Failure", "Error deleting document", e)
                                Toast.makeText(this, "Error deleting event", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // User is not the creator, restrict access
                        Log.w("eventlist", "Unauthorized delete attempt")
                        Toast.makeText(this, "Unauthorized access", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.w("eventlist", "No such event")
                    Toast.makeText(this, "Event does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.w("eventlist", "Error checking event ownership", e)
                Toast.makeText(this, "Error checking event ownership", Toast.LENGTH_SHORT).show()
            }
    }
}