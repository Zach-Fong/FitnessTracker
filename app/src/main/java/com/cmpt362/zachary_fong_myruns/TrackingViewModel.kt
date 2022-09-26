package com.cmpt362.zachary_fong_myruns

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrackingViewModel: ViewModel(), ServiceConnection {
    private var myMessageHandler: MyMessageHandler = MyMessageHandler(Looper.getMainLooper())
    var locations = MutableLiveData<ArrayList<String>>()
    var curSpeed = MutableLiveData<Float>()
    var avgSpeed = MutableLiveData<Float>()
    var totalSpeed = MutableLiveData<Float>()
    var totalDistance = MutableLiveData<Float>()
    var climb = MutableLiveData<Double>()
    var time = MutableLiveData<Int>()
    var activityType = MutableLiveData<String>()
    var lastPos = -1

    init {
        locations.value = ArrayList<String>()
        curSpeed.value = 0F
        avgSpeed.value = 0F
        time.value = 0
        totalSpeed.value = 0F
        totalDistance.value = 0F
        climb.value = 0.0
        activityType.value = "None"
    }

    override fun onServiceConnected(name: ComponentName?, iBinder: IBinder) {
        println("SERVICE CONNECTED")
        val tempBinder = iBinder as NotifyService.MyBinder
        tempBinder.setMsgHandler(myMessageHandler)
    }

    override fun onServiceDisconnected(name: ComponentName?){
    }

    inner class MyMessageHandler(looper: Looper): Handler(looper){
        override fun handleMessage(msg: Message) {
            val bundle = msg.data

            if(msg.what == 2){
                time.value = bundle.getInt("counter", 1)
                curSpeed.value = bundle.getFloat("curSpeed", 0F) * 3.6F
                totalSpeed.value = totalSpeed.value?.plus(curSpeed.value!!)
                avgSpeed.value = totalSpeed.value?.div(time.value!!)
//                println("LATLNG: ${locations.value}")
//                println("Time: ${time.value}\nDistance: ${totalDistance.value}\nClimb: ${climb.value}\nCurSpeed:${curSpeed.value}\navgSpeed: ${avgSpeed.value}\n\n")
            }
            else if(msg.what == 3){
                activityType.value = bundle.getString("activity")
                println("MAXTYPE receiving: ${activityType.value}")
            }
            else{
                var newDistance = bundle.getFloat("distance", 0F)/1000
                totalDistance.value = (totalDistance.value?.plus(newDistance))

                var temp = locations.value
                temp!!.add(bundle.getString("latlng", ""))
//                locations.value = ArrayList<String>()
                locations.value = temp!!

                climb.value = bundle.getDouble("climb", 0.0)/1000

//                println("Time: ${time.value}\nDistance: ${totalDistance.value}\nClimb: ${climb.value}\nCurSpeed:${curSpeed.value}\navgSpeed: ${avgSpeed.value}\n\n")
            }

        }
    }

}