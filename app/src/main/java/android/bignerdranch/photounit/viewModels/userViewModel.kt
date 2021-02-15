package android.bignerdranch.photounit.viewModels

import android.bignerdranch.photounit.model.TokenFirebase
import android.bignerdranch.photounit.network.NetworkModule
import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.USERS
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

class UserViewModel: ViewModel(), DataBaseCommunication {

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

    suspend fun getUserDataInFirebase(authResult: AuthResult?): DatabaseReference?  = withContext(Dispatchers.IO) {
        if (authResult != null) {
            val user = mAuth.currentUser?.uid
            val database = Firebase.database
            return@withContext database.getReference("$USERS/$user")
        } else return@withContext null

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




