package com.cmpt362.zachary_fong_fitnesstracker

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_timepicker.*
import kotlinx.android.synthetic.main.dialog_unitpreference.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.text.SimpleDateFormat
import java.util.*

class MyDialog: DialogFragment(), DialogInterface.OnClickListener {

    companion object{
        const val PREFERENCE_DIALOG = 1
        const val DIALOG_KEY = "key"
    }
    lateinit var sharedPreferenceView: RadioGroup

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog: Dialog

        val bundle = arguments

        if(bundle?.getInt(DIALOG_KEY) == PREFERENCE_DIALOG){
            var sharedPref = activity?.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_unitpreference, null)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(view)
            builder.setTitle("Unit Preference")
            builder.setPositiveButton("OK"){ dialog, which->
                var edit = sharedPref?.edit()
                if(edit != null){
                    var sharedpreference = sharedPreferenceView.checkedRadioButtonId

                    var units = 0
                    if(view.findViewById<RadioButton>(sharedpreference).getText().toString().equals("Kilometers") == false){
                        units = 1
                    }
                    edit.putInt("preference", units)

                    edit.putInt("units_rb_id", sharedpreference)
                    edit.commit()
                }
            }
            builder.setNegativeButton("cancel", this)
            dialog = builder.create()
            dialog.show()
            sharedPreferenceView = dialog.rg_unitPreference
            if (sharedPref != null) {
                sharedPreferenceView.check(sharedPref.getInt("units_rb_id", 0))
            }
        }

        return dialog
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

    }
}