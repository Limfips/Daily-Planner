package tgd.company.dailyplanner.data.customevent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_events_table")
data class CustomEvent(
        val userUid: String,
        val date_start: Long = 0,
        val date_finish: Long = 0,
        var name: String = "",
        var description: String = "",
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null
)
