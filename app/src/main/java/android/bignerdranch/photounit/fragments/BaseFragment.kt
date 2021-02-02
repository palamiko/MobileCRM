package android.bignerdranch.photounit.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation


open class BaseFragment(layout: Int) : Fragment(layout) {


    lateinit var navController: NavController



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }


    fun getText(editText: EditText): String = editText.text.toString()

}