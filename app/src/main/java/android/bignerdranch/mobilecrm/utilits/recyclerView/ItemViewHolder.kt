package android.bignerdranch.mobilecrm.utilits.recyclerView

import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.model.networkModel.MaterialUsed
import android.view.ViewGroup
import android.widget.TextView
import smartadapter.viewholder.SmartViewHolder


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