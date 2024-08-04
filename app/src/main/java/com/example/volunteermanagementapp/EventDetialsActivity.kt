package com.example.volunteermanagementapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.MapView
import android.widget.TextView

class EventDetailsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewPager: ViewPager
    private lateinit var tvEventName: TextView
    private lateinit var tvOrganizedBy: TextView
    private lateinit var tvFromDate: TextView
    private lateinit var tvToDate: TextView
    private lateinit var tvLocation: TextView
    private lateinit var mapView: MapView
    private lateinit var tvDescription: TextView
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        // Initialize views
        viewPager = findViewById(R.id.viewPager)
        tvEventName = findViewById(R.id.tvEventName)
        tvOrganizedBy = findViewById(R.id.tvOrganizedBy)
        tvFromDate = findViewById(R.id.tvFromDate)
        tvToDate = findViewById(R.id.tvToDate)
        tvLocation = findViewById(R.id.tvLocation)
        mapView = findViewById(R.id.mapView)
        tvDescription = findViewById(R.id.tvDescription)

        // Set event details
        tvEventName.text = "Event Name"
        tvOrganizedBy.text = "Organized by: Organizer"
        tvFromDate.text = "From: Start Date"
        tvToDate.text = "To: End Date"
        tvLocation.text = "Location: Some Place"
        tvDescription.text = "Event Description"

        // Initialize the MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val location = LatLng(-34.0, 151.0) // Example coordinates
        googleMap.addMarker(MarkerOptions().position(location).title("Event Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
