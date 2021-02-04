package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.databinding.FragmentDistricListBinding
import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.districtArray
import android.bignerdranch.photounit.viewModels.SharedViewModel
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController


class DistrictListFragment : BaseFragment( R.layout.fragment_distric_list), DataBaseCommunication {

    private val sharedModel: SharedViewModel by activityViewModels()
    lateinit var adapter: ArrayAdapter<String>

    private var binding: FragmentDistricListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**Если пользователь выбирает район, то происходит изменение в liveData idDistrict
         * отслеживаем через слушатель и после выбра района меняем фрагмент путем изменения
         * liveData idCurrentFragment, activity это отслеживает и меняет фрагмент*/
        sharedModel.idDistrict.observe(this, {
            sharedModel.httpGetListStreet(sharedModel.idDistrict.value.toString(), sharedModel.mapStreet) // Отправляем get запрос с id района. Получим Map с улицей и ее id
            findNavController().navigate(R.id.action_districtListFragment_to_streetListFragment)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentDistricListBinding.bind(view)
        binding = fragmentBinding
    }

    override fun onStart() {
        super.onStart()
        createList()
    }

    private fun createList() {
        // создаем адаптер и наполняем его данными
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, districtArray)
        binding?.listDistrict?.adapter = adapter // напоняем view данными из адаптера
        binding?.listDistrict?.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            sharedModel.tempSelectNameDistrict = districtArray[i] + " "// Формируем строку полного адреса для TextView
            sharedModel.getIdDistrictFromMap(districtArray[i]) // id выбраного микрорайона помещаем в LiveData
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
