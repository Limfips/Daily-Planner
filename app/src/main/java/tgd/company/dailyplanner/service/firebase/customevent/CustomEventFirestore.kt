package tgd.company.dailyplanner.service.firebase.customevent

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
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
                .set(events)
                .await()
            true
        } catch (e: Exception) {
            false
        }
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
            Resource.success(data.toObject<List<CustomEvent>>())
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