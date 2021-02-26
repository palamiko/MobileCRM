package android.bignerdranch.photounit.fragments

import android.annotation.SuppressLint
import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.databinding.FragmentAuthorizationBinding
import android.bignerdranch.photounit.fragments.task.TaskFragment.Companion.FIRST
import android.bignerdranch.photounit.model.otherModel.TokenFirebase
import android.bignerdranch.photounit.utilits.KEY_USER_DATA
import android.bignerdranch.photounit.utilits.SHARED_PREF_NAME
import android.bignerdranch.photounit.utilits.hideKeyboard
import android.bignerdranch.photounit.viewModels.UserViewModel
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi


class AuthorizationFragment : BaseFragment(R.layout.fragment_authorization) {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var savedStateHandle: SavedStateHandle
    private var binding: FragmentAuthorizationBinding? = null

    // SharedPreference
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editorSharedPref: SharedPreferences.Editor

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        editorSharedPref = sharedPref.edit()
    }

    private fun writeUserDataInSharedPref(userData: String) {
        editorSharedPref.putString(KEY_USER_DATA, userData)
        editorSharedPref.apply()
    }

    @ExperimentalSerializationApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fragmentBinding = FragmentAuthorizationBinding.bind(view)
        binding = fragmentBinding
        binding?.signIn?.setOnClickListener { signIn(it) }

        if (userViewModel.mAuth.currentUser != null) userViewModel.logOut()
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(FIRST, false)
    }

    override fun onPause() {
        view?.hideKeyboard()
        super.onPause()
    }



    @ExperimentalSerializationApi
    private fun signIn(view: View) {

        coroutineScope.launch(exceptionHandler) {
            visibleProgressBar()
            if (checkTextInput(view, binding?.teUserLogin, binding?.tePassword)) {
                //  Авторизуемся в срм и получеам токен
                val token: TokenFirebase =
                    userViewModel.getAuthorization(binding?.teUserLogin, binding?.tePassword)
                //  Авторизуемся в Firebase при помощи токена
                if (token.value != "0") {
                    val authResult = userViewModel.logInWithToken(token)!!
                    //  Если авторизация успешна получаем ссылку на базу данных с данными пользователя
                    val reference: DatabaseReference? = userViewModel.getUserDataReference(authResult)
                    if (reference != null) {
                        //  Загружам данные пользователя из FireBase
                        val data = userViewModel.getDataFromFirebase(reference)
                        if (data != "") {
                            saveData(data)  //  Сохраняем данные в SharedPreference
                            invisibleProgressBar()  //  Переходим на фрагмент заявок
                            getNextFragment()
                        } else showSnackBar(view, "Пустые данные из Firebase"); invisibleProgressBar()
                    } else showSnackBar(view, "Ошибка ссылки на Firebase"); invisibleProgressBar()
                } else showSnackBar(view, "Не верные учетные данные!"); invisibleProgressBar()
            } else invisibleProgressBar()
        }
    }

    private suspend fun getNextFragment() = withContext(Dispatchers.Main) {
        findNavController().popBackStack()
    }

    private suspend fun saveData(data: String) = withContext(Dispatchers.IO) {
        writeUserDataInSharedPref(data)
        savedStateHandle.set(FIRST, true)
    }

    private suspend fun showSnackBar(view: View, message: String) = withContext(Dispatchers.Main) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackBar.show()
    }

    private suspend fun visibleProgressBar() = withContext(Dispatchers.Main){
        binding?.loadInput?.isVisible = true
    }
    private suspend fun invisibleProgressBar() = withContext(Dispatchers.Main){
        binding?.loadInput?.isInvisible = true
    }

    private suspend fun checkTextInput(
        view: View,
        textEditLogin: EditText?,
        textEditPassword: EditText?
    ):Boolean {
        val login = textEditLogin?.text.toString()
        val pass = textEditPassword?.text.toString()
        val regex:Regex = "^[a-zA-Z0-9]+\$".toRegex()
        var result = false
        if ( login != "" &&  pass != "") {
            if (login.matches(regex) && pass.matches(regex)) {
                result = true
            } else showSnackBar(view, getString(R.string.allowed_latin))
        } else showSnackBar(view, getString(R.string.input_login_password))
        return result
    }
}

