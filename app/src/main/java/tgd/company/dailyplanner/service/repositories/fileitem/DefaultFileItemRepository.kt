package tgd.company.dailyplanner.service.repositories.fileitem

import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.data.fileitem.FileItem
import tgd.company.dailyplanner.service.room.customevent.CustomEventDao
import tgd.company.dailyplanner.service.room.fileitem.FileItemDao
import javax.inject.Inject

class DefaultFileItemRepository @Inject constructor(
        private val fileItemDao: FileItemDao
): IFileItemRepository {

    override suspend fun saveDataInRoom(
            fileItem: FileItem
    ) = fileItemDao.insertFileItem(fileItem)

    override suspend fun deleteDataInRoom(
            fileItem: FileItem
    ) = fileItemDao.deleteFileItem(fileItem)

    override suspend fun clear(
            customEventId: Int
    ) = fileItemDao.clear(customEventId)

    override fun observeFileItems(
            customEventId: Int
    ) = fileItemDao.observeFileItems(customEventId)
}