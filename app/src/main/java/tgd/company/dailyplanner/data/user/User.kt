package tgd.company.dailyplanner.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class User(
    @PrimaryKey(autoGenerate = false)
    val uid: String,
    var name: String
)