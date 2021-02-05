package tgd.company.dailyplanner.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.other.Resource
import tgd.company.dailyplanner.service.repositories.customevent.ICustomEventRepository

class FakeCustomEventRepository: ICustomEventRepository {

    private val serverData = HashMap<String, MutableList<CustomEvent>?>()
    private val roomData = HashMap<String, MutableList<CustomEvent>?>()

    private var checkUID = ""
    private val _events = MutableLiveData<List<CustomEvent>>()

    private fun refreshLiveData(events: List<CustomEvent>?) {
        _events.postValue(events)
    }

    override suspend fun saveDataOnServer(
        userUid: String,
        events: List<CustomEvent>
    ): Boolean {
        serverData[userUid] = events.toMutableList()
        return true
    }

    override suspend fun getDataOnServer(
        userUID: String
    ): Resource<List<CustomEvent>> {
        val result = serverData[userUID]
        return if (result == null){
            Resource.error("null data", null)
        } else {
            Resource.success(result)
        }
    }

    override suspend fun deleteDataInServer(
        userUID: String
    ): Boolean {
        serverData[userUID] = null
        return true
    }

    override suspend fun saveDataInRoom(
        customEvent: CustomEvent
    ) {
        if (roomData[customEvent.userUid] == null) roomData[customEvent.userUid] = mutableListOf()
        roomData[customEvent.userUid]!!.add(customEvent)
        if (checkUID == customEvent.userUid) {
            refreshLiveData(roomData[checkUID])
        }
    }

    override suspend fun deleteDataInRoom(
        customEvent: CustomEvent
    ) {
        roomData[customEvent.userUid] = null
        if (checkUID == customEvent.userUid) {
            refreshLiveData(roomData[checkUID])
        }
    }

    override fun observeCustomEvents(
        userUid: String
    ): LiveData<List<CustomEvent>>  {
        checkUID = userUid
        refreshLiveData(roomData[checkUID])
        return _events
    }
}