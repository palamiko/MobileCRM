package android.bignerdranch.photounit.viewModels

import android.bignerdranch.photounit.model.MaterialUsed
import android.bignerdranch.photounit.model.User
import android.bignerdranch.photounit.model.modelsDB.TaskList
import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.TASK_APPOINTED
import android.bignerdranch.photounit.utilits.TODAY
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class TaskViewModel : ViewModel(), DataBaseCommunication {

    val arrayTask = MutableLiveData<ArrayList<TaskList>>()  // Массив заявок с сервера
    val singleTask = MutableLiveData<TaskList>()  // Данные одной заявки


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

    fun getTaskDef() {
        httpGetListTask(arrayTask, getUserDataId())
    }

    fun getTask() {
        httpGetListTask(arrayTask, getUserDataId(), selectorState.value!!, selectorDay.value!!)
    }

    fun getUserData(): User = ldUserData.value ?: User()

    fun getUserDataId(): String = ldUserData.value?.id.toString()

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

    fun getListMaterial() {
        /**Получить список материалов*/
        httpGetListMaterial(arrayMaterialList)
    }
}