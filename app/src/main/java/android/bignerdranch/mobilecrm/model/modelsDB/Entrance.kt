package android.bignerdranch.mobilecrm.model.modelsDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Entrance(

    @SerialName("uid")
    val id: Int,

    @SerialName("id_building")
    val id_building: Int,

    @SerialName("id_node")
    val id_node: Int,

    @SerialName("number")
    val number: String,

    @SerialName("flat_area")
    val flatRange: String
)

@Serializable
data class ClientsEntrance(

    @SerialName("uid")
    val id_client: Int,

    @SerialName("floor")
    val numberFloor: Int,

    @SerialName("flat")
    val numberFlat: Int,

    @SerialName("connected")
    val status: Int,

    @SerialName("expired")
    val expired: String?,

    @SerialName("isinternet")
    val internet: Boolean,

    @SerialName("istv")
    val tv: Boolean,

    @SerialName("isdom")
    val domofon: Boolean
)
