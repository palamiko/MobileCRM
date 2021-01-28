package android.bignerdranch.photounit.activity

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.model.User
import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.SHARED_PREF_NAME
import android.bignerdranch.photounit.utilits.detectUserIcon
import android.bignerdranch.photounit.utilits.detectUserStatus
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
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), DataBaseCommunication {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    lateinit var sharedPref: SharedPreferences
    private lateinit var headerUserStatus: TextView
    private lateinit var headerUserName: TextView
    private lateinit var headerUserIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
    }

    override fun onStart() {
        super.onStart()
        initNavigationComponent()
    }

    private fun initNavigationComponent() {

        navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController  // Инициализируем NavController
        appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)
        appBarConfiguration.topLevelDestinations.addAll(
            setOf(
                R.id.taskFragment,
                R.id.photoFragment
            )
        )  // Указываем "верхние" уровни навирации. То где будет гамбургер.

        toolbar.setupWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)

        changeListenerNavigateDestination()
        findNavigationView()
    }

    fun changeHeader(user: User) {
        headerUserName.text = user.name
        headerUserStatus.text = detectUserStatus(user.status)
        headerUserIcon.setImageDrawable(detectUserIcon(user.status))
    }

    private fun findNavigationView() {
        if (nav_view.headerCount > 0) {
            // avoid NPE by first checking if there is at least one Header View available
            val headerView: View = nav_view.getHeaderView(0)
            headerUserStatus = headerView.findViewById(R.id.status_user)
            headerUserName = headerView.findViewById(R.id.name_user)
            headerUserIcon = headerView.findViewById(R.id.photoUser)

        } else println("ЛОХ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")


    }

    private fun changeListenerNavigateDestination() {
        /**Функция отключает кнопку назад на authorizationFragment*/
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.authorizationFragment) {
                sharedPref.edit().clear().apply()
                toolbar.navigationIcon = null
            }
        }
    }
}
