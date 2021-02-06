package tgd.company.dailyplanner.service.repositories.customevent

import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.service.firebase.customevent.CustomEventFirestore
import tgd.company.dailyplanner.service.room.customevent.CustomEventDao
import javax.inject.Inject

class DefaultCustomEventRepository @Inject constructor(
    private val customEventFirestore: CustomEventFirestore,
    private val customEventDao: CustomEventDao
): ICustomEventRepository {

    override suspend fun saveDataOnServer(
        userUid: String,
        events: List<CustomEvent>
    ) = customEventFirestore.saveEvents(userUid, events)

    override suspend fun saveDataInRoom(
        customEvent: CustomEvent
    ) = customEventDao.insertCustomEvent(customEvent)

    override suspend fun deleteDataInServer(
        userUID: String
    ) = customEventFirestore.deleteData(userUID)

    override suspend fun deleteDataInRoom(
        customEvent: CustomEvent
    ) = customEventDao.deleteCustomEvent(customEvent)

    override suspend fun clear() = customEventDao.clear()

    override suspend fun getDataOnServer(
        userUID: String
    ) = customEventFirestore.getEvents(userUID)

    override fun observeCustomEvents(
        userUid: String
    ) = customEventDao.observeCustomEvents(userUid)
}