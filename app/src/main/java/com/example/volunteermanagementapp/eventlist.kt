package com.example.volunteermanagementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
                        eventArrayList.add(dc.document.toObject(Event::class.java))
                    }
                }

                eventAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun onEditEvent(event: Event) {
        // Handle edit action
    }

    private fun onDeleteEvent(event: Event) {
        // Handle delete action
    }
}
