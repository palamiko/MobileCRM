package android.bignerdranch.mobilecrm.ui.fragments.task

import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.databinding.FragmentSelectMaterialBinding
import android.bignerdranch.mobilecrm.model.networkModel.MaterialUsed
import android.bignerdranch.mobilecrm.model.viewModels.TaskViewModel
import android.bignerdranch.mobilecrm.ui.fragments.BaseFragment
import android.bignerdranch.mobilecrm.utilits.recyclerView.ItemViewHolderExtraLite
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import smartadapter.SmartRecyclerAdapter
import smartadapter.diffutil.DiffUtilExtension
import smartadapter.filter.FilterExtension
import smartadapter.get
import smartadapter.viewevent.listener.OnClickEventListener
import kotlin.random.Random

class SelectMaterialFragment : BaseFragment(R.layout.fragment_select_material) {
    private val taskViewModel: TaskViewModel by activityViewModels()
    private var binding: FragmentSelectMaterialBinding? = null
    lateinit var smartAdapter: SmartRecyclerAdapter

    lateinit var observerArrayMaterial: Observer<ArrayList<MaterialUsed>>

    private val predicate = object : DiffUtilExtension.DiffPredicate<Any> {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }



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
        smartAdapter = SmartRecyclerAdapter
            .items(arrayMaterial)
            .map(MaterialUsed::class,  SimpleFilterItemViewHolder::class)
            .add(OnClickEventListener {

                taskViewModel.selectMaterial.value!!.add(arrayMaterial[it.position])  // Добавляем выбранный материал в массив
                findNavController().navigate(R.id.action_selectMaterialFragment_to_countMaterialFragment)
            })
            .add(
                FilterExtension(
                    filterPredicate = { item, constraint ->
                        when (item) {
                            is MaterialUsed -> item.toString().contains(constraint)
                            else -> true
                        }
                    },
                    loadingStateListener = { isLoading ->
                        // Set loading progress visibility
                    }
                )
            )
            .into(binding?.listMaterialSelect!!)

        binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("!!!!!!!!!!!!!!!!!!!!!!!")
                filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                filter(newText)
                return true
            }
        })
    }

    fun filter(query: String?) {
        val filterExtension: FilterExtension = smartAdapter.get()
        filterExtension.filter(lifecycleScope, query, autoSetNewItems = true)
        println(query)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.refresh, menu)
        return super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> refresh()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refresh() {
        val items = (0..10000).map { Random.nextInt(100, 10000) }.toMutableList()
        smartAdapter.setItems(items)
        println(items)
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

class SimpleFilterItemViewHolder(view: ViewGroup) : ItemViewHolderExtraLite(view) {



}

