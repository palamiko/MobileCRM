package android.bignerdranch.mobilecrm.model.modelsDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Street (

    @SerialName("uid")
    val id: Int,

    @SerialName("name_ru")
    val name: String
)
