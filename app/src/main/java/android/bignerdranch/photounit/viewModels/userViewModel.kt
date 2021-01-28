package android.bignerdranch.photounit.viewModels

import android.bignerdranch.photounit.utilits.DataBaseCommunication
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class UserViewModel: ViewModel(), DataBaseCommunication {

    val mAuthLiveData = MutableLiveData<FirebaseAuth>()
    var ldToken = MutableLiveData<String>()

    init {
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        mAuthLiveData.value = mAuth
    }

    fun login(username: String, password: String) {
        /**Функция проходит авторизацию на сервере CRM и получает токен авторизации для FB*/
        sendPostForAuth(username, password, ldToken)
    }

    fun logOut() {
        mAuthLiveData.value?.signOut()
    }

    fun clearToken() {
        ldToken = MutableLiveData<String>()
    }

}