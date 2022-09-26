package com.cmpt362.zachary_fong_myruns

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "entry_table")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,                      //Primary Key
    @ColumnInfo(name = "inputType_column")
    var inputType: String = "",                     // Manual, GPS or automatic
    @ColumnInfo(name = "activityType_column")
    var activityType: String = "",                  // Running, cycling etc.
    @ColumnInfo(name = "dateTime_column")
    var dateTime: String = "",                 // When does this entry happen
    @ColumnInfo(name = "duration_column")
    var duration: Int = 0,                      // Exercise duration in seconds
    @ColumnInfo(name = "distance_column")
    var distance: Float = 0F,                    // Distance traveled. Either in meters or feet.
    @ColumnInfo(name = "units_column")
    var units: Int = 0,
    @ColumnInfo(name = "avgPace_column")
    var avgPace: Float = 0F,                     // Average pace
    @ColumnInfo(name = "avgSpeed_column")
    var avgSpeed: Float = 0F,                    // Average speed
    @ColumnInfo(name = "calorie_column")
    var calorie: Float = 0F,                     // Calories burnt
    @ColumnInfo(name = "climb_column")
    var climb: Float = 0F,                       // Climb. Either in meters or feet.
    @ColumnInfo(name = "heartRate_column")
    var heartRate: Int = 0,                     // Heart rate
    @ColumnInfo(name = "comment_column")
    var comment: String = "",                    // Comments
    @ColumnInfo(name = "latLng_column")
    var latLng: String = ""
)
