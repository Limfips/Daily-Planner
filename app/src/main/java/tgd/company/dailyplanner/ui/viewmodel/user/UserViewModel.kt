package tgd.company.dailyplanner.ui.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tgd.company.dailyplanner.data.user.User
import tgd.company.dailyplanner.service.repositories.user.IUserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: IUserRepository
): ViewModel() {

    fun getCurrentUser(): User? = repository.getCurrentUser()

    suspend fun signIn(login: String, password: String) = repository.signInUser(login, password)

    suspend fun signUp(login: String, password: String, name: String) = repository.signUpUser(login, password, name)

    fun signOut() = repository.signOut()

    suspend fun deleteUser() = viewModelScope.launch {
        repository.deleteUser()
    }
}