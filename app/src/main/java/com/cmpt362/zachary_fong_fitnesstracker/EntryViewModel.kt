package com.cmpt362.zachary_fong_fitnesstracker

import androidx.lifecycle.*

class EntryViewModel(private val repository: EntryRepository): ViewModel() {
    val allEntriesLiveData: LiveData<List<Entry>> = repository.allEntries.asLiveData()

    fun insert(entry: Entry){
        repository.insert(entry)
    }

    fun delete(id: Long){
        repository.delete(id)
    }

    fun getEntry(id: Long): MutableLiveData<Entry> {
        return repository.getEntry(id)
    }

    fun getLength(): Int{
        return repository.getLength()
    }

}

class EntryViewModelFactory(private val repository: EntryRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EntryViewModel::class.java))
            return EntryViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}