package tgd.company.dailyplanner.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.data.fileitem.FileItem
import tgd.company.dailyplanner.data.user.User
import tgd.company.dailyplanner.other.Resource
import tgd.company.dailyplanner.other.Status
import tgd.company.dailyplanner.other.isNetworkAvailable
import tgd.company.dailyplanner.service.repositories.customevent.ICustomEventRepository
import tgd.company.dailyplanner.service.repositories.fileitem.IFileItemRepository
import tgd.company.dailyplanner.service.repositories.user.IUserRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val customEventRepository: ICustomEventRepository,
    private val userRepository: IUserRepository,
    private val fileItemRepository: IFileItemRepository
): ViewModel() {


    private val _loginStatus = MutableLiveData<Resource<User>>()
    val loginStatus: LiveData<Resource<User>> = _loginStatus

    fun init(function: () -> Unit) = viewModelScope.launch {
        userRepository.init {
            function()
        }
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

    // используется в CreateFragment
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

    fun deleteEvent(
        customEvent: CustomEvent? = null
    ) {
        viewModelScope.launch {
            if (customEvent == null) {
                customEventRepository.deleteDataInRoom(_selectedCustomEvent.value!!)
                setSelectedCustomEvent(null)
                _createdItemState.postValue(true)
            } else {
                customEventRepository.deleteDataInRoom(customEvent)
            }
        }
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

    var isStart = true
    private var _selectedDay = MutableLiveData(Calendar.getInstance())
    val selectedDay: LiveData<Calendar> = _selectedDay

    fun setSelectedDay(calendar: Calendar) {
        _selectedDay.value = calendar
    }

    // CreateFragment-------------------------------------------------------------------------------
    // активно используется только в create fragment
    // при создании CreateFragment получает значение текущего выбранного
    // дня и задаёт его как значение по умолчанию для нового создаваемого события
    private var _newCustomEventCalendar = MutableLiveData(Calendar.getInstance())
    val newCustomEventCalendar: LiveData<Calendar> = _newCustomEventCalendar
    fun setNewCustomEventCalendar(calendar: Calendar) {
        _newCustomEventCalendar.value = calendar
    }

    fun updateCustomEvent(
        timeIndex: Int,
        name: String,
        description: String,
        files: List<FileItem>,
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
            description,
            selectedCustomEvent.value!!.id
        )
        saveEvent(customEvent)
        saveFileItems(selectedCustomEvent.value!!.id!!, files)
        setSelectedCustomEvent(null)
        _editedItemState.postValue(true)
        function()
    }

    // получает на входе индекс выбранного промежутка времени из списка предложенных;
    // получает название события, описание и функцию выполнения после обработки
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
        _createdItemState.postValue(true)
        function()
    }
    //----------------------------------------------------------------------------------------------

    // MainFragment --------------------------------------------------------------------------------
    private val _deletedItemState = MutableLiveData(false)
    fun setDeletedItemState(state: Boolean) {
        _deletedItemState.value = state
    }
    val deletedItemState: LiveData<Boolean> = _deletedItemState

    private val _createdItemState = MutableLiveData(false)
    fun setCreatedItemState(state: Boolean) {
        _createdItemState.value = state
    }
    val createdItemState: LiveData<Boolean> = _createdItemState

    private val _editedItemState = MutableLiveData(false)
    fun setEditedItemState(state: Boolean) {
        _editedItemState.value = state
    }
    val editedItemState: LiveData<Boolean> = _editedItemState


    private val _events = MutableLiveData<List<CustomEvent>>()
    fun updateEvents(events: List<CustomEvent>) {
        _events.postValue(events)
    }
    fun saveDataOnServer(context: Context, function: (Boolean) -> Unit) {
        if (isNetworkAvailable(context)) {
            viewModelScope.launch {
                var result = customEventRepository.saveDataOnServer(getCurrentUser()!!.uid, _events.value!!)
                _events.value!!.forEach { customEvent ->
                    fileItemRepository.observeFileItems(customEvent.id!!).observeForever {
                        fileItemRepository.uploadOnServer(it)
                        viewModelScope.launch {
                            result = result && fileItemRepository.saveDataInServer(getCurrentUser()!!.uid, it)

                            Log.w("APPVM_TAG", customEvent.toString())
                        }
                    }
                }
                Log.w("APPVM_TAG", result.toString())
                function(result)
            }
        } else {
            function(false)
        }
    }

    fun loadDataInServer(context: Context, function: (Boolean) -> Unit) {
        if (isNetworkAvailable(context)) {
            viewModelScope.launch {
                val result = customEventRepository.getDataOnServer(getCurrentUser()!!.uid)
                if (result.status == Status.SUCCESS) {
                    customEventRepository.clear(getCurrentUser()!!.uid)
                    result.data!!.forEach { customEvent ->
                        customEventRepository.saveDataInRoom(customEvent)
                        val fileItems = fileItemRepository.getDataOnServer(getCurrentUser()!!.uid, customEvent.id!!)
                        if (fileItems.status == Status.SUCCESS) {
                            fileItemRepository.clear(customEvent.id)
                            fileItems.data!!.forEach { fileItem ->
                                fileItemRepository.saveDataInRoom(fileItem)
                            }
                        }
                    }.let {
                        function(true)
                    }
                } else {
                    function(false)
                }
            }
        } else {
            function(false)
        }

    }
    //----------------------------------------------------------------------------------------------

    // MainFragment --------------------------------------------------------------------------------
    private val _selectedCustomEvent = MutableLiveData<CustomEvent?>()
    val selectedCustomEvent: LiveData<CustomEvent?> = _selectedCustomEvent

    fun setSelectedCustomEvent(customEvent: CustomEvent?) {
        _selectedCustomEvent.postValue(customEvent)
    }

    fun observeFileItems(
            customEventId: Int
    ) = fileItemRepository.observeFileItems(customEventId)

    fun saveFileItems(
            customEventId: Int,
            fileItems: List<FileItem>
    ) {
        viewModelScope.launch {
            fileItemRepository.clear(customEventId)
            fileItems.forEach {
                fileItemRepository.saveDataInRoom(it)
            }
        }
    }
    //----------------------------------------------------------------------------------------------
}