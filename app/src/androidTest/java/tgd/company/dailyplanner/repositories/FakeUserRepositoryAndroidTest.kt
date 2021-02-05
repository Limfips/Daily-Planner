package tgd.company.dailyplanner.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import tgd.company.dailyplanner.data.user.User
import tgd.company.dailyplanner.other.Resource
import tgd.company.dailyplanner.service.repositories.user.IUserRepository
import java.util.*
import kotlin.collections.HashMap

class FakeUserRepositoryAndroidTest: IUserRepository {

    private var currentUser: User? = null

    override fun getCurrentUser(): User? {
        return currentUser
    }

    private val authServer = mutableListOf<HashMap<Pair<String, String>, User>>()

    private val serverUsers = mutableListOf<User>()

    private val roomUsers = mutableListOf<User>()

    override suspend fun signUpUser(email: String, password: String, name: String): Resource<User> {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            return Resource.error("Error: fields is not empty", null)
        }

        val user = User(UUID.randomUUID().toString(), name)
        authServer.add(hashMapOf(Pair(Pair(email, password), user)))
        currentUser = user
        roomUsers.removeIf { it.uid == currentUser!!.uid }
        roomUsers.add(currentUser!!)
        serverUsers.removeIf { it.uid == currentUser!!.uid }
        serverUsers.add(currentUser!!)
        return Resource.success(user)
    }

    override suspend fun signInUser(email: String, password: String): Resource<User> {
        if (email.isEmpty() || password.isEmpty()) {
            return Resource.error("Error: fields is not empty", null)
        }

        val user = authServer.find { it.containsKey(Pair(email, password)) }?.get(Pair(email, password))
        return if (user == null) {
            Resource.error("Not found user in server", null)
        } else {
            currentUser = user
            roomUsers.removeIf { it.uid == currentUser!!.uid }
            roomUsers.add(currentUser!!)
            Resource.success(user)
        }
    }

    override suspend fun deleteUser() {
        if (currentUser != null) {
            serverUsers.remove(currentUser)
            roomUsers.remove(currentUser)
            authServer.removeIf {
                it.values.removeIf { user ->
                    user.uid == currentUser!!.uid
                }
            }
            currentUser = null
        }
    }

    override fun signOut() {
        currentUser = null
    }
}