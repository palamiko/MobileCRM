package android.bignerdranch.photounit.utilits

import android.bignerdranch.photounit.BuildConfig

const val VERSION_APP = BuildConfig.VERSION_NAME
const val SERVER_PORT = "20136"
const val SERVER_ADDRESS = "http://87.76.32.43:$SERVER_PORT"
const val GET_STREET = "get-street"
const val GET_HOME = "get-home"
const val GET_TASK_FOR_MASTER = "get_task_for_master_period"
const val POST_LOAD_PHOTO = "uploadImg"
const val AUTH_USER = "Mobile-CRM_User"
const val AUTH_PASS = "vtufnhjY-124"
const val AUTH_IN_APP = "register"
const val CLOSE_TASK = "close_task"
const val GET_MATERIAL_LIST = "get_list_material"


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
