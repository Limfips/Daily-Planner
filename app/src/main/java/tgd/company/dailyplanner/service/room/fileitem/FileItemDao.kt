package tgd.company.dailyplanner.service.room.fileitem

import androidx.lifecycle.LiveData
import androidx.room.*
import tgd.company.dailyplanner.data.fileitem.FileItem
import tgd.company.dailyplanner.data.user.User

@Dao
interface FileItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFileItem(fileItem: FileItem)

    @Query("DELETE FROM file_items_table where customEventId = :customEventId")
    suspend fun clear(customEventId: Int)

    @Delete
    suspend fun deleteFileItem(fileItem: FileItem)

    @Query("SELECT * FROM file_items_table where customEventId = :customEventId")
    fun observeFileItems(customEventId: Int): LiveData<List<FileItem>>
}