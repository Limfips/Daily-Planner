package tgd.company.dailyplanner.service.repositories.fileitem

import androidx.lifecycle.LiveData
import tgd.company.dailyplanner.data.fileitem.FileItem
import tgd.company.dailyplanner.other.Resource

interface IFileItemRepository {
    fun observeFileItems(customEventId: Int): LiveData<List<FileItem>>
    suspend fun saveDataInRoom(fileItem: FileItem)
    suspend fun deleteDataInRoom(fileItem: FileItem)
    suspend fun clear(customEventId: Int)
    fun uploadOnServer(fileItem: FileItem)
    suspend fun saveDataInServer(userUid: String, fileItems: List<FileItem>): Boolean
    suspend fun getDataOnServer(userUid: String, customEventId: Int): Resource<List<FileItem>>
    fun uploadOnServer(fileItems: List<FileItem>)
}