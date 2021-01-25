package android.bignerdranch.photounit.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.fragments.PhotoFragment
import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.SHARED_PREF_NAME
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), DataBaseCommunication {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    // FireBase
    private lateinit var mAuth: FirebaseAuth
    lateinit var myBackStackEntry: Array<NavBackStackEntry>
    lateinit var sharedPref: SharedPreferences
    var test = 0
    lateinit var resultLauncher: ActivityResultLauncher<Intent>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        mAuth = FirebaseAuth.getInstance()

        println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode === Activity.RESULT_OK) {
                // There are no request codes
                //val data: Intent? = result.data
                //doSomeOperations()
                test = 1
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initNavigationComponent()
    }

    fun openSomeActivityForResult() {
        val intent = Intent(this, PhotoFragment::class.java)
        resultLauncher.launch(intent)
    }

    private fun initNavigationComponent() {

        navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController  // Инициализируем NavController

        navController.graph = detectStartFragment()  // Определяем какой фрагмент запустить первым

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
    }

    private fun detectStartFragment(): NavGraph {
        /**Функция проверяет авторизован ли пользователь и
         * в зависимости от этого включает фрагмент.*/

        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph)


        if (mAuth.currentUser != null) {
            if (test == 0) {
                graph.startDestination = R.id.navigationTask
            } else {
                graph.startDestination = R.id.photoFragment
            }

        } else {
            graph.startDestination = R.id.authorizationFragment
        }
        return graph
    }

    @SuppressLint("RestrictedApi")
    private fun changeListenerNavigateDestination() {
        /** Функция отслеживает перемещения по нафигационному графу */

        navController.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id == R.id.authorizationFragment) {
                mAuth.signOut()  // Выходим из Firebase
                myBackStackEntry = navController.backStack.toTypedArray() // Сохряняем бэкстек для передачи в authFragment
                navController.backStack.removeAll(navController.backStack) // Очищаем бэкстэк
                toolbar.navigationIcon = null
            }
        }
    }
}
