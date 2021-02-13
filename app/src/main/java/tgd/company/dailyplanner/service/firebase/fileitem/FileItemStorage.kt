package tgd.company.dailyplanner.service.firebase.fileitem

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import tgd.company.dailyplanner.data.fileitem.FileItem
import javax.inject.Inject


class FileItemStorage @Inject constructor() {

    private val storage = Firebase.storage

    suspend fun upload(fileItem: FileItem): String {
        if (fileItem.serverUrl.isNotEmpty()) return ""
        return try {
            val ref = storage.reference.child("images/${fileItem.id}_${fileItem.customEventId}")
            ref.putFile(Uri.parse(fileItem.roomUrl)).await()
           ref.downloadUrl.toString()
        } catch (e: Exception) {
            Log.w("FS_TAG", e.message + " - " + e.toString())
            ""
        }
    }
}