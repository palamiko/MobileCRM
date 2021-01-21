package android.bignerdranch.photounit.model.modelsDB

data class TaskList(
    var id: String = "",
    var id_c: String = "",
    var phones: String = "",
    var state: String = "",
    var pol1: String = "",
    var pol2: String = "",
    var pol3: String = "",
    var pol4: String = "",
    var pol5: String = "",
    var pol6: String = "",
    var pol7: String = "",
    var created: String = "",
    var modified: String = "",
    var ended: String = "",
    var comments: String = "",
    var id_u: String = "",  // ИД мастера
    var ispayable: String = "",
    var id_u_r: String = "",  // ИД менеджера
    var comments2: String = "",  // Рекомендуемое время выполнения
    var id_parent: String = "",
    var feedback: String = "",
    var dateofmaking: String = "",
    var fio: String = "",
    var address: String = "",
    var pol11: String = "",
    var pol12: String = "",
    var pol13: String = "",
    var isinternet: String = "",
    var istv: String = "",
    var isdom: String = "",
    var flat: String = "",  // Номер квартиры заявки
    var building_number: String = "",  // Номер дома заявки
    var name_ru: String = ""  // Название улицы заявки
)
