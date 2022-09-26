package com.cmpt362.zachary_fong_myruns

import androidx.lifecycle.ViewModel

class EntryDataViewModel: ViewModel() {
    var activityType = ""
    var inputType = ""
    var date = ""
    var time = ""
    var duration = 0
    var distance = 0F
    var calories = 0F
    var heartRate = 0
    var comment = ""
    var units = 0 //which units was saved with
    var climb = 0
    var avgSpeed = 0F
    var curSpeed = 0F
    var latlng = ArrayList<String>()
}