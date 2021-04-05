package android.bignerdranch.mobilecrm.activity

import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.databinding.ActivityMainBinding
import android.bignerdranch.mobilecrm.model.otherModel.User
import android.bignerdranch.mobilecrm.utilits.helpers.SHARED_PREF_NAME
import android.bignerdranch.mobilecrm.utilits.helpers.VERSION_APP
import android.bignerdranch.mobilecrm.utilits.helpers.detectUserIcon
import android.bignerdranch.mobilecrm.utilits.helpers.detectUserStatus
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.jakewharton.threetenabp.AndroidThreeTen


class MainActivity : AppCompatActivity() {

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

        AndroidThreeTen.init(this)  // Включает подержку новых методов для старых тел.
        sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
    }

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

    private fun changeListenerNavigateDestination() {
        /**Функция отключает кнопку назад на authorizationFragment*/
        val navController = this.findNavController(R.id.nav_host_fragment_container)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.authorizationFragment) {
                sharedPref.edit().clear().apply()
                binding.toolbar.navigationIcon = null
            }
        }
    }
}
