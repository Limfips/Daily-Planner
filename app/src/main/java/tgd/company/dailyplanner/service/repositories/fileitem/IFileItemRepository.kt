package tgd.company.dailyplanner.service.repositories.fileitem

import androidx.lifecycle.LiveData
import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.data.fileitem.FileItem
import tgd.company.dailyplanner.other.Resource

interface IFileItemRepository {
    fun observeFileItems(customEventId: Int): LiveData<List<FileItem>>
    suspend fun saveDataInRoom(fileItem: FileItem)
    suspend fun deleteDataInRoom(fileItem: FileItem)
    suspend fun clear(customEventId: Int)
}