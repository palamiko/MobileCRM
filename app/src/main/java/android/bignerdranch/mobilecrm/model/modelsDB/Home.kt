package android.bignerdranch.mobilecrm.model.modelsDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Home(

    @SerialName("uid")
    val id: Int,

    @SerialName("building_number")
    val number: String
)
