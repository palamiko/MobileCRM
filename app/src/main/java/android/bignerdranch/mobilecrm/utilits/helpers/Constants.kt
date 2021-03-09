package android.bignerdranch.mobilecrm.utilits.helpers

import android.bignerdranch.mobilecrm.BuildConfig

// Основная информация
const val VERSION_APP =   "ver ${BuildConfig.VERSION_NAME}"
const val SERVER_PORT = "20136"
const val SERVER_ADDRESS = "http://87.76.32.43:$SERVER_PORT"
const val BASE_URL = "$SERVER_ADDRESS/"


// Базовая http авторизация для запросов
const val AUTH_USER = "Mobile-CRM_User"
const val AUTH_PASS = "vtufnhjY-124"


// Названия запростов
const val GET_STREET = "get-street"
const val GET_HOME = "get-home"
const val GET_TASK_FOR_MASTER = "get_task_for_master_period"
const val POST_LOAD_PHOTO = "uploadImg"
const val GET_AUTHORIZATION = "authorization"
const val CLOSE_TASK = "new_close_task"
const val GET_MATERIAL_LIST = "get_list_material"
const val GET_CLIENT_CARD = "get_client_card"
const val GET_CLIENT_CARD_BILLING = "get_client_card_billing"
const val GET_CABLE_TEST = "get_cable_test"
const val GET_LINK_STATUS = "get_link_status"
const val GET_ERRORS_COUNT = "get_errors"
const val GET_SPEED_PORT = "get_speed_port"


// Состояния заявки  //
const val TASK_RECEIVED = '1'  // Принята
const val TASK_APPOINTED = '2'  // Назначена
const val TASK_COMPLETED = '3'  // Выполнена
const val TASK_CLOSED = '4'  // Закрыта

const val FINISH_OK = "true"
const val FINISH_NO = "false"


// Ноды Firebase //
const val USERS = "users"


// Выбор дня отображения заявок //
const val YESTERDAY = -1
const val TODAY = 0
const val TOMORROW = 1


// SharedPreference
const val SHARED_PREF_NAME = "USER_DATA_FIREBASE"  // Название файла настроек
const val KEY_USER_DATA = "USER_DATA"  // Имя ключа данных пользователя в файле
