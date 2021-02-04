package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.databinding.FragmentCountMaterialBinding
import android.bignerdranch.photounit.viewModels.TaskViewModel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

class CountMaterialFragment : BaseFragment(R.layout.fragment_count_material) {
    private val taskViewModel: TaskViewModel by activityViewModels()
    private var binding: FragmentCountMaterialBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentCountMaterialBinding.bind(view)
        binding = fragmentBinding
    }


    override fun onResume() {
        super.onResume()

        binding?.teCountMaterial?.hint = taskViewModel.selectMaterial.value?.last()?.ed_izm  // Извлекаем еденицы измерения материала
        binding?.tvNameUsedMaterial?.text = taskViewModel.selectMaterial.value?.last()?.name  // Извлекаем имя выбраного материала
        binding?.btnSaveUsedMaterial?.setOnClickListener {
            taskViewModel.selectMaterial.value?.last()?.count = binding?.teCountMaterial?.text.toString()  // Добавляем колличество выбаного материала
            findNavController().navigate(R.id.action_countMaterialFragment_to_closeTaskFragment)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}