package android.bignerdranch.photounit.viewModels

import android.bignerdranch.photounit.model.MaterialUsed
import android.bignerdranch.photounit.model.User
import android.bignerdranch.photounit.model.modelsDB.TaskModel
import android.bignerdranch.photounit.network.NetworkApiService
import android.bignerdranch.photounit.network.NetworkModule
import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.TASK_APPOINTED
import android.bignerdranch.photounit.utilits.TODAY
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


class TaskViewModel: ViewModel(), DataBaseCommunication {

    @ExperimentalSerializationApi
    private val networkApi: NetworkApiService = NetworkModule().networkApiService

    val arrayTask = MutableLiveData<ArrayList<TaskModel>>()  // Массив заявок с сервера
    val singleTask = MutableLiveData<TaskModel>()  // Данные одной заявки


    val selectMaterial = MutableLiveData<ArrayList<MaterialUsed>>()  // Здесь содержатся выбранные для списания материалы
    val arrayMaterialList = MutableLiveData<ArrayList<MaterialUsed>>()  // Сюда загружается список доступных материалов с сервера

    val ldUserData = MutableLiveData<User>()  // Данные пользователя из Firebase


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
    suspend fun newGetTask(id_master: String, state_task: Char = TASK_APPOINTED,
                           date_making: String ): ArrayList<TaskModel> {
        return networkApi.getTaskMaster(id_master, state_task, date_making)
    }

    @ExperimentalSerializationApi
    suspend fun updateTask() {
        val responseTask = newGetTask(getUserId(), getSelectorState(), getSelectorDay())
        if (arrayTask.value != responseTask) arrayTask.postValue(responseTask)
    }

    @ExperimentalSerializationApi
    suspend fun getMaterial() {
        val result = networkApi.getMaterial()
        if (arrayMaterialList.value != result) arrayMaterialList.postValue(result)
    }

    fun getUserData(): User = ldUserData.value ?: User()

    fun getUserId(): String = ldUserData.value?.id.toString()

    fun getSelectorState(): Char = selectorState.value ?: TASK_APPOINTED

    fun getSelectorDay(): String = selectorDay.value ?: getDateTime(TODAY)


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