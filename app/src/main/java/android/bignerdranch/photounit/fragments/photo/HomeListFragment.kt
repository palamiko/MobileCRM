package android.bignerdranch.photounit.fragments.photo

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.databinding.FragmentHomeListBinding
import android.bignerdranch.photounit.fragments.BaseFragment
import android.bignerdranch.photounit.network.NetworkModule
import android.bignerdranch.photounit.viewModels.SharedViewModel
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


class HomeListFragment : BaseFragment(R.layout.fragment_home_list) {

    private val sharedModel: SharedViewModel by activityViewModels()
    private var binding: FragmentHomeListBinding? = null

    @ExperimentalSerializationApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentHomeListBinding.bind(view)
        binding = fragmentBinding

        binding?.progressBarHomeFragment?.isVisible = true
        coroutineScope.launch {
            getHome()
            sharedModel.createListHome()
            createListView(sharedModel.homeNumberList)
        }
    }

    @ExperimentalSerializationApi
    suspend fun getHome() = withContext(Dispatchers.IO) {
        val networkApiService = NetworkModule().networkApiService
        sharedModel.homeListResponse = networkApiService.getHomeList(sharedModel.mDistrict.id, sharedModel.mStreet.id)
    }

    private suspend fun createListView(dataArray: ArrayList<String>) = withContext(Dispatchers.Main) {
        /** Создаем адаптер и наполняем его данными*/
        binding?.progressBarHomeFragment?.isGone = true
        val myAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataArray)
        binding?.listHomes?.apply {
            adapter = myAdapter
            setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
                sharedModel.apply {
                    getSelectHome(i) // Позицию выбраного дома помещаем в ViewModel
                    setCurrentTextFullAddress()
                }
                findNavController().navigate(R.id.action_homeListFragment_to_photoFragment)
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}