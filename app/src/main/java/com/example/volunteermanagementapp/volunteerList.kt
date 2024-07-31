package com.example.volunteermanagementapp

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

class volunteerList: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var volunteerArrayList: ArrayList<Volunteer>
    private lateinit var VolunteerAdapter: VolunteerAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth




    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.volunteers_list)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        volunteerArrayList = arrayListOf()

        VolunteerAdapter = VolunteerAdapter(volunteerArrayList)

        recyclerView.adapter = VolunteerAdapter

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

    private fun EventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("volunteer_details").
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

                        volunteerArrayList.add(dc.document.toObject(Volunteer::class.java))
                    }


                }

                VolunteerAdapter.notifyDataSetChanged()

            }


        })


    }
}