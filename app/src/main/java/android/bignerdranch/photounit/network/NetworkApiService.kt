package android.bignerdranch.photounit.network

import android.bignerdranch.photounit.model.MaterialUsed
import android.bignerdranch.photounit.model.TokenFirebase
import android.bignerdranch.photounit.model.modelsDB.Home
import android.bignerdranch.photounit.model.modelsDB.Street
import android.bignerdranch.photounit.model.modelsDB.TaskModel
import android.bignerdranch.photounit.utilits.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.http.GET
import retrofit2.http.Query


interface NetworkApiService {

    /**Получает список улиц в микрорайоне*/
    @GET(GET_STREET)
    suspend fun getStreetList(@Query(DISTRICT_ID) district_id: Int): List<Street>

    /**Получает список домов в улице*/
    @GET(GET_HOME)
    suspend fun getHomeList(
        @Query(DISTRICT_ID) district_id: Int,
        @Query(STREET_ID) street_id: Int
    ): List<Home>

    /**Авторизация на сервере CRM*/
    @GET(GET_AUTHORIZATION)
    suspend fun getAuthorization(
        @Query(LOGIN) login: String,
        @Query(PASSWORD) password: String
    ): TokenFirebase

    /**Получить список заявок на данного мастера*/
    @GET(GET_TASK_FOR_MASTER)
    suspend fun getTaskMaster(@Query(MASTER_ID) id_master: String,
                              @Query (STATE_TASK) state_task: Char = TASK_APPOINTED,
                              @Query (DATE_MAKING) date_making: String = getDateTime(TODAY)): ArrayList<TaskModel>

    /**Получить список материалов*/
    @GET(GET_MATERIAL_LIST)
    suspend fun getMaterial(): ArrayList<MaterialUsed>


    companion object {
        /**Параметры запросов*/

        const val DISTRICT_ID = "district_id"
        const val STREET_ID = "street_id"
        const val LOGIN = "login_auth"
        const val PASSWORD = "password_auth"
        const val MASTER_ID = "master_id"
        const val STATE_TASK = "state_task"
        const val DATE_MAKING = "date_making"


        private fun getDateTime(day: Int): String {
            val today: LocalDate = LocalDate.now()
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-dd 00:00:00")
            var dayReturn = ""

            when (day) {
                -1 -> {
                    val yesterday: LocalDate = today.minusDays(1)
                    dayReturn = yesterday.format(formatter)
                }
                0 -> {
                    dayReturn = today.format(formatter)
                }
                1 -> {
                    val tomorrow: LocalDate = today.plusDays(1)
                    dayReturn = tomorrow.format(formatter)
                }
            }
            return dayReturn
        }
    }
}