package com.cmpt362.zachary_fong_myruns

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import com.cmpt362.zachary_fong_myruns.databinding.FragmentStartBinding
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_start.*

class FragmentStart : Fragment() {
    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentStartBinding.inflate(inflater, container, false)

        val inputTypes = arrayOf("Manual Entry","GPS","Automatic")
        val activityTypes = arrayOf("Running","Walking","Standing","Cycling","Hiking","Downhill Skiing","Cross-Country Skiing","Snowboarding","Skating",
        "Swimming","Mountain Biking","Wheelchair","Elliptical","Other")
        val inputArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, inputTypes)
        val activityArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, activityTypes)
        binding.spinnerInput.setAdapter(inputArrayAdapter)
        binding.spinnerActivity.setAdapter(activityArrayAdapter)

        binding.buttonStart.setOnClickListener(){

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (transaction != null) {
                if(binding.spinnerInput.selectedItem.toString().equals("Manual Entry")){
                    transaction.replace(R.id.fragment_start, FragmentStartInput.newInstance(binding.spinnerActivity.selectedItem.toString(), binding.spinnerInput.selectedItem.toString()))
                    transaction.disallowAddToBackStack()
                    transaction.commit()
                }
                else{
                    var sharedPref = requireActivity().getSharedPreferences("startTypes", Context.MODE_PRIVATE)
                    var editor = sharedPref.edit()
                    editor.putString("inputType", binding.spinnerInput.selectedItem.toString())
                    if(binding.spinnerInput.selectedItem.toString().equals("GPS")){
                        editor.putString("activityType", binding.spinnerActivity.selectedItem.toString())
                    }
                    editor.commit()
                    val intent = Intent(requireContext(), MapsActivity::class.java)
                    requireContext().startActivity(intent)
                }
            }

        }
        return binding.root
    }
}