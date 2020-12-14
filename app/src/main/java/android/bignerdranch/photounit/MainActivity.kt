package android.bignerdranch.photounit

import android.bignerdranch.photounit.fragments.DistrictListFragment
import android.bignerdranch.photounit.fragments.HomeListFragment
import android.bignerdranch.photounit.fragments.PhotoFragment
import android.bignerdranch.photounit.fragments.StreetListFragment
import android.bignerdranch.photounit.utilits.MyViewModel
import android.bignerdranch.photounit.utilits.SharedViewModel
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer


const val REQUEST_IMAGE_CAPTURE = 1

class MainActivity : AppCompatActivity() {

    lateinit var managerFragment: FragmentManager
    val viewModelMainActivity: MyViewModel by viewModels()
    private val sharedModel: SharedViewModel by viewModels()

    private lateinit var fragment: Fragment
    private var backStack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFragmentManager()
    }

    override fun onSupportNavigateUp(): Boolean {

        onBackPressed()
        return true
    }

    private fun initFragmentManager(){
        managerFragment = supportFragmentManager
        setObserverCurrentFragment()
        openFragment()
    }

    private fun setObserverCurrentFragment() {
        /** Функция запускает слушателя LiveData для idCurrentFragment
         *  и сменяет фрагмент на нужный*/
        sharedModel.idCurrentFragment.observe(this, Observer {
            openFragment()
        })
    }

    private fun openFragment() {
        /** Функция смены фрагмента*/

        var visibleBtnBack = true
        val transaction = managerFragment.beginTransaction()

        when (sharedModel.idCurrentFragment.value) { // Смотрим в ViewModel какой id франмента и меняем на него
            null -> {
                fragment = PhotoFragment()
                backStack = false
                visibleBtnBack = false
            }

            R.layout.fragment_photo -> {
                fragment = PhotoFragment()
                backStack = false
                visibleBtnBack = false
            }

            R.layout.fragment_list_distric -> {
                fragment = DistrictListFragment()
                backStack = true
            }
            R.layout.fragment_street_list -> {
                fragment = StreetListFragment()
                backStack = true
            }
            R.layout.fragment_home_list -> {
                fragment = HomeListFragment()
                backStack = true
            }
        }

        transaction.replace(R.id.container, fragment)
        if (backStack) transaction.addToBackStack(null)
        transaction.commit()
        setVisibleBtnBackToolbar(visibleBtnBack)
    }

    private fun setVisibleBtnBackToolbar(visible: Boolean) {
    /** Функция показывает или убирает кнопку назат в туллбаре*/

        supportActionBar?.setDisplayHomeAsUpEnabled(visible)
        supportActionBar?.setDisplayShowHomeEnabled(visible)
    }
}