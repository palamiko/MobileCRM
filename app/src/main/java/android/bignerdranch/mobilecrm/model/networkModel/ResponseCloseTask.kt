package android.bignerdranch.mobilecrm.model.networkModel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ResponseCloseTask (
    @SerialName("result")
    val result: Boolean = false,
    @SerialName("id_task")
    val id_task: String?,
    @SerialName("state_task")
    val state_task: String?
)
