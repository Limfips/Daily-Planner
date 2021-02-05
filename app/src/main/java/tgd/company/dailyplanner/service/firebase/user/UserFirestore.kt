package tgd.company.dailyplanner.service.firebase.user

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.data.user.User
import tgd.company.dailyplanner.other.FirestorePath
import tgd.company.dailyplanner.other.Resource
import javax.inject.Inject

class UserFirestore @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun saveUserData(user: User): Boolean {
        return try {
            firestore
                .document(FirestorePath.MAIN.value)
                .collection(user.uid)
                .document(FirestorePath.USER.value)
                .set(user)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserData(uid: String): Resource<User> {
        return try {
            val data = firestore
                .document(FirestorePath.MAIN.value)
                .collection(uid)
                .document(FirestorePath.USER.value)
                .get()
                .await()
            Resource.success(data.toObject<User>())
        } catch (e: Exception) {
            Resource.error(e.message.toString(), null)
        }
    }

    suspend fun deleteData(uid: String): Boolean {
        return try {
            firestore
                .document(FirestorePath.MAIN.value)
                .collection(uid)
                .document(FirestorePath.USER.value)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}