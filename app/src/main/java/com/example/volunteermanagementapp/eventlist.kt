package com.example.volunteermanagementapp

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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