package tgd.company.dailyplanner.service.room.customevent

import androidx.lifecycle.LiveData
import androidx.room.*
import tgd.company.dailyplanner.data.customevent.CustomEvent

@Dao
interface CustomEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomEvent(customEvent: CustomEvent)

    @Delete
    suspend fun deleteCustomEvent(customEvent: CustomEvent)

    @Query("SELECT * FROM custom_events_table where userUid = :userUid")
    fun observeCustomEvents(userUid: String): LiveData<List<CustomEvent>>
}