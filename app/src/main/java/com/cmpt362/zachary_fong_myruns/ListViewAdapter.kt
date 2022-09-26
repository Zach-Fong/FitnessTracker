package com.cmpt362.zachary_fong_myruns

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.fragment_settings.view.*

class ListViewAdapter(private var activity: FragmentActivity, private var list: List<Entry>) : BaseAdapter() {
    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Entry = list[position]

    override fun getItemId(position: Int): Long = list[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = TextView(parent?.context)
        val entry: Entry = getItem(position)

        var settingsUnits = 0
        var distance = ""
        var sharedPref = activity.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if(sharedPref != null){
            settingsUnits = sharedPref.getInt("preference", 0)
        }
        if(settingsUnits == 0 && entry.units == 1){
            distance = (entry.distance / 0.621371).toString() + " Kilometers"
        }
        else if(settingsUnits == 0 && entry.units == 0){
            distance = entry.distance.toString() + " Kilometers"
        }
        else if(settingsUnits == 1 && entry.units == 0){
            distance = (entry.distance * 0.621371).toString() + " Miles"
        }
        else if(settingsUnits == 1 && entry.units == 1){
            distance = entry.distance.toString() + " Miles"
        }
        if(position == 0){
            view.setText("${entry.inputType}: ${entry.activityType}\nDate/Time: ${entry.dateTime}\nData: $distance, ${entry.duration} secs\n")
        }
        else{
            view.setText("\n${entry.inputType}: ${entry.activityType}\nDate/Time: ${entry.dateTime}\nData: $distance, ${entry.duration} secs\n")
        }

        if(entry.inputType.equals("Manual Entry")){
            view.setOnClickListener(){
                println("MANUAL ENTRY")
                var newFragment = FragmentViewEntry.newInstance(getItemId(position))

                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_history, newFragment)
                transaction.disallowAddToBackStack()
                transaction.commit()
            }
        }
        else{
            view.setOnClickListener(){
                println("GPS ENTRY")
                var sharedPref = activity.getSharedPreferences("settings", Context.MODE_PRIVATE)
                var edit = sharedPref.edit()
                edit.putLong("id", getItemId(position))
                edit.commit()

                val activityIntent = Intent(activity, ViewMapsActivity::class.java)
                activity.startActivity(activityIntent)
            }
        }

        return view
    }

    fun updateList(newList: List<Entry>){
        list = newList
    }
}