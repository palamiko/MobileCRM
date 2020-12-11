package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.utilits.GET_STREET
import android.bignerdranch.photounit.utilits.SharedViewModel
import android.bignerdranch.photounit.utilits.districtArray
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_street_list.*


class StreetListFragment : Fragment(R.layout.fragment_street_list) {

    private val sharedModel: SharedViewModel by activityViewModels()
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedModel.httpGetJson(GET_STREET, sharedModel.idDistrict.value.toString()) // Отправляем get запрос с id района. Получим Map с улицей и ее id
    }

    override fun onStart() {
        super.onStart()
        setLiveDataObserve()
    }

    private fun createList(dataArray: ArrayList<String>) {
        /** Создаем адаптер и наполняем его данными*/
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataArray)
        list_street.adapter = adapter
        list_street.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            sharedModel.getIdDistrictFromMap(districtArray[i]) // id выбраного микрорайона помещаем в LiveData
        }
    }

    private fun setLiveDataObserve() {
    /** Подключаем слушатели к LiveData*/
        sharedModel.mapStreet.observe(this, Observer {
            println(it)
            sharedModel.createListStreet(it) // Как только был получен ответ от get запроса в виде Map, образуем ArrayList для ListView
        })

        sharedModel.listStreet.observe(this, Observer {
            createList(it) // Создаем ListView как только получили массив с названиями улиц
        })
    }
}