package android.bignerdranch.photounit.utilits

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {

    var currentFragment = MutableLiveData<Fragment>()

}