package android.bignerdranch.mobilecrm.network

import android.bignerdranch.mobilecrm.model.modelsDB.*
import android.bignerdranch.mobilecrm.model.networkModel.*
import android.bignerdranch.mobilecrm.model.otherModel.TokenFirebase
import android.bignerdranch.mobilecrm.utilits.helpers.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface NetworkApiService {

    /** Получает список улиц в микрорайоне */
    @GET(GET_STREET)
    suspend fun getStreetList(@Query(DISTRICT_ID) district_id: Int): List<Street>

    /** Получает список домов в улице */
    @GET(GET_HOME)
    suspend fun getHomeList(
        @Query(DISTRICT_ID) district_id: Int,
        @Query(STREET_ID) street_id: Int
    ): List<Home>

    /** Получает список подъездов в доме */
    @GET(GET_ENTRANCES)
    suspend fun getEntrancesList(@Query(BUILDING_ID) building_id: Int): List<Entrance>

    /** Получает список абонентов в подъезде */
    @GET(GET_CLIENTS_IN_ENTRANCE)
    suspend fun getClientsInEntrance(@Query(ENTRANCE_ID) entrance_id: Int): List<ClientsEntrance>

    /** Авторизация на сервере CRM */
    @GET(GET_AUTHORIZATION)
    suspend fun getAuthorization(
        @Query(LOGIN) login: String,
        @Query(PASSWORD) password: String
    ): TokenFirebase

    /** Получить список заявок на данного мастера */
    @GET(GET_TASK_FOR_MASTER)
    suspend fun getTaskMaster(@Query(MASTER_ID) id_master: String,
                              @Query (STATE_TASK) state_task: Char = TASK_APPOINTED,
                              @Query (DATE_MAKING) date_making: String = getDateTime(TODAY)): ArrayList<TaskModel>

    /** Получить список материалов */
    @GET(GET_MATERIAL_LIST)
    suspend fun getMaterial(): ArrayList<MaterialUsed>

    /** Закрыть заявку */
    @POST(CLOSE_TASK)
    suspend fun postCloseTask(@Body dataCloseTask: DataCloseTask): ResponseCloseTask

    /** Получить карточку абонента */
    @GET(GET_CLIENT_CARD)
    suspend fun getClientCard(@Query(ID_CLIENT) id_client: String): ClientCard?

    /** Получить карточку абонента, данные с биллинга*/
    @GET(GET_CLIENT_CARD_BILLING)
    suspend fun getClientCardBilling(@Query(NUMBER_CONTRACT) number_contract: String): ClientCardBilling

    /**Выполнить кабель тест*/
    @GET(GET_CABLE_TEST)
    suspend fun getCableTest(@Query(IP_SWITCH) ipSwitch: String,
                             @Query(NUMBER_PORT) port: String,
                             @Query(SWITCH_TYPE) switchType: String): ResultCableTest

    /**Получить статус линка на порту*/
    @GET(GET_LINK_STATUS)
    suspend fun getLinkStatus(@Query(IP_SWITCH) ipSwitch: String,
                              @Query(NUMBER_PORT) port: String,
                              @Query(SWITCH_TYPE) switchType: String): ResultLinkStatus

    @GET(GET_ERRORS_COUNT)
    suspend fun getErrors(@Query(IP_SWITCH) ipSwitch: String,
                          @Query(NUMBER_PORT) port: String,
                          @Query(SWITCH_TYPE) switchType: String): ResultErrorTest

    @GET(GET_SPEED_PORT)
    suspend fun getSpeedPort(@Query(IP_SWITCH) ipSwitch: String,
                             @Query(NUMBER_PORT) port: String,
                             @Query(SWITCH_TYPE) switchType: String): ResultSpeedPort

    /** Получить историю заявок абонента */
    @GET(GET_HISTORY_TASK_LIST)
    suspend fun getHistoryListTask(@Query(ID_CLIENT) id_client: String): List<HistoryTask>

    /** Все действия для данной заявки */
    @GET(GET_ACTION_FOR_TASK)
    suspend fun getActionForTask(@Query(ID_TASK) id_task: String): List<ActionTask>

    /** Отправить токен пользователя, его ID и логин */
    @GET(GET_SEND_TOKEN)
    suspend fun sendTokenFirebase(@Query(MASTER_ID) id_master: String,
                                  @Query(MASTER_LOGIN) login_master: String,
                                  @Query(TOKEN_MASTER) token: String): ResponseOfToken

    companion object {
        /**Параметры запросов*/

        private const val DISTRICT_ID = "district_id"
        private const val STREET_ID = "street_id"
        private const val BUILDING_ID = "building_id"
        private const val ENTRANCE_ID = "entrance_id"
        private const val LOGIN = "login_auth"
        private const val PASSWORD = "password_auth"
        private const val MASTER_ID = "master_id"
        private const val MASTER_LOGIN = "master_login"
        private const val TOKEN_MASTER = "token_master"
        private const val STATE_TASK = "state_task"
        private const val DATE_MAKING = "date_making"
        private const val ID_CLIENT = "id_client"
        private const val NUMBER_CONTRACT = "number_contract"
        private const val IP_SWITCH = "ip_commutator"
        private const val NUMBER_PORT = "number_port"
        private const val SWITCH_TYPE = "switch_type"
        private const val ID_TASK = "id_task"


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