
package com.example.volunteermanagementapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter
    private var eventList: MutableList<Event> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadEvents()

        eventsAdapter = EventsAdapter(eventList)
        recyclerView.adapter = eventsAdapter
    }

    private fun loadEvents() {
        eventList.add(Event("Event 1", "2024-08-01"))
        eventList.add(Event("Event 2", "2024-08-05"))
        eventList.add(Event("Event 3", "2024-08-10"))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                // Handle profile action
                true
            }
            R.id.action_volunteer -> {
                // Handle volunteer action
                true
            }
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
