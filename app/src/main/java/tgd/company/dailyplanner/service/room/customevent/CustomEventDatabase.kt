package tgd.company.dailyplanner.service.room.customevent

import androidx.room.Database
import androidx.room.RoomDatabase
import tgd.company.dailyplanner.data.customevent.CustomEvent

@Database(
        entities = [CustomEvent::class],
        version = 1,
    exportSchema = false
)
abstract class CustomEventDatabase: RoomDatabase() {
    abstract fun customEventDao(): CustomEventDao
}