package android.bignerdranch.mobilecrm.fragments.task

import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.databinding.FragmentCountMaterialBinding
import android.bignerdranch.mobilecrm.fragments.BaseFragment
import android.bignerdranch.mobilecrm.utilits.helpers.hideKeyboard
import android.bignerdranch.mobilecrm.utilits.helpers.showKeyboard
import android.bignerdranch.mobilecrm.viewModels.TaskViewModel
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

class CountMaterialFragment : BaseFragment(R.layout.fragment_count_material) {
    private val taskViewModel: TaskViewModel by activityViewModels()
    private var binding: FragmentCountMaterialBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentCountMaterialBinding.bind(view)
        binding = fragmentBinding
        view.showKeyboard() // Показываем клавиатуру

        binding?.apply {
            teCountMaterial.hint = taskViewModel.selectMaterial.value?.last()?.ed_izm  // Извлекаем еденицы измерения материала
            tvNameUsedMaterial.text = taskViewModel.selectMaterial.value?.last()?.name  // Извлекаем имя выбраного материала

            btnSaveUsedMaterial.setOnClickListener {
                val countText = binding?.teCountMaterial?.text.toString()
                if (countText.isNotEmpty()) {
                    taskViewModel.getSelectMaterial().last().count = binding?.teCountMaterial?.text.toString()  // Добавляем колличество выбаного материала
                    findNavController().navigate(R.id.action_countMaterialFragment_to_closeTaskFragment)
                } else Toast.makeText(requireContext(), "Укажите колличество материала", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onDestroyView() {
        if (taskViewModel.getSelectMaterial().last().count == null) taskViewModel.selectMaterial.value?.removeLast()
        view?.hideKeyboard()
        binding = null
        super.onDestroyView()
    }
}