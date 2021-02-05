package tgd.company.dailyplanner.service.room.user

import androidx.room.Database
import androidx.room.RoomDatabase
import tgd.company.dailyplanner.data.user.User

@Database(
        entities = [User::class],
        version = 1,
    exportSchema = false
)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}