package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.utilits.SharedViewModel
import android.bignerdranch.photounit.utilits.districtArray
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_list_distric.*


class DistrictListFragment : Fragment( R.layout.fragment_list_distric) {

    private val sharedModel: SharedViewModel by activityViewModels()
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**Если пользователь выбирает район, то происходит изменение в liveData idDistrict
         * отслеживаем через слушатель и после выбра района меняем фрагмент путем изменения
         * liveData idCurrentFragment, activity это отслеживает и меняет фрагмент*/
        sharedModel.idDistrict.observe(this, Observer {
            sharedModel.idCurrentFragment.value = R.layout.fragment_street_list
        })
    }

    override fun onStart() {
        super.onStart()
        createList()
    }

    private fun createList() {
        // создаем адаптер и наполняем его данными
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, districtArray)
        list_district.adapter = adapter // напоняем view данными из адаптера
        list_district.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            sharedModel.getIdDistrictFromMap(districtArray[i]) // id выбраного микрорайона помещаем в LiveData
        }
    }
}
