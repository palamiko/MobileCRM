package android.bignerdranch.photounit.model.networkModel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultCableTest (
    @SerialName("result")
    val state: Boolean?,
    @SerialName("pair1")
    val pair1: String?,
    @SerialName("pair2")
    val pair2: String?,
    @SerialName("pair3")
    val pair3: String?,
    @SerialName("pair4")
    val pair4: String?,
    @SerialName("pair1_length")
    val length1: String?,
    @SerialName("pair2_length")
    val length2: String?,
    @SerialName("pair3_length")
    val length3: String?,
    @SerialName("pair4_length")
    val length4: String?,
)
