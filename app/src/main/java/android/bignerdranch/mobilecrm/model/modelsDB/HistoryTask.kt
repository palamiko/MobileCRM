package android.bignerdranch.mobilecrm.model.modelsDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class HistoryTask(
    @SerialName("id_task")
    var id_task: String = "",
    @SerialName("address")
    var address: String = "",
    @SerialName("id_u_r")
    var id_dispatcher: String = "",
    @SerialName("created")
    var dateCreate: String = "",
    @SerialName("id_u")
    var id_master: String = "",
    @SerialName("ended")
    var dateEnded: String = "",
    @SerialName("comments")
    var comments: String = "",
    @SerialName("feedback")
    var feedback: String = "",
    @SerialName("name_master")
    var name_master: String = "",
    @SerialName("zayav_state")
    var state_task: String = ""
)

@Serializable
data class ActionTask (
    @SerialName("id")
    var id: String = "",
    @SerialName("act_number")
    var actNumber: String? = "",
    @SerialName("comments")
    var comments: String = "",
    @SerialName("date")
    var date: String = "",
    @SerialName("finish")
    var isFinished: Boolean?,
    @SerialName("id_u")
    var id_master: String = "",
    @SerialName("id_z")
    var id_z: String = "",
    @SerialName("sum")
    var summ: String? = "",
    @SerialName("name_master")
    var name_master: String = ""
)
