package tgd.company.dailyplanner.service.firebase.customevent

import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.data.user.User
import tgd.company.dailyplanner.other.FirestorePath
import tgd.company.dailyplanner.other.Resource
import javax.inject.Inject

class CustomEventFirestore @Inject constructor() {

    private val firestore = Firebase.firestore

    suspend fun saveEvents(userUid: String, events: List<CustomEvent>): Boolean {
        return try {
            firestore
                .collection("main")
                .document(FirestorePath.MAIN.value)
                .collection(userUid)
                .document(FirestorePath.EVENTS.value)
                .set(events.toMap())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun List<CustomEvent>.toMap(): Map<String, CustomEvent> {
        val result = HashMap<String, CustomEvent>()
        this.forEach { event ->
            result[event.id.toString()] = event
        }
        return result
    }

    private fun DocumentSnapshot.toCustomEvents(): List<CustomEvent> {
        val result = ArrayList<CustomEvent>()
        data!!.values.forEach { any ->
            val obj = any as HashMap<*, *>
            val customEvent = CustomEvent(
                    obj["userUid"].toString(),
                    obj["date_start"].toString().toLong(),
                    obj["date_finish"].toString().toLong(),
                    obj["name"].toString(),
                    obj["description"].toString(),
                    obj["id"].toString().toInt()
            )
            result.add(customEvent)
        }
        return result
    }

    suspend fun getEvents(userUID: String): Resource<List<CustomEvent>> {
        return try {
            val data = firestore
                .collection("main")
                .document(FirestorePath.MAIN.value)
                .collection(userUID)
                .document(FirestorePath.EVENTS.value)
                .get()
                .await()
            Resource.success(data.toCustomEvents())
        } catch (e: Exception) {
            Resource.error(e.message.toString(), null)
        }
    }

    suspend fun deleteData(uid: String): Boolean {
        return try {
            firestore
                .collection("main")
                .document(FirestorePath.MAIN.value)
                .collection(uid)
                .document(FirestorePath.EVENTS.value)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}