package android.bignerdranch.mobilecrm.utilits.recyclerView

import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.model.modelsDB.TaskModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class ClientCardRecyclerAdapter (var clientDataArray: ArrayList<TaskModel>,
                                 private val itemLongClickListener: OnItemLongClickListener
) : RecyclerView.Adapter<ClientCardViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ClientCardViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.task_recycler_view_item, viewGroup, false)
        return ClientCardViewHolder(inflater)
    }

    override fun onBindViewHolder(viewHolder: ClientCardViewHolder, position: Int) {
        val oneTask = clientDataArray[position]
        viewHolder.bind(oneTask, itemLongClickListener)
    }

    override fun getItemCount() = clientDataArray.size
}

class ClientCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(task: TaskModel, longClickListener: OnItemLongClickListener) {

        itemView.setOnClickListener {
            longClickListener.onItemLongClicked(task, "")
        }
    }
}

