package com.cmpt362.zachary_fong_fitnesstracker

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.zachary_fong_fitnesstracker.databinding.ActivityMapsBinding
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


class ViewMapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private var entryId = 0L
    private var units = 0

    private lateinit var database: EntryDatabase
    private lateinit var databaseDao: EntryDatabaseDAO
    private lateinit var repository: EntryRepository
    private lateinit var viewModel: EntryViewModel
    private lateinit var factory: EntryViewModelFactory

    private lateinit var latLng: MutableList<String>

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var polylineOptions: PolylineOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btSave.setText("Delete")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        var sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
        units = sharedPref.getInt("preference", 0)
        entryId = sharedPref.getLong("id", 0L)

        database = EntryDatabase.getInstance(this)
        databaseDao = database.entryDatabaseDao
        repository = EntryRepository(databaseDao)
        factory = EntryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(EntryViewModel::class.java)

        viewModel.getEntry(entryId).observe(this){
            latLng = it.latLng.split(",") as MutableList<String>

            Log.d("ooooo", latLng.toString())
            Log.d("ppppp", latLng.size.toString())
            if(latLng.size > 1){
                latLng.removeLast()
                drawMap()
            }
            else
                findViewById<TextView>(R.id.tv_errormsg).setText("Did not record location data\n   Please record for longer")

            findViewById<TextView>(R.id.tv_curSpeed).setText("Cur speed: N/A")
            findViewById<TextView>(R.id.tv_type).setText("Type: " + it.activityType)

            if(units == 0 && it.units == 1){
                var roundSpeed = ((it.avgSpeed/0.621371)*100.00).toInt()/100.00
                var roundClimb = ((it.climb/0.621371)*100.00).toInt()/100.00
                var roundDistance = ((it.distance/0.621371)*100.00).toInt()/100.00
                findViewById<TextView>(R.id.tv_avgSpeed).setText("Avg speed: " + roundSpeed.toString() + " Km/h")
                findViewById<TextView>(R.id.tv_climb).setText("Climb: " + roundClimb.toString() + " Km")
                findViewById<TextView>(R.id.tv_distance).setText("Distance: " + roundDistance.toString() + " Km")
            }
            else if(units == 0 && it.units == 0){
                var roundSpeed = (it.avgSpeed*100.00).toInt()/100.00
                var roundClimb = (it.climb*100.00).toInt()/100.00
                var roundDistance = (it.distance*100.00).toInt()/100.00
                findViewById<TextView>(R.id.tv_avgSpeed).setText("Avg speed: " + roundSpeed.toString() + " Km")
                findViewById<TextView>(R.id.tv_climb).setText("Climb: " + roundClimb.toString() + " Km")
                findViewById<TextView>(R.id.tv_distance).setText("Distance: " + roundDistance.toString() + " Km")
            }
            else if(units == 1 && it.units == 0){
                var roundSpeed = ((it.avgSpeed*0.621371)*100.00).toInt()/100.00
                var roundClimb = ((it.climb*0.621371)*100.00).toInt()/100.00
                var roundDistance = ((it.distance*0.621371)*100.00).toInt()/100.00
                findViewById<TextView>(R.id.tv_avgSpeed).setText("Avg speed: " + roundSpeed.toString() + " Miles")
                findViewById<TextView>(R.id.tv_climb).setText("Climb: " + roundClimb.toString() + " Miles")
                findViewById<TextView>(R.id.tv_distance).setText("Distance: " + roundDistance.toString() + " Miles")
            }
            else if(units == 1 && it.units == 1){
                var roundSpeed = (it.avgSpeed*100.00).toInt()/100.00
                var roundClimb = (it.climb*100.00).toInt()/100.00
                var roundDistance = (it.distance*100.00).toInt()/100.00
                findViewById<TextView>(R.id.tv_avgSpeed).setText("Avg speed: " + roundSpeed.toString() + " Miles")
                findViewById<TextView>(R.id.tv_climb).setText("Climb: " + roundClimb.toString() + " Miles")
                findViewById<TextView>(R.id.tv_distance).setText("Distance: " + roundDistance.toString() + " Miles")
            }

            findViewById<TextView>(R.id.tv_calories).setText("Calories: " + it.calorie.toInt().toString())
        }

        //delete item
        findViewById<Button>(R.id.bt_save).setOnClickListener(){
            viewModel.delete(entryId)
            finish()
        }

        findViewById<Button>(R.id.bt_cancel).setOnClickListener(){
            finish()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)
    }

    private fun drawMap(){

        var latLngAr = latLng.first().split(" ")
        var latLngFirst = LatLng(latLngAr[0].toDouble(), latLngAr[1].toDouble())
        var markerStart = MarkerOptions().position(latLngFirst)

        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngFirst, 17f)
        mMap.animateCamera(cameraUpdate)

        markerStart.icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN
            )
        )
        mMap.addMarker(markerStart)
        latLngAr = latLng.last().split(" ")
        mMap.addMarker(MarkerOptions().position(LatLng(latLngAr[0].toDouble(), latLngAr[1].toDouble())))

        for(ll in latLng){
            var curAr = ll.split(" ")
            var cur = LatLng(curAr[0].toDouble(), curAr[1].toDouble())
            polylineOptions.add(cur)
            mMap.addPolyline(polylineOptions)
        }
    }
}