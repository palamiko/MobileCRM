package android.bignerdranch.mobilecrm.model.otherModel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class User (
    var id: Long = 0,
    var login: String = "",
    var pass: String = "",
    var name: String = "",
    var status: String = "",
    var token: String = ""
)

@Serializable
data class TokenFirebase(
    @SerialName("token")
    val value: String
)

@Serializable
data class AuthData(
    @SerialName("login_auth")
    val login: String,
    @SerialName("password_auth")
    val pass: String
)
