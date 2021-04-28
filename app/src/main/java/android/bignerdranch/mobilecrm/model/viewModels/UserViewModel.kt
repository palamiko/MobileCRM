package android.bignerdranch.mobilecrm.model.viewModels

import android.bignerdranch.mobilecrm.model.otherModel.TokenFirebase
import android.bignerdranch.mobilecrm.model.otherModel.User
import android.bignerdranch.mobilecrm.network.NetworkModule
import android.bignerdranch.mobilecrm.utilits.helpers.USERS
import android.widget.EditText
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserViewModel: ViewModel() {

    var mAuth: FirebaseAuth

    init {
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        this.mAuth = mAuth
    }

    fun logOut() {
        mAuth.signOut()
    }

    suspend fun logInWithToken(token: TokenFirebase): AuthResult?
            = withContext(Dispatchers.IO) {
        try {
            return@withContext mAuth.signInWithCustomToken(token.value).await()

        } catch (e: Exception) {
            return@withContext null
        }
    }

    suspend fun getUserDataReference(authResult: AuthResult?): DatabaseReference?  = withContext(Dispatchers.IO) {
        if (authResult != null) {
            val user = mAuth.currentUser?.uid
            val database = Firebase.database
            return@withContext database.getReference("$USERS/$user")
        } else return@withContext null
    }

    suspend fun getDataFromFirebase(reference: DatabaseReference?): String = withContext(Dispatchers.IO) {
        var data = ""
        if (reference != null) {data = Json.encodeToString(reference.get().await().getValue(User::class.java))}
        return@withContext data
    }

    @ExperimentalSerializationApi
    suspend fun getAuthorization(login: EditText?, password: EditText?): TokenFirebase =
        withContext(Dispatchers.IO) {
            val networkApiService = NetworkModule().networkApiService
            return@withContext networkApiService.getAuthorization(
                login?.text.toString(),
                password?.text.toString()
            )
        }

}




