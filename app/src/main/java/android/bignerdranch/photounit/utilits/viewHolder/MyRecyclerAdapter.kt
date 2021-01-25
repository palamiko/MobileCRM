package android.bignerdranch.photounit.utilits.viewHolder

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.model.modelsDB.TaskList
import android.bignerdranch.photounit.viewModels.TaskViewModel
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class MainAdapter(private val dataSet: ArrayList<TaskList>, val navController: NavController,
                  private val taskViewModel: TaskViewModel) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    private var removedPosition: Int = 0
    private lateinit var removedItem: TaskList

    class MainViewHolder(view: View, ): RecyclerView.ViewHolder(view) {
        private val tvAddressTask: TextView = view.findViewById(R.id.tv_address_task)
        private val tvCommentTask: TextView = view.findViewById(R.id.tv_comment)
        private val tvDateCompletionTask: TextView = view.findViewById(R.id.date_of_completion)
        private val tvTimeCompletionTask: TextView = view.findViewById(R.id.tv_time_completion)
        private val tvPhoneTask: TextView = view.findViewById(R.id.tv_phone)
        private val tvIsMoneyTask: ImageView = view.findViewById(R.id.iv_is_money)
        private val tvTypeServiceTask: TextView = view.findViewById(R.id.tv_type_service)

        fun bind(task: TaskList) {
            tvAddressTask.text = detectAddress(task)
            tvCommentTask.text = task.comments
            tvDateCompletionTask.text = task.dateofmaking.substringBeforeLast("T")
            tvTimeCompletionTask.text = task.comments2
            tvPhoneTask.text = task.phones
            tvIsMoneyTask.visibility = detectPayable(task.ispayable)
            tvTypeServiceTask.text = detectService(
                mapOf (
                    "Интернет" to task.isinternet,
                    "Домофон" to task.isdom,
                    "Телевидение" to task.istv
                )
            )
        }

        private fun detectAddress(task: TaskList): String {
            /**Эта ф-ия проверяет приходит ли заявка с полным адресом или адрес забит от руки
             * и возвращает разные вариации в поле адрес*/
            return if (task.name_ru == null) {
                task.address
            } else "${task.name_ru} ${task.building_number}-${task.flat}"
        }

        private fun detectService(map: Map<String, String>): String {
            /** Проверяет на какой услуге в базе стоит true и возвращает ее имя*/
            var i = ""
            map.forEach {
                if (it.value == "true") {
                    i = it.key
                }
            }
            return i
        }

        private fun detectPayable(pay: String): Int {
            return if (pay == "false") View.INVISIBLE
            else View.VISIBLE
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

    fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
        removedItem = dataSet[position]
        removedPosition = position

        taskViewModel.singleTask.value = dataSet[position] // Помещаем данные выбраной заявки в VM
        navController.navigate(R.id.action_taskFragment_to_closeTaskFragment)

        dataSet.removeAt(position)  // Удалить элемент
        notifyItemRemoved(position)  // Уведомить всех слушателей что элемент удален

        Snackbar.make(viewHolder.itemView, "$removedItem removed", Snackbar.LENGTH_LONG).setAction("UNDO") {
            dataSet.add(removedPosition, removedItem)
            notifyItemInserted(removedPosition)
        }.show()
    }

    override fun getItemCount() = dataSet.size
}



class TouchHelper (var viewAdapter: RecyclerView.Adapter<*>,
                  var deleteIcon: Drawable,
                  private val colorDrawableBackground: ColorDrawable
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
        (viewAdapter as MainAdapter).removeItem(viewHolder.adapterPosition, viewHolder)
    }

    override fun onChildDraw(
        c: android.graphics.Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

        if (dX > 0) {
            colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
        } else {
            colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
            deleteIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
            deleteIcon.level = 0
        }

        colorDrawableBackground.draw(c)

        c.save()

        if (dX > 0)
            c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
        else
            c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

        deleteIcon.draw(c)

        c.restore()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}