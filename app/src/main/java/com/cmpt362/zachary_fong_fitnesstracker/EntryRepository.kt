package com.cmpt362.zachary_fong_fitnesstracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntryRepository(private val entryDatabaseDAO: EntryDatabaseDAO) {
    val allEntries: Flow<List<Entry>> = entryDatabaseDAO.getAllEntries()

    fun insert(entry: Entry){
        CoroutineScope(IO).launch{
            entryDatabaseDAO.insertEntry(entry)
        }
    }

    fun delete(id: Long){
        CoroutineScope(IO).launch {
            entryDatabaseDAO.deleteEntry(id)
        }
    }

    fun getEntry(id: Long): MutableLiveData<Entry>{
        var liveEntry = MutableLiveData<Entry>()

        CoroutineScope(IO).launch {
            var entry = entryDatabaseDAO.getEntry(id)
            liveEntry.postValue(entry)
        }
        return liveEntry
    } //gets entry using mutablelivedata to wait for database object

    fun getLength(): Int{
        var len = 0
        CoroutineScope(IO).launch {
            len = entryDatabaseDAO.getAllEntries().count()
        }
        return len
    }


}