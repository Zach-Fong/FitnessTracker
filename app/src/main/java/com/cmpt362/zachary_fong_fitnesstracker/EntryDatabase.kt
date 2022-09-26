package com.cmpt362.zachary_fong_fitnesstracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Entry::class], version = 1)
abstract class EntryDatabase: RoomDatabase() {
    abstract val entryDatabaseDao: EntryDatabaseDAO

    companion object{
        @Volatile
        private var INSTANCE: EntryDatabase? = null

        fun getInstance(context: Context): EntryDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext, EntryDatabase::class.java, "entry_db").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}
