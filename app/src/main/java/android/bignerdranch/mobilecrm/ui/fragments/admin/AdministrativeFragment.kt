package android.bignerdranch.mobilecrm.ui.fragments.admin

import android.bignerdranch.mobilecrm.model.otherModel.User
import android.bignerdranch.mobilecrm.ui.theme.MyTestComposeTheme
import android.bignerdranch.mobilecrm.utilits.helpers.KEY_USER_DATA
import android.bignerdranch.mobilecrm.utilits.helpers.SHARED_PREF_NAME
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class AdministrativeFragment : Fragment() {
    private lateinit var activeUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activeUser = getUserDataFromSharedPref()
    }

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFragment {
                    if (activeUser.status != "adm") NotAdmin() else AdministrativeTools()
                }
            }
        }
    }

    @Composable
    fun ComposeFragment(container: @Composable () -> Unit) {
        MyTestComposeTheme {
            container()
        }
    }

    @Composable
    fun NotAdmin() {
        Row() {
            Spacer(modifier = Modifier.requiredWidth(120.dp))
            Text(text = "Вы не администратор")
        }

    }

    @Preview
    @Composable
    fun Prev() {
        NotAdmin()
        //AdministrativeTools()
    }


    @Composable
    fun AdministrativeTools() {

        Column() {
            Spacer(modifier = Modifier.height(100.dp))
            Text(text = "Вы администратор", modifier = Modifier.padding(24.dp))
        }
    }

    private fun getUserDataFromSharedPref(): User {
        /** Получаем данные пользователя из SharedPreference */

        val sharedPreferences = requireActivity().getSharedPreferences(
            SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        )  // Получаем настройки сохраненые в Authorization

        val userData = sharedPreferences.getString(KEY_USER_DATA, null)
        if (userData != null) {
            // Возврат активного(авторизованного) пользователя
            return Json.decodeFromString(User.serializer(), userData)
        } else Toast.makeText(
            requireContext(), "Ошибка чтения данных пользователя", Toast.LENGTH_SHORT
        ).show()
        return User() // Возврат пустого пользователя
    }
}

