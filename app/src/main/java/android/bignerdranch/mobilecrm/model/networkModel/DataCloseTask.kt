package android.bignerdranch.mobilecrm.model.networkModel

import kotlinx.serialization.Serializable

@Serializable
data class DataCloseTask(
    var id_task: String,
    var state_task: Char,
    var id_user: String,
    var comment: String? = null,
    var finish: String,
    var summ: String? = null,
    var material: ArrayList<MaterialUsed>
)
