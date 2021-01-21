package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.viewModels.TaskViewModel
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_count_material.*

class CountMaterialFragment : BaseFragment(R.layout.fragment_count_material) {
    private val taskViewModel: TaskViewModel by activityViewModels()


    override fun onResume() {
        super.onResume()

        te_count_material.hint = taskViewModel.selectMaterial.value?.last()?.ed_izm  // Извлекаем еденицы измерения материала
        tv_name_used_material.text = taskViewModel.selectMaterial.value?.last()?.name  // Извлекаем имя выбраного материала
        btn_save_used_material.setOnClickListener {
            taskViewModel.selectMaterial.value?.last()?.count = te_count_material.text.toString()  // Добавляем колличество выбаного материала
            navController.navigate(R.id.action_countMaterialFragment_to_closeTaskFragment)
        }
    }
}