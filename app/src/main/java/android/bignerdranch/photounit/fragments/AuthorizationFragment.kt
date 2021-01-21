package android.bignerdranch.photounit.fragments

import android.annotation.SuppressLint
import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.KEY_USER_DATA
import android.bignerdranch.photounit.utilits.SHARED_PREF_NAME
import android.bignerdranch.photounit.utilits.USERS
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_authorization.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AuthorizationFragment : BaseFragment(R.layout.fragment_authorization), DataBaseCommunication {

    private val dataUser = MutableLiveData<String>()  // Информация из Firebase
    private val ldToken = MutableLiveData<String>()  // Токен авторизации приходящий с срм
    private lateinit var mAuth: FirebaseAuth
    // SharedPreference
    lateinit var sharedPref: SharedPreferences
    lateinit var editorSharedPref: SharedPreferences.Editor


    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = requireActivity().getSharedPreferences(
            SHARED_PREF_NAME,
            MODE_PRIVATE)
        editorSharedPref = sharedPref.edit()
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    @SuppressLint("RestrictedApi")
    private fun init() {
        mAuth =FirebaseAuth.getInstance()

        sign_in.setOnClickListener {
            sendPostForAuth(te_user_login, te_password, ldToken) // Отправляем POST в корутине.
        }

        // Подключаем слушатель
        ldToken.observe(this, {
            navController.backStack.addAll(mainActivity.myBackStackEntry)  // Добавляем бэестек после удаления назад для нормальной работы navgraph
            authWithCustomToken(it)  // Если изменилась лайвдата то пришел токен и авторизуемся
        })

        dataUser.observe(this, {
            if (it != null) {
                writeUserDataInSharedPref(it)
                navController.navigate(R.id.authorizationFragment_to_navigationTask)
            }
        })
    }

    // Функция аутентифицирует в FireBase используя токен
    private fun authWithCustomToken(token: String) {
        if (token != "") {
            token.let {
                mAuth.signInWithCustomToken(it)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Log.d(ContentValues.TAG, "signInWithCustomToken:success")
                            val user = mAuth.currentUser
                            val database = Firebase.database
                            val userRef = database.getReference("$USERS/${mAuth.currentUser?.uid}") // Ссылка на данные пользователя
                            if (user != null) {
                                Log.w("MyLOG", user.uid)
                                CoroutineScope(Dispatchers.IO).launch {
                                    getUserDataFromFirebase(userRef, dataUser)  // Делаем запрос в бд в корутине
                                }
                            }
                        } else {
                            Log.w(ContentValues.TAG, "signInWithCustomToken:failure", task.exception)
                            Toast.makeText(requireContext(), "Ошибка авторизации", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } else {
            Toast.makeText(requireContext(), "Ошибка авторизации. Token не получен.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeUserDataInSharedPref(userData: String) {
        editorSharedPref.putString(KEY_USER_DATA, userData)
        editorSharedPref.apply()
    }
}


