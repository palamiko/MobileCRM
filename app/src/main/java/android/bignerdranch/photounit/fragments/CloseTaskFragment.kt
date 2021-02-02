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
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.android.synthetic.main.fragment_close_task.*
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.listener.OnClickEventListener


class CloseTaskFragment : BaseFragment(R.layout.fragment_close_task) {

    private val taskVM: TaskViewModel by activityViewModels()
    private lateinit var smartRecyclerAdapter: SmartRecyclerAdapter


    override fun onStart() {
        super.onStart()
        initView()

    }

    override fun onResume() {
        super.onResume()
        if (taskVM.selectMaterial.value != null ) {
            createRecyclerViewAdapter(taskVM.selectMaterial.value!!)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Сохряняем заполенные поля
        if (te_input_comment_close.text != null) {
            outState.putString(TEXT_COMMENT, te_input_comment_close.text.toString())
        }
        if (te_input_summ.text != null) {
            outState.putString(TEXT_SUMM, te_input_summ.text.toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            savedInstanceState.getString(TEXT_COMMENT, "")
            savedInstanceState.getString(TEXT_SUMM, "")
        }
    }

    private fun detectAddress(item: TaskList): String {
        /**Эта ф-ия проверяет приходит ли заявка с полным адресом или адрес забит от руки
         * и возвращает разные вариации в поле адрес*/
        return if (item.name_ru == null) {
            item.address
        } else "${item.name_ru} ${item.building_number}-${item.flat}"
    }


    private fun initView() {
        tv_address_close_task.text = detectAddress(taskVM.singleTask.value!!)
        tv_comment_close_task.text = taskVM.singleTask.value!!.comments

        detectPayTask()
        initializationBtnClickListener()
    }

    private fun createRecyclerViewAdapter(arrayMaterialSelect: ArrayList<MaterialUsed>) {

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
                println(taskVM.selectMaterial.value)
            })
            .into(list_used_material)
    }

    private fun sendCloseTask() {
        val dataCloseTask = DataCloseTask (
            id_task = taskVM.singleTask.value!!.id,
            state_task = TASK_COMPLETED,
            id_user = taskVM.getUserData(id=true),
            comment = getText(te_input_comment_close),
            finish = FINISH_OK,
            summ = getText(te_input_summ),
            material = taskVM.selectMaterial.value!!
        )
        taskVM.closeTask (dataCloseTask = dataCloseTask)
    }

    private fun initializationBtnClickListener() {
        /**Инициализация слушателей нажатий кнопок*/
        btn_close_task.setOnClickListener {
            sendCloseTask()
            findNavController().navigate(R.id.action_closeTaskFragment_to_taskFragment)
        }

        btn_add_material.setOnClickListener {
            findNavController().navigate(R.id.action_closeTaskFragment_to_selectMaterialFragment)
        }
    }

    private fun detectPayTask() {
        /**Функция определяет платная заявка или нет и меняет надпись в RecyclerView*/
        if (taskVM.singleTask.value!!.ispayable != "true") {
            te_input_summ.isInvisible = true
            tv_summ.isInvisible = true
        }
    }


    companion object {
        const val TEXT_COMMENT = "text_edit_comment"
        const val TEXT_SUMM = "text_edit_summ"
    }
}
