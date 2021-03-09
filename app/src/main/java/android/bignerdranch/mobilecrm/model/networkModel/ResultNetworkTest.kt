package android.bignerdranch.mobilecrm.model.networkModel

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

@Serializable
data class ResultLinkStatus(
    /** Содержит результат линк теста 0-ERROR, 1-UP, 2-DOWN*/
    @SerialName("result")
    val state: String?
)

@Serializable
data class ResultErrorTest(
    /**Содержит результат теста на ошибки на порту*/
    @SerialName("crc")
    val CRCError: String?,
    @SerialName("drop")
    val dropError: String?
)

@Serializable
data class ResultSpeedPort(
    /**Содержит скорость на которой поднят порт*/
    @SerialName("speed")
    val speedPort: String?
)
