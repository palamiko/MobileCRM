package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.MainActivity
import android.bignerdranch.photounit.R
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation


open class BaseFragment(var layout: Int) : Fragment(layout) {

    lateinit var mainActivity: MainActivity
    lateinit var navController: NavController
    lateinit var actionBar: ActionBar

    override fun onStart() {
        super.onStart()
        initTools()
        detectVisibleBtnBackToolbar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    private fun initTools() {
        mainActivity = requireActivity() as MainActivity
        //navController = mainActivity.navController
        actionBar = mainActivity.actionBar
    }

    private fun detectVisibleBtnBackToolbar(){
        if (layout == R.layout.fragment_photo) {
            setVisibleBtnBackToolbar(false)
        } else {
            setVisibleBtnBackToolbar(true)
        }
    }

    private fun setVisibleBtnBackToolbar(visible: Boolean) {
        /** Функция показывает или убирает кнопку назат в туллбаре*/

        actionBar.setDisplayHomeAsUpEnabled(visible)
        actionBar.setDisplayShowHomeEnabled(visible)
    }
}