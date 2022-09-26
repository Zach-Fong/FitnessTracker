package com.cmpt362.zachary_fong_fitnesstracker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var startFragment: FragmentStart
    private lateinit var historyFragment: FragmentHistory
    private lateinit var settingsFragment: FragmentSettings
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var tab: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var myFragmentStateAdapter: FragmentStateAdapter
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy
    private val TAB_TEXT = arrayOf("Start","History","Settings")
    private val PERMISSION_REQUEST_CODE = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Util.checkPermissions(this)

        startFragment = FragmentStart()
        historyFragment = FragmentHistory()
        settingsFragment = FragmentSettings()
        fragments = ArrayList()
        fragments.add(startFragment)
        fragments.add(historyFragment)
        fragments.add(settingsFragment)

        tab = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        myFragmentStateAdapter = MyFragmentStateAdapter(this, fragments)
        viewPager.adapter = myFragmentStateAdapter

        tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy(){
            tab: TabLayout.Tab, position: Int ->
            tab.text = TAB_TEXT[position]
        }
        tabLayoutMediator = TabLayoutMediator(tab, viewPager, tabConfigurationStrategy)
        tabLayoutMediator.attach()

    }

    override fun onResume() {
        super.onResume()
        val intent = Intent()
        intent.action = NotifyService.ACTION_STOP
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}