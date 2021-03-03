package android.bignerdranch.mobilecrm.utilits.recyclerView

import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.model.modelsDB.TaskModel
import android.bignerdranch.mobilecrm.utilits.helpers.detectProblem
import android.bignerdranch.mobilecrm.viewModels.TaskViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class MainViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val tvAddressTask: TextView = itemView.findViewById(R.id.tv_address_task)
    private val tvCommentTask: TextView = itemView.findViewById(R.id.tv_comment)
    private val tvDateCompletionTask: TextView = itemView.findViewById(R.id.date_of_completion)
    private val tvTimeCompletionTask: TextView = itemView.findViewById(R.id.tv_time_completion)
    private val tvPhoneTask: TextView = itemView.findViewById(R.id.tv_phone)
    private val tvIsMoneyTask: ImageView = itemView.findViewById(R.id.iv_is_money)
    private val tvTypeServiceTask: TextView = itemView.findViewById(R.id.tv_type_service)
    private val tvProblem: TextView = itemView.findViewById(R.id.tv_problem)

    fun bind(task: TaskModel, longClickListener: OnItemLongClickListener) {

        itemView.setOnLongClickListener {
            longClickListener.onItemLongClicked(task, detectAddress(task))
            return@setOnLongClickListener true
        }

        tvAddressTask.text = detectAddress(task)
        tvCommentTask.text = task.comments
        tvDateCompletionTask.text = task.dateofmaking.substringBeforeLast("T")
        tvTimeCompletionTask.text = task.recommended_time
        tvPhoneTask.text = task.phones
        tvIsMoneyTask.visibility = detectPayable(task.ispayable!!)

        tvTypeServiceTask.text = detectService(
            mapOf(
                "Интернет" to task.isInternet!!,
                "Домофон" to task.isDom!!,
                "Телевидение" to task.isTv!!
            )
        )
        if (tvTypeServiceTask.text != "") {
            tvProblem.text = detectProblem(
                task.pol11!!,
                task.pol12!!,
                task.pol13!!,
                tvTypeServiceTask.text.toString()
            )
        }

    }


    private fun detectAddress(task: TaskModel?): String {
        /**Эта ф-ия проверяет приходит ли заявка с полным адресом или адрес забит от руки
         * и возвращает разные вариации в поле адрес*/
        return if (task?.name_ru == null) {
            task!!.address!!
        } else "${task.name_ru} ${task.building_number}-${task.flat}"
    }

    private fun detectService(map: Map<String, Boolean>): String {
        /** Проверяет на какой услуге в базе стоит true и возвращает ее имя*/
        var i = ""
        map.forEach {
            if (it.value) {
                i = it.key
            }
        }
        return i
    }

    private fun detectPayable(pay: Boolean): Int {
        return if (!pay) View.INVISIBLE else View.VISIBLE
    }
}

class MainAdapter(
    var taskArray: ArrayList<TaskModel>, private val itemLongClickListener: OnItemLongClickListener,
    private val navController: NavController,
    private val taskViewModel: TaskViewModel
) : RecyclerView.Adapter<MainViewHolder>() {

    //private var removedPosition: Int = 0
    private lateinit var removedItem: TaskModel


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.task_recycler_view_item, viewGroup, false)
        return MainViewHolder(inflater)
    }

    override fun onBindViewHolder(viewHolder: MainViewHolder, position: Int) {
        val oneTask = taskArray[position]
        viewHolder.bind(oneTask, itemLongClickListener)
    }

    override fun getItemCount() = taskArray.size

    fun removeItem(position: Int) {  //, viewHolder: RecyclerView.ViewHolder было аргументом
        println("В адаптере: $taskArray")

        removedItem =taskArray[position]
        //removedPosition = position
        taskViewModel.singleTask.value = taskArray[position] // Помещаем данные выбраной заявки в VM

        taskArray.removeAt(position)  // Удалить элемент
        notifyItemRemoved(position)  // Уведомить всех слушателей что элемент удален
        navController.navigate(R.id.action_taskFragment_to_closeTaskFragment)
    }
}

interface OnItemLongClickListener {
    fun onItemLongClicked(task: TaskModel, address: String)
}
