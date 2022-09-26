package com.cmpt362.zachary_fong_myruns

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_unitpreference.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class FragmentSettings : Fragment() {
    private var privacySettings = false
    private var unitPreference = -1
    private var comments = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)


        var privacySettingsView = view.checkbox_privacySettings
        var unitPreferenceView = view.tv_unitPreference
        var commentsView = view.tv_comments
        var privacySettings = false
        var unitPreference = -1
        var comments = ""
        var sharedPref = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        var edit = sharedPref?.edit()
        if(sharedPref != null){
            privacySettings = sharedPref.getBoolean("privacySettings", false)
            unitPreference = sharedPref.getInt("preference", 0)
            comments = sharedPref.getString("comments", "").toString()
        }

        view.checkbox_privacySettings.setOnClickListener(){
            edit?.putBoolean("privacySettings", view.checkbox_privacySettings.isChecked)
            edit?.commit()
        }

        if(privacySettings == true){
            privacySettingsView.setChecked(true)
        }
        else if(privacySettings == false){
            privacySettingsView.setChecked(false)
        }

        view.userProfile.setOnClickListener(){ view->
            val intent = Intent(this@FragmentSettings.requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        view.tv_unitPreference.setOnClickListener(){
            val unitPreferenceDialog = MyDialog()
            val unitPreferenceBundle = Bundle()
            unitPreferenceBundle.putInt(MyDialog.DIALOG_KEY, MyDialog.PREFERENCE_DIALOG)
            unitPreferenceBundle.putInt("preference", unitPreference)
            unitPreferenceDialog.arguments = unitPreferenceBundle
            unitPreferenceDialog.show(parentFragmentManager, "unitPreferenceDialog")
            Log.d("units", unitPreference.toString())
        }

//        view.tv_comments.setOnClickListener(){
//            val commentsDialog = MyDialog()
//            val commentsBundle = Bundle()
//            commentsBundle.putInt(MyDialog.DIALOG_KEY, MyDialog.COMMENTS_DIALOG)
//            commentsBundle.putString("comments", comments)
//            commentsDialog.arguments = commentsBundle
//            commentsDialog.show(parentFragmentManager, "commentsDialog")
//        }

        view.tv_webpage.setOnClickListener(){
            val url = "https://www.sfu.ca/computing.html"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        return view
    }
}