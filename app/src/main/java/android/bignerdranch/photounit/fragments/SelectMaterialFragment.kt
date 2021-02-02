package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.model.MaterialUsed
import android.bignerdranch.photounit.utilits.viewHolder.ItemViewHolderExtraLite
import android.bignerdranch.photounit.viewModels.TaskViewModel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_select_material.*
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.listener.OnClickEventListener

class SelectMaterialFragment : BaseFragment(R.layout.fragment_select_material) {
    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var smartRecyclerAdapter: SmartRecyclerAdapter
    lateinit var observerArrayMaterial: Observer<ArrayList<MaterialUsed>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createLiveDataObserver()
        taskViewModel.httpGetListMaterial(taskViewModel.arrayMaterialList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLiveDataObserve()
    }

    private fun createList(arrayMaterial: ArrayList<MaterialUsed>) {
        smartRecyclerAdapter = SmartRecyclerAdapter
            .items(arrayMaterial)
            .map(MaterialUsed::class, ItemViewHolderExtraLite::class)
            .add(OnClickEventListener {

                taskViewModel.selectMaterial.value!!.add(arrayMaterial[it.position])  // Добавляем выбранный материал в массив
                findNavController().navigate(R.id.action_selectMaterialFragment_to_countMaterialFragment)
            })
            .into(list_material_select)
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
}