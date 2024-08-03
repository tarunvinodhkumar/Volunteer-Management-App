package com.example.volunteermanagementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
        // Handle edit action
    }

    private fun onDeleteEvent(event: Event) {
        db.collection("event_details").document(event.id)
            .delete()
            .addOnSuccessListener {
                // Remove from the local list and update the adapter
                eventArrayList.remove(event)
                eventAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w("Delete Failure", "Error deleting document", e)
            }
    }
}
