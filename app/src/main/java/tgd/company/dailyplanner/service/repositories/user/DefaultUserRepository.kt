package tgd.company.dailyplanner.service.repositories.user

import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tgd.company.dailyplanner.data.user.User
import tgd.company.dailyplanner.other.Resource
import tgd.company.dailyplanner.other.Status
import tgd.company.dailyplanner.service.firebase.user.UserAuthentication
import tgd.company.dailyplanner.service.firebase.user.UserFirestore
import tgd.company.dailyplanner.service.room.user.UserDao
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DefaultUserRepository
@Inject constructor(
    private val userAuthentication: UserAuthentication,
    private val userFirestore: UserFirestore,
    private val userDao: UserDao
): IUserRepository {

    private var currentUser: User? = null

    override suspend fun init(function: () -> Unit) {
        val result = userAuthentication.getCurrentUser()
        if (result != null) {
            val user = userFirestore.getUserData(result.uid)
            currentUser = user.data
            if (currentUser != null) {
                userDao.insertUser(currentUser!!)
                function()
            }
        }
    }


    override fun getCurrentUser(): User? {
        return currentUser
    }

    override suspend fun signUpUser(email:String, password:String, name: String): Resource<User> {
        val result = userAuthentication.signUpUser(email, password)
        return if (result.status == Status.SUCCESS) {
            val user = User(result.data!!.user!!.uid, name)
            currentUser = user
            userFirestore.saveUserData(currentUser!!)
            userDao.insertUser(currentUser!!)
            Resource.success(user)
        } else {
            Resource.error(result.message ?: "Error: status error", null)
        }
    }

    override suspend fun signInUser(email:String, password:String): Resource<User> {
        val result = userAuthentication.signInUser(email, password)
        return if (result.status == Status.SUCCESS) {
            val user = userFirestore.getUserData(result.data!!.user!!.uid).data
            currentUser = user
            userDao.insertUser(currentUser!!)
            Resource.success(user)
        } else {
            Resource.error("Error", null)
        }
    }

    override suspend fun deleteUser() {
        if (currentUser != null) {
            userDao.deleteUser(currentUser!!)
            userFirestore.deleteData(currentUser!!.uid)
            userAuthentication.delete()
            currentUser = null
        }
    }

    override fun signOut() {
        userAuthentication.signOut()
        currentUser = null
    }
}