package com.cmpt362.zachary_fong_myruns

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel:ViewModel() {
    val userImg = MutableLiveData<Bitmap>()
}