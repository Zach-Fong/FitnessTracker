package com.cmpt362.zachary_fong_myruns

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import com.cmpt362.zachary_fong_myruns.databinding.ActivityMapsBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ln

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val PERMISSION_REQUEST_CODE = 0
    private var isCenter = false
    private var isBound = false
    private lateinit var locationManager: LocationManager
    private lateinit var curMarkerOptions: MarkerOptions
    private lateinit var curMarker: Marker
    private lateinit var startMarkerOptions: MarkerOptions
    private lateinit var curLatLng: LatLng
    private lateinit var startLatLng: LatLng
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var serviceIntent: Intent
    private lateinit var trackingViewModel: TrackingViewModel

    lateinit var entryDataViewModel: EntryDataViewModel
    private lateinit var database: EntryDatabase
    private lateinit var databaseDao: EntryDatabaseDAO
    private lateinit var repository: EntryRepository
    private lateinit var viewModel: EntryViewModel
    private lateinit var factory: EntryViewModelFactory
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityMapsBinding.inflate(layoutInflater)
         setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapFragment.retainInstance = true

        entryDataViewModel = ViewModelProvider(this).get(EntryDataViewModel::class.java)
        database = EntryDatabase.getInstance(this)
        databaseDao = database.entryDatabaseDao
        repository = EntryRepository(databaseDao)
        factory = EntryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(EntryViewModel::class.java)

        val sdf = SimpleDateFormat("MMM dd yyyy HH:mm")
        entryDataViewModel.date = sdf.format(Date())
        sharedPref = getSharedPreferences("startTypes", Context.MODE_PRIVATE)
        entryDataViewModel.activityType = sharedPref.getString("activityType", "N/A").toString()
        entryDataViewModel.inputType = sharedPref.getString("inputType", "").toString()

        var newPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
        entryDataViewModel.units = newPref.getInt("preference", 0)

        binding.btSave.setOnClickListener(){
            val entryObj = Entry()

            println("DISCONNECTED: TRACKINGVIEWMODEL\n\n")
            var latlngString = ""
            for(i in entryDataViewModel.latlng){
                latlngString += "$i,"
            }
            entryObj.latLng= latlngString
            entryObj.dateTime = entryDataViewModel.date
            entryObj.inputType = entryDataViewModel.inputType
            entryObj.activityType = entryDataViewModel.activityType
            entryObj.duration = entryDataViewModel.duration
            entryObj.distance = entryDataViewModel.distance
            entryObj.units = entryDataViewModel.units
            entryObj.avgSpeed = entryDataViewModel.avgSpeed
            entryObj.calorie = entryDataViewModel.calories
            entryObj.climb = entryDataViewModel.climb.toFloat()
            viewModel.insert(entryObj)

            println("LATLNG final: ${entryDataViewModel.latlng.toString()}")

            unbindService()
            this.stopService(serviceIntent)
            finish()
        }

        binding.btCancel.setOnClickListener(){
            unbindService()
            this.stopService(serviceIntent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        curMarkerOptions = MarkerOptions()
        startMarkerOptions = MarkerOptions()
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)

        initLocationManager()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun initLocationManager(){
        try{
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider = locationManager.getBestProvider(criteria, true)
            val location = locationManager.getLastKnownLocation(provider!!)
            if(location != null)
                onLocationChanged(location)
            locationManager.requestLocationUpdates(provider, 0, 0f, this)

            serviceIntent = Intent(this, NotifyService::class.java)

            startForegroundService(serviceIntent)

            trackingViewModel = ViewModelProvider(this).get(TrackingViewModel::class.java)
            bindService()

            trackingViewModel.locations.observe(this){
                entryDataViewModel.latlng = it
//                println("entry added")
            }
            trackingViewModel.curSpeed.observe(this){
                entryDataViewModel.curSpeed = it
                var res = handleUnits(it)
                findViewById<TextView>(R.id.tv_curSpeed).setText("Cur speed: $res/h")
            }
            trackingViewModel.avgSpeed.observe(this){
                entryDataViewModel.avgSpeed = it
                var res = handleUnits(it)
                findViewById<TextView>(R.id.tv_avgSpeed).setText("Avg speed: $res/h")
            }
            trackingViewModel.totalDistance.observe(this){
                entryDataViewModel.distance = it
                var res = handleUnits(it)
                entryDataViewModel.calories = (66.666666666666F * it)
                var rounded = (entryDataViewModel.calories * 10).toInt()/10
                findViewById<TextView>(R.id.tv_distance).setText("Distance: $res")
                findViewById<TextView>(R.id.tv_calories).setText("Calories: $rounded")
            }
            trackingViewModel.climb.observe(this){
                var res = handleUnits(it.toFloat())
                findViewById<TextView>(R.id.tv_climb).setText("Climb: $res")
            }
            trackingViewModel.time.observe(this){
                entryDataViewModel.duration = it
            }

            if(entryDataViewModel.inputType.equals("Automatic")){
                trackingViewModel.activityType.observe(this){
                    entryDataViewModel.activityType = it
                    findViewById<TextView>(R.id.tv_type).setText("Type: $it")
                    println("NEW TYPE: $it")
                }
            }

            findViewById<TextView>(R.id.tv_type).setText("Type: ${entryDataViewModel.activityType}")
            if(entryDataViewModel.inputType.equals("Automatic")) findViewById<TextView>(R.id.tv_type).setText("Type: None")


        } catch (e: SecurityException){
            println("ERROR: LOCATION")
        }
    }

    private fun handleUnits(value: Float): String {
        var rounded = (value*100.00).toInt()/100.00

        if(entryDataViewModel.units == 0){
            return "$rounded Km"
        }
        else{
            val newValue = value * 0.621371
            val newValueRounded = (newValue*100.00).toInt()/100.00
            return "$newValueRounded M"
        }
    }

    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        curLatLng = LatLng(lat, lng)

        if (isCenter) {
            curMarkerOptions.position(curLatLng)
            curMarker.remove()
            curMarker = mMap.addMarker(curMarkerOptions)!!
        }

        if (!isCenter) {
            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(curLatLng, 17f)
            mMap.animateCamera(cameraUpdate)
            isCenter = true

            startLatLng = curLatLng
            startMarkerOptions.position(startLatLng)
            startMarkerOptions.icon(
                BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_GREEN
                )
            )
            mMap.addMarker(startMarkerOptions)

            curMarker = mMap.addMarker(startMarkerOptions)!!
        }
        polylineOptions.add(curLatLng)
        mMap.addPolyline(polylineOptions)
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        if(locationManager != null){
            locationManager.removeUpdates(this)
        }
        unbindService()
        stopService(serviceIntent)
        println("DESTROYED")
    }

    fun bindService(){
        if(!isBound){
            this.bindService(serviceIntent, trackingViewModel, Context.BIND_AUTO_CREATE)
            isBound = true
            println("called bindService")
        }
    }

    fun unbindService(){
        if(isBound){
            this.unbindService(trackingViewModel)
            isBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        if(isCenter){

            if(trackingViewModel.lastPos >= 0){
                var firstll = entryDataViewModel.latlng[0].split(" ")
                startLatLng = LatLng(firstll[0].toDouble(), firstll[1].toDouble())

                var lastLatLng = LatLng(0.0,0.0)
                for(i in trackingViewModel.lastPos..entryDataViewModel.latlng.size-1){
                    var latLng = entryDataViewModel.latlng[i].split(" ")
                    var lat = latLng[0].toDouble()
                    var lng = latLng[1].toDouble()
                    lastLatLng = LatLng(lat, lng)
                    polylineOptions.add(lastLatLng)
                    mMap.addPolyline(polylineOptions)
                }
                curMarker.remove()
                curMarkerOptions.position(lastLatLng)
                curMarker = mMap.addMarker(curMarkerOptions)!!
            }
        }
    }

    override fun onPause() {
        super.onPause()
        trackingViewModel.lastPos = entryDataViewModel.latlng.size - 1
        curMarker.remove()
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
}