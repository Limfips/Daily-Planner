package tgd.company.dailyplanner.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.data.user.User
import tgd.company.dailyplanner.other.Resource
import tgd.company.dailyplanner.other.Status
import tgd.company.dailyplanner.service.repositories.customevent.ICustomEventRepository
import tgd.company.dailyplanner.service.repositories.user.IUserRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val customEventRepository: ICustomEventRepository,
    private val userRepository: IUserRepository
): ViewModel() {


    private val _loginStatus = MutableLiveData<Resource<User>>()
    val loginStatus: LiveData<Resource<User>> = _loginStatus

    fun init(function: () -> Unit) = viewModelScope.launch {
        userRepository.init()
        function()
    }

    fun getCurrentUser(): User? = userRepository.getCurrentUser()

    fun signOut() = userRepository.signOut()

    fun signIn(login: String, password: String) {
        viewModelScope.launch {
            _loginStatus.postValue(Resource.loading(null))
            val result = userRepository.signInUser(login, password)

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
            val result = userRepository.signUpUser(login, password, name)

            if (result.status == Status.SUCCESS) {
                _loginStatus.postValue(Resource.success(result.data))
            } else {
                _loginStatus.postValue(Resource.error(result.message ?: "signUp error", null))
            }
        }
    }

    suspend fun deleteUser() = viewModelScope.launch {
        userRepository.deleteUser()
    }

    suspend fun saveDataOnServer(
        userUid: String,
        events: List<CustomEvent>
    )= viewModelScope.launch {
        customEventRepository.saveDataOnServer(userUid, events)
    }

    fun saveEvent(
        customEvent: CustomEvent
    ) = viewModelScope.launch {
        customEventRepository.saveDataInRoom(customEvent)
    }

    suspend fun deleteDataOnServer(
        userUID: String
    ) = viewModelScope.launch {
        customEventRepository.deleteDataInServer(userUID)
    }

    suspend fun deleteEvent(
        customEvent: CustomEvent
    ) = viewModelScope.launch {
        customEventRepository.deleteDataInRoom(customEvent)
    }

    suspend fun loadDataInServer(
        userUID: String
    ) = viewModelScope.launch {
        customEventRepository.getDataOnServer(userUID).data?.forEach {
            customEventRepository.saveDataInRoom(it)
        }
    }

    fun observeCustomEvents(
        userUid: String
    ) = customEventRepository.observeCustomEvents(userUid)


    // CreateFragment-------------------------------------------------------------------------------
    private var _newCustomEventCalendar = MutableLiveData(Calendar.getInstance())
    val newCustomEventCalendar: LiveData<Calendar> = _newCustomEventCalendar

    var isStart = true
    private var _selectedDay = MutableLiveData(Calendar.getInstance())
    val selectedDay: LiveData<Calendar> = _selectedDay

    fun setNewCustomEventCalendar(calendar: Calendar) {
        _newCustomEventCalendar.value = calendar
    }
    fun setSelectedDay(calendar: Calendar) {
        _selectedDay.value = calendar
    }

    fun insertCustomEvent(
        timeIndex: Int,
        name: String,
        description: String,
        function: () -> Unit
    ) {
        val start: Calendar = _newCustomEventCalendar.value!!.clone() as Calendar
        val finish: Calendar = _newCustomEventCalendar.value!!.clone() as Calendar
        start.set(Calendar.HOUR_OF_DAY, timeIndex)
        start.set(Calendar.MINUTE, 0)

        if (timeIndex == 23) {
            finish.set(Calendar.HOUR_OF_DAY, 0)
            finish.set(Calendar.MINUTE, 0)
        } else {
            finish.set(Calendar.HOUR_OF_DAY, timeIndex + 1)
            finish.set(Calendar.MINUTE, 0)
        }

        val customEvent = CustomEvent(
            getCurrentUser()!!.uid,
            start.timeInMillis,
            finish.timeInMillis,
            name,
            description
        )
        saveEvent(customEvent)
        function()
    }
    //----------------------------------------------------------------------------------------------
}