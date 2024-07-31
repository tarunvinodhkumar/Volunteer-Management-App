package com.example.volunteermanagementapp

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase

class eventlist: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventArrayList: ArrayList<Event>
    private lateinit var EventAdapter: EventAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth




    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.events_list)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        eventArrayList = arrayListOf()

        EventAdapter = EventAdapter(eventArrayList)

        recyclerView.adapter = EventAdapter

        EventChangeListener()



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

//    // Find the buttons by their IDs
//    val eventsToggle: Button = findViewById(R.id.events_toggle)
//    val volunteersToggle: Button = findViewById(R.id.volunteers_toggle)

//    // Set up the toggle buttons
//    events_toggle.setOnClickListener {
//        // Navigate to EventsActivity
//        val intent = Intent(this, EventsActivity::class.java)
//        startActivity(intent)
//    }
//
//    volunteers_toggle.setOnClickListener {
//        // Navigate to VolunteersActivity
//        val intent = Intent(this, VolunteersActivity::class.java)
//        startActivity(intent)
//    }
//
    private fun EventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("event_details").
                addSnapshotListener(object : EventListener<QuerySnapshot>{
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {

                        if (error != null){

                            Log.e("Firestore Error",error.message.toString())
                            return
                        }

                        for (dc : DocumentChange in value?.documentChanges!!){


                            if (dc.type == DocumentChange.Type.ADDED){

                                eventArrayList.add(dc.document.toObject(Event::class.java))
                            }


                        }

                        EventAdapter.notifyDataSetChanged()

                    }


                })


    }
}