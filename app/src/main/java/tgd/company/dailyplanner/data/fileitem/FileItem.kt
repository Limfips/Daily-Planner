package tgd.company.dailyplanner.data.fileitem

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_items_table")
data class FileItem(
        var name: String = "",
        var roomUrl: String = "",
        var serverUrl: String = "",
        var customEventId: Int? = null,
        var userUid: String? = "",
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null
)
