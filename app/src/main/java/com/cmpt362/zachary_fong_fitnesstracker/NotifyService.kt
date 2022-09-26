package com.cmpt362.zachary_fong_fitnesstracker

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.maps.model.LatLng
import weka.core.Attribute
import weka.core.DenseInstance
import java.lang.Math.round
import java.lang.Math.sqrt
import java.math.BigDecimal
import java.util.*
import kotlin.math.roundToInt
import weka.core.Instance
import java.text.DecimalFormat

class NotifyService: Service(), LocationListener, SensorEventListener {
    private lateinit var notificationManager: NotificationManager
    private lateinit var myBroadcastReceiver: BroadcastReceiver

    private lateinit var notification: Notification

    lateinit var myBinder: MyBinder
    private lateinit var locationManager: LocationManager
    private val CHANNEL_ID = "channel_id"
    private val NOTIFY_ID = 1
    private var msgHandler: Handler? = null

    private lateinit var myTask: MyTask
    private lateinit var timer: Timer
    private lateinit var prevLocation: Location
    private var prevAltitude = 0.0
    private var counter = 0
    private var curSpeed = 0F

    private lateinit var sensorManager: SensorManager
    private lateinit var mClassAttribute: Attribute
    private var accBlock = DoubleArray(64)
    private var blockSize = 0
    private var im = DoubleArray(64)
    private var max = 0.0
    private var label ="label"
    private var typeList = ArrayList<String>()

    companion object{
        val ACTION_STOP = "stop it"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        initLocationManager()

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        showNotification()

        myBinder = MyBinder()

        myBroadcastReceiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_STOP)
        registerReceiver(myBroadcastReceiver, intentFilter)

        myTask = MyTask()
        timer = Timer()
        timer.scheduleAtFixedRate(myTask, 0, 1000L)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        val labelItems = ArrayList<String>(3)
        labelItems.add("Standing")
        labelItems.add("Walking")
        labelItems.add("Running")
        mClassAttribute = Attribute("label", labelItems)

    }

    override fun onBind(intent: Intent?): IBinder? {
        println("ONBIND() CALLED")
        return myBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    inner class MyBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            notificationManager.cancel(NOTIFY_ID)
            stopSelf()
            unregisterReceiver(myBroadcastReceiver)
        }
    }

    private fun showNotification(){

        val firstIntent = Intent(this, MapsActivity::class.java)
        val openAppIntent = PendingIntent.getActivity(this, 0, firstIntent, 0)

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )

        notificationBuilder.setContentTitle("Fitness Tracker")
        notificationBuilder.setContentText("Service is on")
        notificationBuilder.setSmallIcon(R.drawable.ic_baseline_location_on_24)
        notificationBuilder.setContentIntent(openAppIntent)
        notification = notificationBuilder.build()

        if(Build.VERSION.SDK_INT > 26){
            val notificationChannel = NotificationChannel(CHANNEL_ID, "fitnesstracker channel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(NOTIFY_ID, notification)
    }

    inner class MyBinder: Binder(){
        fun setMsgHandler(inputHandler: Handler){
            msgHandler = inputHandler
        }
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
        } catch (e: SecurityException){
            println("ERROR: LOCATION")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        if(locationManager != null){
            locationManager.removeUpdates(this)
        }
        unregisterReceiver(myBroadcastReceiver)
        timer.cancel()
        counter = 0
        sensorManager.unregisterListener(this)
        println("RESULTS: $typeList")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onLocationChanged(location: Location) {
//        println("Location updated")
        if(counter == 0){
            prevLocation = location
        }
        if(prevAltitude == 0.0) prevAltitude = location.altitude

        val lat = location.latitude
        val lng = location.longitude
        var latLng = "$lat $lng"
        curSpeed = (location.speedAccuracyMetersPerSecond*100).roundToInt()/100F
        if(msgHandler != null){
//            println("SENDING MESSAGE")
            val bundle = Bundle()
            bundle.putString("latlng", latLng)
            bundle.putFloat("distance", location.distanceTo(prevLocation))
            bundle.putDouble("climb", prevAltitude - location.altitude)
            val message = msgHandler!!.obtainMessage()
            message.data = bundle
            message.what = 1
            msgHandler!!.sendMessage(message)
        }
        prevLocation = location
    }

    inner class MyTask: TimerTask(){
        override fun run() {
            try{
                counter += 1
                if(msgHandler != null){
                    val bundle = Bundle()
                    bundle.putInt("counter", counter)
                    bundle.putFloat("curSpeed", curSpeed)
                    val message = msgHandler!!.obtainMessage()
                    message.data = bundle
                    message.what = 2
                    msgHandler!!.sendMessage(message)
                }
            }catch(t: Throwable){
                println("FAILED")
            }
        }

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER){
//            var x = (event.values.get(0) / SensorManager.GRAVITY_EARTH).toDouble()
//            var y = (event.values.get(1) / SensorManager.GRAVITY_EARTH).toDouble()
//            var z = (event.values.get(2) / SensorManager.GRAVITY_EARTH).toDouble()

            var m = Math.sqrt((event.values[0] * event.values[0] + event.values[1] * event.values[1] + (event.values[2] * event.values[2])).toDouble())
            accBlock[blockSize++] = m

            val fft = FFT(64)
            val inst: Instance = DenseInstance(66)

            if(blockSize == 64){
                blockSize = 0
                max = Double.MIN_VALUE

                for(temp in accBlock){
                    if(temp > max) max = temp
                }

                fft.fft(accBlock, im)

                for(i in accBlock.indices){
                    val mag = Math.sqrt(accBlock[i] * accBlock[i] + im[i]
                            * im[i])
                    inst.setValue(i, mag)
                    im[i] = .0 // Clear the field
                    if(i == 0) println("FIRST INST: $mag")
                }

                inst.setValue(64, max)

                val labelVal = WekaClassifier.classify(inst.toDoubleArray().toTypedArray())
                var labelName = "none"

                if(labelVal == 0.0) labelName = "standing"
                else if(labelVal == 1.0) labelName = "walking"
                else if (labelVal == 2.0) labelName = "running"
                println("CLASSIFIED: $labelName\nVALUE[0]: ${inst.value(0)}")

                typeList.add(labelName)

                val typeNums = typeList.groupingBy { it }.eachCount()
                var maxType = typeNums.maxByOrNull { it.value }?.key
                if(maxType == null) maxType = "None"

                if(msgHandler != null){
                    val bundle = Bundle()
                    bundle.putString("activity", maxType)
                    println("MAXTYPE sending: $maxType")
                    val message = msgHandler!!.obtainMessage()
                    message.data = bundle
                    message.what = 3
                    msgHandler!!.sendMessage(message)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

}