package com.cmpt362.zachary_fong_fitnesstracker

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_startinput.view.*
import java.text.SimpleDateFormat
import java.util.*

private var activityType = ""
private var inputType = ""

class FragmentStartInput : Fragment() {


    lateinit var entryDataViewModel: EntryDataViewModel
    lateinit var dialog: Dialog

    private lateinit var database: EntryDatabase
    private lateinit var databaseDao: EntryDatabaseDAO
    private lateinit var repository: EntryRepository
    private lateinit var viewModel: EntryViewModel
    private lateinit var factory: EntryViewModelFactory
    private var units = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            activityType = it.getString("activityType", "")
            inputType = it.getString("inputType", "")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_startinput, container, false)

        entryDataViewModel = ViewModelProvider(this).get(EntryDataViewModel::class.java)
        database = EntryDatabase.getInstance(requireActivity())
        databaseDao = database.entryDatabaseDao
        repository = EntryRepository(databaseDao)
        factory = EntryViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(EntryViewModel::class.java)
        viewModel.allEntriesLiveData.observe(requireActivity()){
            for(entry in it){
                Log.d("Entry: ", entry.toString())
            }
            Log.d("Entry", it.size.toString())
        }

        entryDataViewModel.activityType = activityType
        entryDataViewModel.inputType = inputType

        view.tv_date.setOnClickListener(){
            var calendar = Calendar.getInstance()
            val dateSetListener = object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    entryDataViewModel.date = SimpleDateFormat("MMM dd yyyy").format(calendar.time)
                    Log.d("date", entryDataViewModel.date)
                }
            }
            val datePickerDialog = DatePickerDialog(requireContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }

        view.tv_time.setOnClickListener(){
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                entryDataViewModel.time = SimpleDateFormat("HH:mm").format(cal.time)
                Log.d("time", entryDataViewModel.time)
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        view.tv_duration.setOnClickListener(){
            val builder = AlertDialog.Builder(requireActivity())
            val viewInner = layoutInflater.inflate(R.layout.dialog_entertext, null)
            val tv_data = viewInner.findViewById<TextView>(R.id.tv_data)
            builder.setView(viewInner)
            builder.setTitle("Duration")
            builder.setPositiveButton("OK"){ dialog, which ->
                if(tv_data.text == "" || tv_data.text.toString().toIntOrNull() == null){
                    entryDataViewModel.duration = 0
                }
                else{
                    entryDataViewModel.duration = tv_data.text.toString().toInt()
                }
                Log.d("duration", entryDataViewModel.duration.toString())
            }
            dialog = builder.create()
            dialog.show()
        }

        view.tv_distance.setOnClickListener(){
            val builder = AlertDialog.Builder(requireActivity())
            val viewInner = layoutInflater.inflate(R.layout.dialog_entertext, null)
            val tv_data = viewInner.findViewById<TextView>(R.id.tv_data)
            builder.setView(viewInner)
            builder.setTitle("Distance")
            builder.setPositiveButton("OK"){ dialog, which ->
                if(tv_data.text == "" || tv_data.text.toString().toIntOrNull() == null){
                    entryDataViewModel.distance = 0F
                }
                else{
                    entryDataViewModel.distance = tv_data.text.toString().toFloat()
                }
                Log.d("distance", entryDataViewModel.distance.toString())
            }
            dialog = builder.create()
            dialog.show()
        }

        view.tv_calories.setOnClickListener(){
            val builder = AlertDialog.Builder(requireActivity())
            val viewInner = layoutInflater.inflate(R.layout.dialog_entertext, null)
            val tv_data = viewInner.findViewById<TextView>(R.id.tv_data)
            builder.setView(viewInner)
            builder.setTitle("Calories")
            builder.setPositiveButton("OK"){ dialog, which ->
                if(tv_data.text == "" || tv_data.text.toString().toIntOrNull() == null){
                    entryDataViewModel.calories = 0F
                }
                else{
                    entryDataViewModel.calories = tv_data.text.toString().toFloat()
                }
                Log.d("calories", entryDataViewModel.calories.toString())
            }
            dialog = builder.create()
            dialog.show()
        }

        view.tv_heartRate.setOnClickListener(){
            val builder = AlertDialog.Builder(requireActivity())
            val viewInner = layoutInflater.inflate(R.layout.dialog_entertext, null)
            val tv_data = viewInner.findViewById<TextView>(R.id.tv_data)
            builder.setView(viewInner)
            builder.setTitle("Heart Rate")
            builder.setPositiveButton("OK"){ dialog, which ->
                if(tv_data.text == "" || tv_data.text.toString().toIntOrNull() == null){
                    entryDataViewModel.heartRate = 0
                }
                else{
                    entryDataViewModel.heartRate = tv_data.text.toString().toInt()
                }
                Log.d("heart rate", entryDataViewModel.heartRate.toString())
            }
            dialog = builder.create()
            dialog.show()
        }

        view.tv_comment.setOnClickListener(){
            val builder = AlertDialog.Builder(requireActivity())
            val viewInner = layoutInflater.inflate(R.layout.dialog_entertext, null)
            val tv_data = viewInner.findViewById<TextView>(R.id.tv_data)
            builder.setView(viewInner)
            builder.setTitle("Comment")
            builder.setPositiveButton("OK"){ dialog, which ->
                entryDataViewModel.comment = tv_data.text.toString()
                Log.d("Comment", entryDataViewModel.comment)
            }
            dialog = builder.create()
            dialog.show()

        }

        view.button_cancel.setOnClickListener(){
            //resets viewmodel objects data so it does not mess up data
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (transaction != null) {
                transaction.remove(this)
                transaction.commit()

                entryDataViewModel.date = ""
                entryDataViewModel.time = ""
                entryDataViewModel.duration = 0
                entryDataViewModel.distance = 0F
                entryDataViewModel.calories = 0F
                entryDataViewModel.heartRate = 0
                entryDataViewModel.comment = ""
            }
        }

        view.button_save.setOnClickListener(){
            //saves all data into database with specified data type, and any other changes required
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (transaction != null) {
                transaction.remove(this)
                transaction.commit()

                var sharedPref = requireActivity()?.getSharedPreferences("settings", Context.MODE_PRIVATE)
                if(sharedPref != null){
                    units = sharedPref.getInt("preference", 0)
                    Log.d("units", units.toString())
                }
                entryDataViewModel.units = units

                val entryObj = Entry()
                entryObj.inputType = entryDataViewModel.inputType
                entryObj.activityType = entryDataViewModel.activityType
                entryObj.dateTime = entryDataViewModel.date + " " + entryDataViewModel.time
                if(entryObj.dateTime.equals(" ")){
                    val sdf = SimpleDateFormat("MMM dd yyyy HH:mm")
                    val curDate = sdf.format(Date())
                    entryObj.dateTime = curDate
                }
                entryObj.duration = entryDataViewModel.duration
                entryObj.distance = entryDataViewModel.distance
                entryObj.units = entryDataViewModel.units
                if(entryDataViewModel.duration == 0)
                    entryObj.avgPace = 0F
                else{
                    entryObj.avgPace = entryDataViewModel.distance / entryDataViewModel.duration
                }
                entryObj.calorie = entryDataViewModel.calories
                entryObj.climb = 0F //MUST CHANGE LATER NOTE
                entryObj.heartRate = entryDataViewModel.heartRate
                entryObj.comment = entryDataViewModel.comment

                viewModel.insert(entryObj)
            }
        }

        return view
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(activityType: String, inputType: String) =
            FragmentStartInput().apply {
                arguments = Bundle().apply {
                    putString("activityType", activityType)
                    putString("inputType", inputType)
                }
            }
    }

}