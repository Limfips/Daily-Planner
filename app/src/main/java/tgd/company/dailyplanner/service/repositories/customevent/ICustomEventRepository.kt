package tgd.company.dailyplanner.service.repositories.customevent

import androidx.lifecycle.LiveData
import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.other.Resource


interface ICustomEventRepository {
    suspend fun saveDataOnServer(userUid: String, events: List<CustomEvent>): Boolean
    suspend fun getDataOnServer(userUID: String): Resource<List<CustomEvent>>
    suspend fun deleteDataInServer(userUID: String): Boolean
    suspend fun saveDataInRoom(customEvent: CustomEvent)
    suspend fun deleteDataInRoom(customEvent: CustomEvent)
    fun observeCustomEvents(userUid: String): LiveData<List<CustomEvent>>
    suspend fun clear(userUid: String)
}