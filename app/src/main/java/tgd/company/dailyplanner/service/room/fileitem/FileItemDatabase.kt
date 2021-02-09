package tgd.company.dailyplanner.service.room.fileitem

import androidx.room.Database
import androidx.room.RoomDatabase
import tgd.company.dailyplanner.data.fileitem.FileItem
import tgd.company.dailyplanner.data.user.User
import tgd.company.dailyplanner.service.room.user.UserDao

@Database(
        entities = [FileItem::class],
        version = 1,
        exportSchema = false
)
abstract class FileItemDatabase: RoomDatabase() {
    abstract fun fileItemDao(): FileItemDao
}