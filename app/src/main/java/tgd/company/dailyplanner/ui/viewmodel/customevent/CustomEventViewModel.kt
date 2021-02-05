package tgd.company.dailyplanner.ui.viewmodel.customevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.service.repositories.customevent.ICustomEventRepository
import javax.inject.Inject

@HiltViewModel
class CustomEventViewModel @Inject constructor(
    private val repository: ICustomEventRepository
): ViewModel() {

    suspend fun saveDataOnServer(
        userUid: String,
        events: List<CustomEvent>
    )= viewModelScope.launch {
        repository.saveDataOnServer(userUid, events)
    }

    suspend fun saveEvent(
        customEvent: CustomEvent
    ) = viewModelScope.launch {
        repository.saveDataInRoom(customEvent)
    }

    suspend fun deleteDataOnServer(
        userUID: String
    ) = viewModelScope.launch {
        repository.deleteDataInServer(userUID)
    }

    suspend fun deleteEvent(
        customEvent: CustomEvent
    ) = viewModelScope.launch {
        repository.deleteDataInRoom(customEvent)
    }

    suspend fun loadDataInServer(
        userUID: String
    ) = viewModelScope.launch {
        repository.getDataOnServer(userUID).data?.forEach {
            repository.saveDataInRoom(it)
        }
    }

    fun observeCustomEvents(
        userUid: String
    ) = repository.observeCustomEvents(userUid)
}