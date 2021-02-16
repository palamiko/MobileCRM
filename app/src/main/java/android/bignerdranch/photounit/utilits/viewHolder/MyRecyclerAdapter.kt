package android.bignerdranch.photounit.utilits.viewHolder

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.model.modelsDB.TaskModel
import android.bignerdranch.photounit.utilits.detectProblem
import android.bignerdranch.photounit.viewModels.TaskViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class MainAdapter(private val dataSet: ArrayList<TaskModel>, private val navController: NavController,
                  private val taskViewModel: TaskViewModel) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    //private var removedPosition: Int = 0
    private lateinit var removedItem: TaskModel

    class MainViewHolder(view: View, ): RecyclerView.ViewHolder(view) {
        private val tvAddressTask: TextView = view.findViewById(R.id.tv_address_task)
        private val tvCommentTask: TextView = view.findViewById(R.id.tv_comment)
        private val tvDateCompletionTask: TextView = view.findViewById(R.id.date_of_completion)
        private val tvTimeCompletionTask: TextView = view.findViewById(R.id.tv_time_completion)
        private val tvPhoneTask: TextView = view.findViewById(R.id.tv_phone)
        private val tvIsMoneyTask: ImageView = view.findViewById(R.id.iv_is_money)
        private val tvTypeServiceTask: TextView = view.findViewById(R.id.tv_type_service)
        private val tvProblem: TextView = view.findViewById(R.id.tv_problem)

        fun bind(task: TaskModel) {

            tvAddressTask.text = detectAddress(task)
            tvCommentTask.text = task.comments
            tvDateCompletionTask.text = task.dateofmaking.substringBeforeLast("T")
            tvTimeCompletionTask.text = task.recommended_time
            tvPhoneTask.text = task.phones
            tvIsMoneyTask.visibility = detectPayable(task.ispayable!!)

            tvTypeServiceTask.text = detectService(
                mapOf (
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

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.task_recycler_view_item, viewGroup, false)
        return MainViewHolder(inflater)
    }

    override fun onBindViewHolder(viewHolder: MainViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])
    }

    fun removeAllItem() {
        val count = dataSet.size
        dataSet.clear()
        notifyItemRangeRemoved(0, count)
    }

    fun removeItem(position: Int) {  //, viewHolder: RecyclerView.ViewHolder было аргументом
        println("В адаптере: $dataSet")

        removedItem =dataSet[position]
        //removedPosition = position
        taskViewModel.singleTask.value = dataSet[position] // Помещаем данные выбраной заявки в VM

        dataSet.removeAt(position)  // Удалить элемент
        notifyItemRemoved(position)  // Уведомить всех слушателей что элемент удален
        navController.navigate(R.id.action_taskFragment_to_closeTaskFragment)

/**
        Snackbar.make(viewHolder.itemView, "$removedItem removed", Snackbar.LENGTH_LONG).setAction("UNDO") {
            dataSet.add(removedPosition, removedItem)
            notifyItemInserted(removedPosition)
        }.show()*/
    }

    override fun getItemCount() = dataSet.size
}
