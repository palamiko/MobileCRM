package android.bignerdranch.photounit.utilits.viewHolder

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.model.MaterialUsed
import android.bignerdranch.photounit.model.modelsDB.TaskList
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import smartadapter.viewevent.model.ViewEvent
import smartadapter.viewevent.viewholder.OnItemSelectedEventListener
import smartadapter.viewholder.SmartViewHolder

open class ItemViewHolder (parentView: ViewGroup) :
    SmartViewHolder<TaskList>(parentView, R.layout.task_recycler_view_item) {

    private val tvAddressTask: TextView = itemView.findViewById(R.id.tv_address_task)
    private val tvCommentTask: TextView = itemView.findViewById(R.id.tv_comment)
    private val tvDateCompletionTask: TextView = itemView.findViewById(R.id.date_of_completion)
    private val tvTimeCompletionTask: TextView = itemView.findViewById(R.id.tv_time_completion)
    private val tvPhoneTask: TextView = itemView.findViewById(R.id.tv_phone)
    private val tvIsMoneyTask: TextView = itemView.findViewById(R.id.tv_is_money)
    private val tvTypeServiceTask: TextView = itemView.findViewById(R.id.tv_type_service)

    override fun bind(item: TaskList) {

        fun detectAddress(): String {
            /**Эта ф-ия проверяет приходит ли заявка с полным адресом или адрес забит от руки
             * и возвращает разные вариации в поле адрес*/
            return if (item.name_ru == null) {
                item.address
            } else "${item.name_ru} ${item.building_number}-${item.flat}"
        }

        tvAddressTask.text = detectAddress()
        tvCommentTask.text = item.comments
        tvDateCompletionTask.text = item.dateofmaking.substringBeforeLast("T")
        tvTimeCompletionTask.text = item.comments2
        tvPhoneTask.text = item.phones
        tvIsMoneyTask.text = detectPayable(item.ispayable)
        tvTypeServiceTask.text = detectService(
            mapOf (
                "Интернет" to item.isinternet,
                "Домофон" to item.isdom,
                "Телевидение" to item.istv
            )
        )
        tvTypeServiceTask.paintFlags = tvTypeServiceTask.paintFlags or Paint.UNDERLINE_TEXT_FLAG
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

    private fun detectPayable(pay: String): String {
        val i: String
        if (pay == "true") {
            i = "Платная"
            tvIsMoneyTask.setTextColor(Color.parseColor("#ffcc0000"))
        } else {
            i = "Бесплатная"
            tvIsMoneyTask.setTextColor(Color.parseColor("#ff99cc00"))
        }
        return i
    }
}

class SimpleExpandableItemViewHolder(parentView: ViewGroup) :
    SmartViewHolder<TaskList>(parentView, R.layout.expandable_item),
    OnItemSelectedEventListener {

    private val tvAddressTask: TextView = itemView.findViewById(R.id.tv_address_task_close)
    private val tvCommentTask: TextView = itemView.findViewById(R.id.tv_comment_close)
    private val tvDateCompletionTask: TextView = itemView.findViewById(R.id.date_of_completion_close)
    private val tvTimeCompletionTask: TextView = itemView.findViewById(R.id.tv_time_completion_close)
    private val tvPhoneTask: TextView = itemView.findViewById(R.id.tv_phone_close)
    private val tvIsMoneyTask: TextView = itemView.findViewById(R.id.tv_is_money_close)
    private val tvTypeServiceTask: TextView = itemView.findViewById(R.id.tv_type_service_close)
    private val subItem: ConstraintLayout = itemView.findViewById(R.id.subItemContainer)


    override fun bind(item: TaskList) {

        fun detectAddress(): String {
            /**Эта ф-ия проверяет приходит ли заявка с полным адресом или адрес забит от руки
             * и возвращает разные вариации в поле адрес*/
            return if (item.name_ru == null) {
                item.address
            } else "${item.name_ru} ${item.building_number}-${item.flat}"
        }

        tvAddressTask.text = detectAddress()
        tvCommentTask.text = item.comments
        tvDateCompletionTask.text = item.dateofmaking.substringBeforeLast("T")
        tvTimeCompletionTask.text = item.comments2
        tvPhoneTask.text = item.phones
        tvIsMoneyTask.text = detectPayable(item.ispayable)
        tvTypeServiceTask.text = detectService(
            mapOf (
                "Интернет" to item.isinternet,
                "Домофон" to item.isdom,
                "Телевидение" to item.istv
            )
        )
        //tvTypeServiceTask.paintFlags = tvTypeServiceTask.paintFlags or Paint.UNDERLINE_TEXT_FLAG
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

    private fun detectPayable(pay: String): String {
        val i: String
        if (pay == "true") {
            i = "Платная"
            tvIsMoneyTask.setTextColor(Color.parseColor("#ffcc0000"))
        } else {
            i = "Бесплатная"
            tvIsMoneyTask.setTextColor(Color.parseColor("#ff99cc00"))
        }
        return i
    }


    override fun onItemSelect(event: ViewEvent.OnItemSelected) {
        when (event.isSelected) {

            true -> {
                tvIsMoneyTask.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less_black_24dp, 0)
                subItem.visibility = View.VISIBLE
            }
            false -> {
                tvIsMoneyTask.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_black_24dp, 0)
                subItem.visibility = View.GONE
            }
        }
    }
}

open class ItemViewHolderLite (parentView: ViewGroup) :
    SmartViewHolder<MaterialUsed>(parentView, R.layout.used_material_item) {

    private var tvNameMaterial: TextView = itemView.findViewById(R.id.tv_name_used_material_rcv)
    private val tvCountMaterial: TextView = itemView.findViewById(R.id.tv_count_used_material_rcv)


    override fun bind(item: MaterialUsed) {
        println(item)
        tvNameMaterial.text = item.name
        tvCountMaterial.text = item.count
    }

}

open class ItemViewHolderExtraLite (parentView: ViewGroup) :
    SmartViewHolder<MaterialUsed>(parentView, R.layout.material_list_item) {

    private var tvNameMaterial: TextView = itemView.findViewById(R.id.name_material)

    override fun bind(item: MaterialUsed) {
        tvNameMaterial.text = item.name
    }
}