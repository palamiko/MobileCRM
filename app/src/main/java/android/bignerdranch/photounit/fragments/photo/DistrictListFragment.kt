package android.bignerdranch.photounit.fragments.photo

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.databinding.FragmentDistricListBinding
import android.bignerdranch.photounit.fragments.BaseFragment
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
    private var binding: FragmentDistricListBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentDistricListBinding.bind(view)
        binding = fragmentBinding
        createList()
    }

    private fun createList() {
        /** Функция создает адаптер ListView и наполняет его данными*/

        val myAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, districtArray)

        binding?.listDistrict?.apply {
            adapter = myAdapter // напоняем view данными из адаптера
            setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
                sharedModel.getSelectDistrict(districtArray[i]) // Создаем экземпляр выбраного района в ViewModel
                findNavController().navigate(R.id.action_districtListFragment_to_streetListFragment)
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
