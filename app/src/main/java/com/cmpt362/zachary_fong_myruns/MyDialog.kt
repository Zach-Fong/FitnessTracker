package com.cmpt362.zachary_fong_myruns

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
        const val COMMENTS_DIALOG = 2
        const val TIME_DIALOG = 3
        const val DURATION_DIALOG = 4
        const val DISTANCE_DIALOG = 5
        const val CALORIES_DIALOG = 6
        const val HEARTRATE_DIALOG = 7
        const val COMMENT_DIALOG = 8
        const val DIALOG_KEY = "key"
    }
    lateinit var commentsView: TextView
    lateinit var sharedPreferenceView: RadioGroup

    private var date = ""
    private var time = ""
    private var duration = 0
    private var distance = 0
    private var calories = 0
    private var heartRate = 0
    private var comment = ""

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
        else if(bundle?.getInt(DIALOG_KEY) == COMMENTS_DIALOG){
            var sharedPref = activity?.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_entertext, null)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(view)
            builder.setTitle("Comments")
            builder.setPositiveButton("OK") { dialog, which ->
                var edit = sharedPref?.edit()
                if (edit != null) {
                    var comments = commentsView.text.toString()
                    edit.putString("comments", comments)
                    edit.commit()
                }
            }
            builder.setNegativeButton("Cancel", this)
            dialog = builder.create()
            dialog.show()
            commentsView = dialog.tv_comments
            commentsView.setText(sharedPref?.getString("comments", ""))
        }
//        else if(bundle?.getInt(DIALOG_KEY) == TIME_DIALOG){
//            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_timepicker, null)
//            val builder = AlertDialog.Builder(requireActivity())
//            builder.setView(view)
//            builder.setPositiveButton("OK", this)
//            builder.setNegativeButton("Cancel"){ dialog, which ->
//
//            }
//            dialog = builder.create()
//        }
        else if(bundle?.getInt(DIALOG_KEY) == DURATION_DIALOG){
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_entertext, null)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(view)
            builder.setTitle("Duration")
            builder.setPositiveButton("OK", this)
            builder.setNegativeButton("Cancel", this)
            dialog = builder.create()
        }
        else if(bundle?.getInt(DIALOG_KEY) == DISTANCE_DIALOG){
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_entertext, null)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(view)
            builder.setTitle("Distance")
            builder.setPositiveButton("OK", this)
            builder.setNegativeButton("Cancel", this)
            dialog = builder.create()
        }
        else if(bundle?.getInt(DIALOG_KEY) == CALORIES_DIALOG){
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_entertext, null)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(view)
            builder.setTitle("Calories")
            builder.setPositiveButton("OK", this)
            builder.setNegativeButton("Cancel", this)
            dialog = builder.create()
        }
        else if(bundle?.getInt(DIALOG_KEY) == HEARTRATE_DIALOG){
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_entertext, null)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(view)
            builder.setTitle("Heart Rate")
            builder.setPositiveButton("OK", this)
            builder.setNegativeButton("Cancel", this)
            dialog = builder.create()
        }
        else if(bundle?.getInt(DIALOG_KEY) == COMMENT_DIALOG){
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_entertext, null)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(view)
            builder.setTitle("Comment")
            builder.setPositiveButton("OK", this)
            builder.setNegativeButton("Cancel", this)
            dialog = builder.create()
        }

        return dialog
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

    }
}