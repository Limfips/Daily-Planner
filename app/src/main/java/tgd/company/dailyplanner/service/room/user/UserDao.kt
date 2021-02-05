package tgd.company.dailyplanner.service.room.user

import androidx.lifecycle.LiveData
import androidx.room.*
import tgd.company.dailyplanner.data.user.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users_table where uid = :userUid")
    fun observeUser(userUid: String): LiveData<User>
}