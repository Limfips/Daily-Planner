package tgd.company.dailyplanner.service.repositories.fileitem

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tgd.company.dailyplanner.data.fileitem.FileItem
import tgd.company.dailyplanner.other.Resource
import tgd.company.dailyplanner.service.firebase.fileitem.FileItemFirestore
import tgd.company.dailyplanner.service.firebase.fileitem.FileItemStorage
import tgd.company.dailyplanner.service.room.fileitem.FileItemDao
import javax.inject.Inject

class DefaultFileItemRepository @Inject constructor(
        private val dao: FileItemDao,
        private val storage: FileItemStorage,
        private val firestore: FileItemFirestore
): IFileItemRepository {

    override suspend fun saveDataInRoom(
            fileItem: FileItem
    ) = dao.insertFileItem(fileItem)

    override suspend fun deleteDataInRoom(
            fileItem: FileItem
    ) = dao.deleteFileItem(fileItem)

    override suspend fun clear(
            customEventId: Int
    ) = dao.clear(customEventId)

    override fun observeFileItems(
            customEventId: Int
    ) = dao.observeFileItems(customEventId)

    override fun uploadOnServer(fileItem: FileItem){
        CoroutineScope(Dispatchers.IO).launch {
            val serverUrl = storage.upload(fileItem)
            fileItem.serverUrl = serverUrl
            dao.insertFileItem(fileItem)
        }
    }

    override fun uploadOnServer(fileItems: List<FileItem>){
        CoroutineScope(Dispatchers.IO).launch {
            fileItems.forEach {
                val serverUrl = storage.upload(it)
                it.serverUrl = serverUrl
                dao.insertFileItem(it)
            }
        }
    }

    override suspend fun saveDataInServer(
            userUid: String,
            fileItems: List<FileItem>
    ) = firestore.saveFileItems(userUid, fileItems)

    override suspend fun getDataOnServer(
            userUid: String, customEventId: Int
    ) = firestore.getFileItems(userUid, customEventId)

}