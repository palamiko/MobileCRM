package android.bignerdranch.photounit.model.otherModel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class User (
    var id: Long = 0,
    var login: String = "",
    var pass: String = "",
    var name: String = "",
    var status: String = ""
)

@Serializable
data class TokenFirebase(
    @SerialName("token")
    val value: String
)
