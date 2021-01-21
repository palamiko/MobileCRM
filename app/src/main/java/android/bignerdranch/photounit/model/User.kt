package android.bignerdranch.photounit.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var id: Long = 0,
    var login: String = "",
    var pass: String = ""
)
