package com.cmpt362.zachary_fong_myruns

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class FragmentViewEntry : Fragment() {
    // TODO: Rename and change types of parameters
    private var entryId = 0L
    private var units = 0

    private lateinit var database: EntryDatabase
    private lateinit var databaseDao: EntryDatabaseDAO
    private lateinit var repository: EntryRepository
    private lateinit var viewModel: EntryViewModel
    private lateinit var factory: EntryViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            entryId = it.getLong("id", 0L)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_entry, container, false)

        var sharedPref = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        if(sharedPref != null){
            units = sharedPref.getInt("preference", 0)
        }


        database = EntryDatabase.getInstance(requireActivity())
        databaseDao = database.entryDatabaseDao
        repository = EntryRepository(databaseDao)
        factory = EntryViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(EntryViewModel::class.java)

        //populate view entry with correct information
        viewModel.getEntry(entryId).observe(requireActivity()){
            view.findViewById<TextView>(R.id.tv_inputType).setText(it.inputType)
            view.findViewById<TextView>(R.id.tv_activityType).setText(it.activityType)
            view.findViewById<TextView>(R.id.tv_dateTime).setText(it.dateTime)
            view.findViewById<TextView>(R.id.tv_duration).setText(it.duration.toString() + " seconds")

            if(units == 0 && it.units == 1){
                view.findViewById<TextView>(R.id.tv_distance).setText((it.distance / 0.621371).toString() + " Kilometers")
            }
            else if(units == 0 && it.units == 0){
                view.findViewById<TextView>(R.id.tv_distance).setText(it.distance.toString() + " Kilometers")
            }
            else if(units == 1 && it.units == 0){
                view.findViewById<TextView>(R.id.tv_distance).setText((it.distance * 0.621371).toString() + " Miles")
            }
            else if(units == 1 && it.units == 1){
                view.findViewById<TextView>(R.id.tv_distance).setText(it.distance.toString() + " Miles")
            }

            view.findViewById<TextView>(R.id.tv_calories).setText(it.calorie.toString())
            view.findViewById<TextView>(R.id.tv_heartRate).setText(it.heartRate.toString() + "bpm")
        }

        //delete item
        view.findViewById<Button>(R.id.bt_delete).setOnClickListener(){
            viewModel.delete(entryId)
            val transaction = requireActivity().supportFragmentManager?.beginTransaction()
            transaction.remove(this)
            transaction.commit()
        }

        view.findViewById<Button>(R.id.bt_cancel).setOnClickListener(){
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.remove(this)
            transaction.commit()
        }

        return view
    }



    companion object {
        @JvmStatic
        fun newInstance(id: Long) =
            FragmentViewEntry().apply {
                arguments = Bundle().apply {
                    putLong("id", id)
                }
            }
    }
}