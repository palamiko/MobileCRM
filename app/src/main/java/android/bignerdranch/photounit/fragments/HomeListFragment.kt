package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.viewModels.SharedViewModel
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_home_list.*


class HomeListFragment : BaseFragment(R.layout.fragment_home_list) {

    private val sharedModel: SharedViewModel by activityViewModels()
    lateinit var adapter: ArrayAdapter<String>

    lateinit var observerMapHome: Observer<Map<String, String>>
    lateinit var observerListHome: Observer<ArrayList<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createLiveDataObserver()

        // Отправляем get запрос с id района, id улицы. Получим Map с номерами домов
        sharedModel.httpGetListHome(
            sharedModel.idDistrict.value.toString(),
            sharedModel.idStreet.value.toString(),
            sharedModel.mapHome
        )
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
        list_homes.adapter = adapter
        list_homes.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            sharedModel.tempSelectNameHome = dataArray[i] // Формируем строку полного адреса для TextView
            sharedModel.getIdHomeFromMap(dataArray[i]) // id выбраного дома помещаем в LiveData путем извлечения из словаря который пришел через get, по его имени.
            sharedModel.setCurrentTextFullAddress()
            findNavController().navigate(R.id.action_homeListFragment_to_photoFragment)
        }
    }

    private fun setLiveDataObserve() {
        /** Подключаем слушатели к LiveData*/
        sharedModel.mapHome.observe(this, observerMapHome)
        sharedModel.listHome.observe(this, observerListHome)
    }

    private fun createLiveDataObserver() {
        /** Создаем слушатели отдельно*/
        observerMapHome = Observer {
            println(it)
            sharedModel.createListHome(it) // Как только был получен ответ от get запроса в виде Map, образуем ArrayList для ListView
        }
        observerListHome = Observer {
            createList(it) // Создаем ListView как только получили массив с названиями улиц
        }
    }

    private fun removeLiveDataObserver() {
        /** Отключаем слушатели*/
        sharedModel.mapHome.removeObserver(observerMapHome)
        sharedModel.listHome.removeObserver(observerListHome)
    }
}