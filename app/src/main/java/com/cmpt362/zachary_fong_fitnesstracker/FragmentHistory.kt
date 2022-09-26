package com.cmpt362.zachary_fong_fitnesstracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentHistory : Fragment() {
    private lateinit var listView: ListView

    private lateinit var database: EntryDatabase
    private lateinit var databaseDao: EntryDatabaseDAO
    private lateinit var repository: EntryRepository
    private lateinit var viewModel: EntryViewModel
    private lateinit var factory: EntryViewModelFactory

    private lateinit var listViewAdapter: ListViewAdapter
    private lateinit var arrayList: ArrayList<Entry>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        listView = view.findViewById(R.id.lv_history)
        database = EntryDatabase.getInstance(requireActivity())
        databaseDao = database.entryDatabaseDao
        repository = EntryRepository(databaseDao)
        factory = EntryViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(EntryViewModel::class.java)

        arrayList = ArrayList()
        listViewAdapter = ListViewAdapter(requireActivity(), arrayList)
        listView.adapter = listViewAdapter

        //tell adapter list is updated to update view
        viewModel.allEntriesLiveData.observe(requireActivity()){
            listViewAdapter.updateList(it)
            listViewAdapter.notifyDataSetChanged()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if(listViewAdapter != null){
            listViewAdapter.notifyDataSetChanged()
        }
    }
}