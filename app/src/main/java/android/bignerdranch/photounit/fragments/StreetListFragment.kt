package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.utilits.SharedViewModel
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_street_list.*


class StreetListFragment : BaseFragment(R.layout.fragment_street_list) {

    private val sharedModel: SharedViewModel by activityViewModels()
    lateinit var adapter: ArrayAdapter<String>

    lateinit var observerMapStreet: Observer<Map<String, String>>
    lateinit var observerListStreet: Observer<ArrayList<String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createLiveDataObserver()
    }

    override fun onStart() {
        super.onStart()
        setLiveDataObserve()
    }

    override fun onStop() {
        removeLiveDataObserver()
        super.onStop()
    }

    private fun createList(dataArray: ArrayList<String>) {
        /** Создаем адаптер и наполняем его данными*/
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataArray)
        list_street.adapter = adapter
        list_street.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            sharedModel.tempSelectNameStreet = dataArray[i] + " " // Формируем строку полного адреса для TextView
            sharedModel.getIdStreetFromMap(dataArray[i]) // id выбраного микрорайона помещаем в LiveData путем извлечения из словаря который пришел через get, по его имени.
            navController.navigate(R.id.action_streetListFragment_to_homeListFragment)
        }
    }

    private fun setLiveDataObserve() {
    /** Подключаем слушатели к LiveData*/
        sharedModel.mapStreet.observe(this, observerMapStreet)
        sharedModel.listStreet.observe(this,observerListStreet)
    }

    private fun createLiveDataObserver() {
        /** Создаем слушатели отдельно*/
        observerMapStreet = Observer {
            println(it)
            sharedModel.createListStreet(it) // Как только был получен ответ от get запроса в виде Map, образуем ArrayList для ListView
        }
        observerListStreet = Observer {
            createList(it) // Создаем ListView как только получили массив с названиями улиц
        }
    }

    private fun removeLiveDataObserver() {
        sharedModel.mapStreet.removeObserver(observerMapStreet)
        sharedModel.listStreet.removeObserver(observerListStreet)
    }
}