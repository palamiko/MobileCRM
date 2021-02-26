package android.bignerdranch.photounit.model.modelsDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientCardBilling (
    @SerialName("num_contract")
    val numberContract: String?,
    @SerialName("tariff")
    val tariff: String?,
    @SerialName("status")
    val stateAccount:String?,
    @SerialName("balance")
    val balance: String?,
    @SerialName("ip")
    val ip: String?,
    @SerialName("date_start")
    val dateStart: String?,
    @SerialName("mac")
    val mac: String?
)
