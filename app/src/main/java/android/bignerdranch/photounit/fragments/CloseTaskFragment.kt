package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.model.DataCloseTask
import android.bignerdranch.photounit.model.MaterialUsed
import android.bignerdranch.photounit.model.modelsDB.TaskList
import android.bignerdranch.photounit.utilits.FINISH_OK
import android.bignerdranch.photounit.utilits.SwipeRemoveItemBinder
import android.bignerdranch.photounit.utilits.TASK_COMPLETED
import android.bignerdranch.photounit.utilits.viewHolder.ItemViewHolderLite
import android.bignerdranch.photounit.viewModels.TaskViewModel
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.android.synthetic.main.fragment_close_task.*
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.listener.OnClickEventListener


class CloseTaskFragment : BaseFragment(R.layout.fragment_close_task) {

    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var smartRecyclerAdapter: SmartRecyclerAdapter


    override fun onStart() {
        super.onStart()
        initView()
    }

    override fun onResume() {
        super.onResume()

        if (taskViewModel.selectMaterial.value != null ) {
            createRecyclerViewAdapter(taskViewModel.selectMaterial.value!!)
        }
        // Воссанавливает значения полей
        te_input_comment_close.setText(taskViewModel.textEditComment.value)
        te_input_summ.setText(taskViewModel.textEditSumma.value)
    }

    override fun onPause() {

        // Сохраняет значения полей
        taskViewModel.textEditComment.value = te_input_comment_close.text.toString()
        taskViewModel.textEditSumma.value = te_input_summ.text.toString()
        super.onPause()

    }

    private fun detectAddress(item: TaskList): String {
        /**Эта ф-ия проверяет приходит ли заявка с полным адресом или адрес забит от руки
         * и возвращает разные вариации в поле адрес*/
        return if (item.name_ru == null) {
            item.address
        } else "${item.name_ru} ${item.building_number}-${item.flat}"
    }


    private fun initView() {
        tv_address_close_task.text = detectAddress(taskViewModel.singleTask.value!!)
        tv_comment_close_task.text = taskViewModel.singleTask.value!!.comments

        detectPayTask()
        initializationBtnClickListener()
        createListenerResponseCloseTask()
    }

    private fun createRecyclerViewAdapter(arrayMaterialSelect: ArrayList<MaterialUsed>) {
        println("#####################$arrayMaterialSelect")

        smartRecyclerAdapter = SmartRecyclerAdapter
            .items(arrayMaterialSelect)
            .map(MaterialUsed::class, ItemViewHolderLite::class)
            .add(OnClickEventListener {
                Toast.makeText(requireContext(), "onClick ${it.position}", Toast.LENGTH_SHORT)
                    .show()
            })
            .add(SwipeRemoveItemBinder(ItemTouchHelper.LEFT) {
                // Remove item from SmartRecyclerAdapter data set
                smartRecyclerAdapter.removeItem(it.viewHolder.adapterPosition)
                println(taskViewModel.selectMaterial.value)
            })
            .into(list_used_material)
    }

    private fun createListenerResponseCloseTask() {
        /**Показывает тост-информация об успешном/не успешном закрытии заявки*/
        taskViewModel.resultCloseTask.observe(this, {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT)
                .show() // Слушает ответ от сервера после закрытия заявки
            taskViewModel.resultCloseTask.value = null
        })
    }

    private fun initializationBtnClickListener() {
        /**Инициализация слушателей нажатий кнопок*/
        btn_close_task.setOnClickListener {
            val dataCloseTask = DataCloseTask (
                id_task = taskViewModel.singleTask.value!!.id,
                state_task = TASK_COMPLETED,
                id_user = taskViewModel.ldUserData.value!!.id.toString(),
                comment = te_input_comment_close.text.toString(),
                finish = FINISH_OK,
                summ = te_input_summ.text.toString(),
                material = taskViewModel.selectMaterial.value!!
            )

            taskViewModel.closeTask (
                dataCloseTask = dataCloseTask,
                result = taskViewModel.resultCloseTask
            )

            navController.navigate(R.id.action_closeTaskFragment_to_taskFragment)
        }

        btn_add_material.setOnClickListener {
            navController.navigate(R.id.action_closeTaskFragment_to_selectMaterialFragment)
        }
    }

    private fun detectPayTask() {
        /**Функция определяет платная заявка или нет и меняет надпись в RecyclerView*/
        if (taskViewModel.singleTask.value!!.ispayable != "true") {
            te_input_summ.isInvisible = true
            tv_summ.isInvisible = true
        }
    }
}
