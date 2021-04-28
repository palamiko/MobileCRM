package android.bignerdranch.mobilecrm.model.viewModels

import android.bignerdranch.mobilecrm.model.modelsDB.ResponseOfToken
import android.bignerdranch.mobilecrm.model.modelsDB.TaskModel
import android.bignerdranch.mobilecrm.model.networkModel.DataCloseTask
import android.bignerdranch.mobilecrm.model.networkModel.MaterialUsed
import android.bignerdranch.mobilecrm.model.networkModel.ResponseCloseTask
import android.bignerdranch.mobilecrm.model.otherModel.User
import android.bignerdranch.mobilecrm.network.NetworkApiService
import android.bignerdranch.mobilecrm.network.NetworkModule
import android.bignerdranch.mobilecrm.utilits.helpers.FINISH_OK
import android.bignerdranch.mobilecrm.utilits.helpers.TASK_APPOINTED
import android.bignerdranch.mobilecrm.utilits.helpers.TASK_COMPLETED
import android.bignerdranch.mobilecrm.utilits.helpers.TODAY
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


class TaskViewModel : ViewModel() {

    @ExperimentalSerializationApi
    private val networkApi: NetworkApiService = NetworkModule().networkApiService

    val arrayTask = MutableLiveData<ArrayList<TaskModel>>()  // Массив заявок с сервера
    val singleTask = MutableLiveData<TaskModel>()  // Данные одной заявки


    val selectMaterial =
        MutableLiveData<ArrayList<MaterialUsed>>()  // Здесь содержатся выбранные для списания материалы
    val arrayMaterialList =
        MutableLiveData<ArrayList<MaterialUsed>>()  // Сюда загружается список доступных материалов с сервера

    val ldUserData = MutableLiveData<User>()  // Данные пользователя из Firebase
    val resultSendToken = MutableLiveData<ResponseOfToken>()


    // Состояния View
    val selectorDay = MutableLiveData<String>()
    val selectorState = MutableLiveData<Char>()

    val radioButtonDay = MutableLiveData<Int>()
    val radioButtonState = MutableLiveData<Int>()

    // Текс в TextEdit
    var savedComment: String? = ""
    var savedSumm: String? = ""

    init {
        selectMaterial.value = arrayListOf()  // Инициализируем масив
    }

    @ExperimentalSerializationApi
    fun sendTokenToServ(
        /** Отправляет токен мастера на сервер */
        id_master: String,
        login_master: String,
        token: String
    ) {
        viewModelScope.launch {
            resultSendToken.postValue(
                networkApi.sendTokenFirebase(id_master, login_master, token)
            )
        }
    }

    @ExperimentalSerializationApi
    suspend fun getTask(
        id_master: String, state_task: Char = TASK_APPOINTED,
        date_making: String
    ): ArrayList<TaskModel> {
        return networkApi.getTaskMaster(id_master, state_task, date_making)
    }

    @ExperimentalSerializationApi
    suspend fun updateTask() {
        /**Обновление списка заявок*/
        val responseTask = getTask(getUserId(), getSelectorState(), getSelectorDay())
        if (arrayTask.value != responseTask) arrayTask.postValue(responseTask)
    }

    @ExperimentalSerializationApi
    suspend fun getMaterial() {
        /**Запрос списка материалов*/
        val result = networkApi.getMaterial()
        if (arrayMaterialList.value != result) arrayMaterialList.postValue(result)
    }

    @ExperimentalSerializationApi
    /**Закрывает заявку. возвращает результат в виде String*/
    suspend fun closeTask(comment: String, summ: String): String {
        val dataCloseTask = DataCloseTask(
            id_task = getSingleTask().id_task,
            state_task = TASK_COMPLETED,
            id_user = getUserId(),
            comment = comment,
            finish = FINISH_OK,
            summ = summ,
            material = getSelectMaterial()
        )
        val result = networkApi.postCloseTask(dataCloseTask)
        return resultToString(result)
    }

    fun getUserData(): User = ldUserData.value ?: User()

    private fun getUserId(): String = ldUserData.value?.id.toString()

    fun getSelectorState(): Char = selectorState.value ?: TASK_APPOINTED

    fun getSelectorDay(): String = selectorDay.value ?: getDateTime(TODAY)

    private fun getSingleTask(): TaskModel = singleTask.value ?: TaskModel()


    fun setSelectorDay(day: Int) {
        /**Принимает константу положения переключателя дня и преобразует в строку*/
        selectorDay.value = getDateTime(day)
    }

    fun setSelectorDay(day: String) {
        /**Принимает строковое значение даты*/
        selectorDay.value = day
    }

    fun setSelectorState(state: Char) {
        selectorState.value = state
    }

    fun getSelectMaterial(): ArrayList<MaterialUsed> = selectMaterial.value ?: ArrayList()


    private fun resultToString(result: ResponseCloseTask): String {
        return if (result.result) "Успешно закрыта" else "Ошибка закрытия заявки"
    }

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