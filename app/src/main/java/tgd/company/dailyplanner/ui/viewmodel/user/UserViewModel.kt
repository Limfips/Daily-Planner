package tgd.company.dailyplanner.ui.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tgd.company.dailyplanner.data.user.User
import tgd.company.dailyplanner.other.Resource
import tgd.company.dailyplanner.other.Status
import tgd.company.dailyplanner.service.repositories.user.IUserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: IUserRepository
): ViewModel() {

    private val _loginStatus = MutableLiveData<Resource<User>>()
    val loginStatus: LiveData<Resource<User>> = _loginStatus

    fun init(function: () -> Unit) = viewModelScope.launch {
        repository.init()
        function()
    }

    fun getCurrentUser(): User? = repository.getCurrentUser()

    fun signOut() = repository.signOut()

    fun signIn(login: String, password: String) {
        viewModelScope.launch {
            _loginStatus.postValue(Resource.loading(null))
            val result = repository.signInUser(login, password)

            if (result.status == Status.SUCCESS) {
                _loginStatus.postValue(Resource.success(result.data))
            } else {
                _loginStatus.postValue(Resource.error("Error", null))
            }
        }
    }

    fun signUp(login: String, password: String, name: String) {
        viewModelScope.launch {
            _loginStatus.postValue(Resource.loading(null))
            val result = repository.signUpUser(login, password, name)

            if (result.status == Status.SUCCESS) {
                _loginStatus.postValue(Resource.success(result.data))
            } else {
                _loginStatus.postValue(Resource.error(result.message ?: "signUp error", null))
            }
        }
    }

    suspend fun deleteUser() = viewModelScope.launch {
        repository.deleteUser()
    }
}