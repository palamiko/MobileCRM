package android.bignerdranch.mobilecrm.ui.fragments.photo

import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.databinding.FragmentDistricListBinding
import android.bignerdranch.mobilecrm.model.modelsDB.districtArray
import android.bignerdranch.mobilecrm.model.viewModels.SharedViewModel
import android.bignerdranch.mobilecrm.ui.fragments.BaseFragment
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController


class DistrictListFragment : BaseFragment( R.layout.fragment_distric_list) {

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
