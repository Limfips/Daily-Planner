package tgd.company.dailyplanner.service.firebase.fileitem

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import tgd.company.dailyplanner.data.fileitem.FileItem
import tgd.company.dailyplanner.other.FirestorePath
import tgd.company.dailyplanner.other.Resource
import javax.inject.Inject

class FileItemFirestore @Inject constructor() {

    private val firestore = Firebase.firestore

    suspend fun saveFileItems(userUid: String, fileItems: List<FileItem>): Boolean {
        return try {
            firestore
                    .collection("main")
                    .document(FirestorePath.MAIN.value)
                    .collection(userUid)
                    .document(FirestorePath.FILES.value)
                    .collection(fileItems[0].customEventId.toString())
                    .document(FirestorePath.IMAGES.value)
                    .set(fileItems.toMap())
                    .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getFileItems(userUID: String, customEventId: Int): Resource<List<FileItem>> {
        return try {
            val data = firestore
                    .collection("main")
                    .document(FirestorePath.MAIN.value)
                    .collection(userUID)
                    .document(FirestorePath.FILES.value)
                    .collection(customEventId.toString())
                    .document(FirestorePath.IMAGES.value)
                    .get()
                    .await()
            Resource.success(data.toFileItems(customEventId))
        } catch (e: Exception) {
            Resource.error(e.message.toString(), null)
        }
    }

    suspend fun deleteDataForCustomEvent(userUID: String, customEventId: Int): Boolean {
        return try {
            firestore
                    .collection("main")
                    .document(FirestorePath.MAIN.value)
                    .collection(userUID)
                    .document(FirestorePath.EVENTS.value)
                    .collection(customEventId.toString())
                    .document(FirestorePath.IMAGES.value)
                    .delete()
                    .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun List<FileItem>.toMap(): Map<String, FileItem> {
        val result = HashMap<String, FileItem>()
        this.forEach { event ->
            result[event.id.toString()] = event
        }
        return result
    }

    private fun DocumentSnapshot.toFileItems(customEventId: Int): List<FileItem> {
        val result = ArrayList<FileItem>()
        data!!.values.forEach { any ->
            val obj = any as HashMap<*, *>
            val fileItem = FileItem(
                    obj["name"].toString(),
                    obj["roomUrl"].toString(),
                    obj["serverUrl"].toString(),
                    obj["customEventId"].toString().toInt(),
                    obj["userUid"].toString(),
                    obj["id"].toString().toInt()
            )
            if (fileItem.customEventId == customEventId) result.add(fileItem)
        }
        return result
    }
}