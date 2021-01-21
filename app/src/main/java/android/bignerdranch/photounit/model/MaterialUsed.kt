package android.bignerdranch.photounit.model

import kotlinx.serialization.Serializable


@Serializable
data class MaterialUsed(
    var id: String ,
    var ed_izm: String ,
    var name: String ,
    var count: String? = null
)