package android.bignerdranch.mobilecrm.model.modelsDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskModel (
    @SerialName("id")
    var id_task: String = "",
    @SerialName("id_c")
    var id_client: Int? = 1,
    @SerialName("phones")
    var phones: String? = "",
    @SerialName("state")
    var state: String? = "",
    @SerialName("pol1")
    var pol1: String? = "",
    @SerialName("pol2")
    var pol2: String? = "",
    @SerialName("pol3")
    var pol3: String? = "",
    @SerialName("pol4")
    var pol4: String? = "",
    @SerialName("pol5")
    var pol5: String? = "",
    @SerialName("pol6")
    var pol6: String? = "",
    @SerialName("pol7")
    var pol7: String? = "",
    @SerialName("created")
    var created: String? = "",
    @SerialName("modified")
    var modified: String? = "",
    @SerialName("ended")
    var ended: String? = "",
    @SerialName("comments")
    var comments: String? = "",
    @SerialName("id_u")
    var id_master: String = "",  // ИД мастера
    @SerialName("ispayable")
    var ispayable: Boolean? = false,
    @SerialName("id_u_r")
    var id_dispatcher: String = "", // ИД менеджера
    @SerialName("comments2")
    var recommended_time: String? = "",  // Рекомендуемое время выполнения
    @SerialName("id_parent")
    var id_parent: String? = "",
    @SerialName("feedback")
    var feedback: String? = "",
    @SerialName("dateofmaking")
    var dateofmaking: String = "",
    @SerialName("fio")
    var fio: String? = "",
    @SerialName("address")
    var address: String? = "",
    @SerialName("pol11")
    var pol11: String? = "",
    @SerialName("pol12")
    var pol12: String? = "",
    @SerialName("pol13")
    var pol13: String? = "",
    @SerialName("isinternet")
    var isInternet: Boolean? = false,
    @SerialName("istv")
    var isTv: Boolean? = false,
    @SerialName("isdom")
    var isDom: Boolean? = false,
    @SerialName("flat")
    var flat: String? = "",  // Номер квартиры заявки
    @SerialName("building_number")
    var building_number: String? = "",  // Номер дома заявки
    @SerialName("name_ru")
    var name_ru: String? = ""  // Название улицы заявки
)
