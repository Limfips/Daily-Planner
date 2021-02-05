package tgd.company.dailyplanner.service.firebase.user

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import tgd.company.dailyplanner.other.Resource
import javax.inject.Inject

class UserAuthentication @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun signUpUser(email:String, password:String): Resource<AuthResult> {
        return try{
            val data = auth.createUserWithEmailAndPassword(email,password)
                .await()
            Resource.success(data)
        }catch (e : Exception){
            Resource.error(e.message ?: "Error", null)
        }
    }

    suspend fun signInUser(email:String, password:String): Resource<AuthResult> {
        return try {
            val data = auth.signInWithEmailAndPassword(email,password)
                .await()
            Resource.success(data)
        } catch (e: Exception) {
            Resource.error(e.message ?: "Error", null)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun delete() {
        auth.currentUser!!.delete()
    }
}