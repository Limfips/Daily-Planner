package tgd.company.dailyplanner.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class User(
    @PrimaryKey(autoGenerate = false)
    var uid: String = "",
    var name: String = ""
)