package android.bignerdranch.photounit.fragments.task

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.databinding.FragmentSelectMaterialBinding
import android.bignerdranch.photounit.fragments.BaseFragment
import android.bignerdranch.photounit.model.MaterialUsed
import android.bignerdranch.photounit.utilits.viewHolder.ItemViewHolderExtraLite
import android.bignerdranch.photounit.viewModels.TaskViewModel
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.listener.OnClickEventListener

class SelectMaterialFragment : BaseFragment(R.layout.fragment_select_material) {
    private val taskViewModel: TaskViewModel by activityViewModels()
    private var binding: FragmentSelectMaterialBinding? = null

    lateinit var observerArrayMaterial: Observer<ArrayList<MaterialUsed>>


    @ExperimentalSerializationApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentSelectMaterialBinding.bind(view)
        binding = fragmentBinding

        createLiveDataObserver()
        setLiveDataObserve()
        taskViewModel.viewModelScope.launch{getListMaterial()}
    }

    @ExperimentalSerializationApi
    suspend fun getListMaterial() {

        binding?.progressBarSelectMaterial?.isVisible = taskViewModel.arrayMaterialList.value.isNullOrEmpty()
        taskViewModel.getMaterial()
        binding?.progressBarSelectMaterial?.isGone = true
    }

    private fun createList(arrayMaterial: ArrayList<MaterialUsed>) {
        val smartRecyclerAdapter: SmartRecyclerAdapter = SmartRecyclerAdapter
            .items(arrayMaterial)
            .map(MaterialUsed::class, ItemViewHolderExtraLite::class)
            .add(OnClickEventListener {

                taskViewModel.selectMaterial.value!!.add(arrayMaterial[it.position])  // Добавляем выбранный материал в массив
                findNavController().navigate(R.id.action_selectMaterialFragment_to_countMaterialFragment)
            })
            .into(binding?.listMaterialSelect!!)
    }

    private fun createLiveDataObserver() {
        /** Создаем слушатели отдельно*/
        observerArrayMaterial = Observer {
            createList(it)
        }
    }

    private fun setLiveDataObserve() {
        /** Подключаем слушатели к LiveData*/
        taskViewModel.arrayMaterialList.observe(viewLifecycleOwner, observerArrayMaterial)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}