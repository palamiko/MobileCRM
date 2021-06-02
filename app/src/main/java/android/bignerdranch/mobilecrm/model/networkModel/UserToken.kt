package android.bignerdranch.mobilecrm.model.networkModel

import android.bignerdranch.mobilecrm.network.NetworkApiService.Companion.MASTER_ID
import android.bignerdranch.mobilecrm.network.NetworkApiService.Companion.MASTER_LOGIN
import android.bignerdranch.mobilecrm.network.NetworkApiService.Companion.TOKEN_MASTER
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserToken (
    @SerialName(MASTER_ID)
    val userId: String,
    @SerialName(MASTER_LOGIN)
    val userLogin: String,
    @SerialName(TOKEN_MASTER)
    val userToken: String
)

@Serializable
data class ResponseOfToken(
    @SerialName("successful")
    var successful: Boolean = false
)
