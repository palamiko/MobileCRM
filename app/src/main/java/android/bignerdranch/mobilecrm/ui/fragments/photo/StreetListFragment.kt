package android.bignerdranch.mobilecrm.ui.fragments.photo

import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.databinding.FragmentStreetListBinding
import android.bignerdranch.mobilecrm.model.viewModels.SharedViewModel
import android.bignerdranch.mobilecrm.network.NetworkModule
import android.bignerdranch.mobilecrm.ui.fragments.BaseFragment
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi


class StreetListFragment : BaseFragment(R.layout.fragment_street_list) {

    private val sharedModel: SharedViewModel by activityViewModels()
    private var binding: FragmentStreetListBinding? = null

    @ExperimentalSerializationApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentStreetListBinding.bind(view)
        binding = fragmentBinding

        binding?.progressBarStreetFragment?.isVisible = true
        coroutineScope.launch(exceptionHandler) {
            getStreet()
            sharedModel.createListStreet()
            createListView(sharedModel.streetNameList)
        }
    }

    @ExperimentalSerializationApi
    private suspend fun getStreet() = withContext(Dispatchers.IO) {
        /**Делает запрос списка улиц с сервера, результат возвращает в LiveData*/
        val networkApiService = NetworkModule().networkApiService
        sharedModel.streetListResponse = networkApiService.getStreetList(sharedModel.mDistrict.id)
    }

    private suspend fun createListView(dataArray: ArrayList<String>) = withContext(Dispatchers.Main) {
        /** Создаем адаптер и наполняем его данными*/
        binding?.progressBarStreetFragment?.isGone = true
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataArray)
        binding?.listStreet?.apply {
            adapter = arrayAdapter
            setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
                sharedModel.getSelectStreet(i)
                findNavController().navigate(R.id.action_streetListFragment_to_homeListFragment)
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}