package android.bignerdranch.photounit.activity

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.databinding.ActivityMainBinding
import android.bignerdranch.photounit.model.User
import android.bignerdranch.photounit.utilits.*
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.jakewharton.threetenabp.AndroidThreeTen



class MainActivity : AppCompatActivity(), DataBaseCommunication {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    lateinit var sharedPref: SharedPreferences
    private lateinit var headerVerApp: TextView
    private lateinit var headerUserStatus: TextView
    private lateinit var headerUserName: TextView
    private lateinit var headerUserIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AndroidThreeTen.init(this)
        sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
    }


    override fun onStart() {
        super.onStart()
        initNavigationComponent()
    }

    private fun initNavigationComponent() {

        navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController  // Инициализируем NavController
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        appBarConfiguration.topLevelDestinations.addAll(
            setOf(
                R.id.taskFragment,
                R.id.photoFragment
            )
        )  // Указываем "верхние" уровни навирации. То где будет гамбургер.

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        changeListenerNavigateDestination()
        findNavigationView()
    }

    fun changeHeader(user: User) {
        headerUserName.text = user.name
        headerUserStatus.text = detectUserStatus(user.status)
        headerUserIcon.setImageDrawable(detectUserIcon(user.status))
        headerVerApp.text = VERSION_APP
    }

    private fun findNavigationView() {
        if (binding.navView.headerCount > 0) {
            // avoid NPE by first checking if there is at least one Header View available
            val headerView: View = binding.navView.getHeaderView(0)
            headerUserStatus = headerView.findViewById(R.id.status_user)
            headerUserName = headerView.findViewById(R.id.name_user)
            headerUserIcon = headerView.findViewById(R.id.photoUser)
            headerVerApp = headerView.findViewById(R.id.tv_ver_app)

        } else println("Ошибка в MainActivity")
    }

    private fun changeListenerNavigateDestination() {
        /**Функция отключает кнопку назад на authorizationFragment*/
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.authorizationFragment) {
                sharedPref.edit().clear().apply()
                binding.toolbar.navigationIcon = null
            }
        }
    }
}
