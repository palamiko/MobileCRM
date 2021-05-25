package android.bignerdranch.mobilecrm.ui.activity

import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.databinding.ActivityMainBinding
import android.bignerdranch.mobilecrm.model.otherModel.User
import android.bignerdranch.mobilecrm.model.viewModels.TaskViewModel
import android.bignerdranch.mobilecrm.utilits.helpers.*
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var sharedPref: SharedPreferences
    private lateinit var headerVerApp: TextView
    private lateinit var headerUserStatus: TextView
    private lateinit var headerUserName: TextView
    private lateinit var headerUserIcon: ImageView


    @ExperimentalSerializationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AndroidThreeTen.init(this)  // Включает подержку новых методов для старых тел.
        sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        sendToken()  // Получет личный токен в FB
    }

    @ExperimentalSerializationApi
    override fun onStart() {
        super.onStart()

        initNavigationComponent()
        changeListenerNavigateDestination()
        findNavigationView()
    }

    private fun initNavigationComponent() {

        val navController = this.findNavController(R.id.nav_host_fragment_container)
        val appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        appBarConfiguration.topLevelDestinations.addAll(  // Указываем "верхние" уровни навигации. То где будет гамбургер.
            setOf(R.id.taskFragment, R.id.photoFragment)
        )

        binding.apply {
            toolbar.setupWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }

    fun changeHeader(user: User) {
        headerUserName.text = user.name
        headerUserStatus.text = detectUserStatus(user.status)
        headerUserIcon.setImageDrawable(detectUserIcon(user.status))
        headerVerApp.text = VERSION_APP
    }

    private fun findNavigationView() {
        if (binding.navView.headerCount > 0) {

            val headerView: View = binding.navView.getHeaderView(0)
            headerUserStatus = headerView.findViewById(R.id.status_user)
            headerUserName = headerView.findViewById(R.id.name_user)
            headerUserIcon = headerView.findViewById(R.id.photoUser)
            headerVerApp = headerView.findViewById(R.id.tv_ver_app)

        } else println("Ошибка в MainActivity")
    }

    @ExperimentalSerializationApi
    private fun changeListenerNavigateDestination() {
        /** Функция отключает кнопку назад на authorizationFragment */
        val navController = this.findNavController(R.id.nav_host_fragment_container)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.authorizationFragment) {
                sharedPref.edit().clear().apply()
                binding.toolbar.navigationIcon = null
            }
        }
    }


    @ExperimentalSerializationApi
    private fun sendToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Получаем модель User из SharedPref если пользователь до этого авторизовывался
            val user = getUserFromSharedPref()
            // Получаем токен из FireBase
            val token = task.result
            // Отправляем токен на сервер CRM
            if (token != null && user != null) {
                sendTokenToServer(user.id.toString(), user.login, token)
                Log.d(TAG, token.toString())
            }
        })
    }

    @ExperimentalSerializationApi
    private fun sendTokenToServer(id_master: String, login_master: String, token: String) {
        /** Отправляет данные пользователя на сервер СРМ */
        val taskViewModel: TaskViewModel by viewModels()
        taskViewModel.sendTokenToServ(id_master, login_master, token)
    }

    private fun getUserFromSharedPref(): User? {
        var user: User? = null
        // Получаем настройки сохраненые в Authorization
        sharedPref = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)

        val userDataString = sharedPref.getString(KEY_USER_DATA, null)
        if (userDataString != null) {
            user = Json.decodeFromString(User.serializer(), userDataString)
        }
        return user
    }
}
