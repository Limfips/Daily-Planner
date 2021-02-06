package tgd.company.dailyplanner.service.repositories.user

import tgd.company.dailyplanner.data.user.User
import tgd.company.dailyplanner.other.Resource

interface IUserRepository {
    suspend fun signUpUser(email: String, password: String, name: String): Resource<User>
    suspend fun signInUser(email: String, password: String): Resource<User>
    fun signOut()
    suspend fun deleteUser()
    fun getCurrentUser(): User?
    suspend fun init(function: () -> Unit)
}