package android.bignerdranch.mobilecrm.model.modelsDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientCard (
    @SerialName("id_client")
    val id_client: String?,
    @SerialName("id_entrance")
    val id_entrance: Int?,
    @SerialName("agreement_number")
    val agreement_number: String?,
    @SerialName("is_internet")
    val is_internet: Boolean?,
    @SerialName("is_domofon")
    val is_domofon: Boolean?,
    @SerialName("is_tv")
    val is_tv: Boolean?,
    @SerialName("id_port")
    val id_port: Int?,
    @SerialName("number_port")
    val number_port: Int?,
    @SerialName("id_switch")
    val id_switch: Int?,
    @SerialName("ip_switch")
    var ip_switch: String?,
    @SerialName("type_switch")
    val type_switch: String?,
    @SerialName("model_switch")
    val model_switch: String?,
    @SerialName("id_node")
    val id_node: Int?,
    @SerialName("id_building")
    val id_building: Int?,
    @SerialName("entrance_node")
    val entrance_node: Int?,
    @SerialName("flor_node")
    val flor_node: Int?,
    @SerialName("building_number")
    val building_number: String?,
    @SerialName("name_ru")
    val street_name: String?
)

@Serializable
data class ResponseOfToken(
    @SerialName("successful")
    var successful: Boolean = false
)
