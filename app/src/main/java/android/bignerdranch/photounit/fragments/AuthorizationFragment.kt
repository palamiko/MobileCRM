package android.bignerdranch.photounit.fragments

import android.annotation.SuppressLint
import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.fragments.TaskFragment.Companion.FIRST
import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.KEY_USER_DATA
import android.bignerdranch.photounit.utilits.SHARED_PREF_NAME
import android.bignerdranch.photounit.utilits.USERS
import android.bignerdranch.photounit.viewModels.UserViewModel
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_authorization.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AuthorizationFragment : BaseFragment(R.layout.fragment_authorization), DataBaseCommunication {

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var savedStateHandle: SavedStateHandle

    private val dataUser = MutableLiveData<String>()  // Информация из Firebase

    // SharedPreference
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editorSharedPref: SharedPreferences.Editor


    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        editorSharedPref = sharedPref.edit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (userViewModel.mAuthLiveData.value != null) userViewModel.logOut()
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(LOGIN_SUCCESSFUL, false)
        savedStateHandle.set(FIRST, false)
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun init() {
        sign_in.setOnClickListener {
            load_input.isVisible = true
            userViewModel.login(getText(te_user_login), getText(te_password))  // Отправляем POST в корутине.
        }

        userViewModel.ldToken.observe(this, {
            authWithCustomToken(it)  // Если изменилась лайвдата то пришел токен и авторизуемся
        })

        dataUser.observe(this, {
            if (it != null) { // Если пришли данные пользователя из FB то переходим к TaskFragment
                writeUserDataInSharedPref(it)
                savedStateHandle.set(LOGIN_SUCCESSFUL, true)
                savedStateHandle.set(FIRST, true)
                clearData()
                findNavController().popBackStack()
            }
        })
    }

    private fun clearData() {
        dataUser.value = null
        userViewModel.clearToken()
        load_input.isInvisible = true
    }

    // Функция аутентифицирует в FireBase используя токен
    private fun authWithCustomToken(token: String) {
        if (token != "") {
            token.let {
                userViewModel.mAuthLiveData.value?.signInWithCustomToken(it)
                    ?.addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Log.d(ContentValues.TAG, "signInWithCustomToken:success")
                            val user = userViewModel.mAuthLiveData.value?.currentUser
                            val database = Firebase.database
                            val userRef = database.getReference("$USERS/${userViewModel.mAuthLiveData.value?.currentUser?.uid}") // Ссылка на данные пользователя
                            if (user != null) {
                                Log.w("MyLOG", user.uid)
                                CoroutineScope(Dispatchers.IO).launch {
                                    getUserDataFromFirebase(userRef, dataUser)  // Делаем запрос в бд в корутине
                                }
                            }
                        } else {
                            load_input.isInvisible = true
                            Log.w(ContentValues.TAG, "signInWithCustomToken:failure", task.exception)
                            Toast.makeText(requireContext(), "Ошибка авторизации", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } else {
            load_input.isInvisible = true
            Toast.makeText(requireContext(), "Ошибка авторизации. Token не получен.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeUserDataInSharedPref(userData: String) {
        editorSharedPref.putString(KEY_USER_DATA, userData)
        editorSharedPref.apply()
    }
    companion object {
        const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
    }
}


