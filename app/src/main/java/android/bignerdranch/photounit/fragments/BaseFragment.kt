package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.activity.MainActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation


open class BaseFragment(layout: Int) : Fragment(layout) {

    lateinit var mainActivity: MainActivity
    lateinit var navController: NavController

    override fun onStart() {
        super.onStart()
        initTools()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    private fun initTools() {
        mainActivity = requireActivity() as MainActivity
    }

}