package android.bignerdranch.mobilecrm.model.viewModels

import android.bignerdranch.mobilecrm.model.modelsDB.ActionTask
import android.bignerdranch.mobilecrm.model.modelsDB.ClientCard
import android.bignerdranch.mobilecrm.model.modelsDB.ClientCardBilling
import android.bignerdranch.mobilecrm.model.modelsDB.HistoryTask
import android.bignerdranch.mobilecrm.model.networkModel.ResultCableTest
import android.bignerdranch.mobilecrm.model.networkModel.ResultErrorTest
import android.bignerdranch.mobilecrm.model.networkModel.ResultLinkStatus
import android.bignerdranch.mobilecrm.model.networkModel.ResultSpeedPort
import android.bignerdranch.mobilecrm.network.NetworkApiService
import android.bignerdranch.mobilecrm.network.NetworkModule
import android.bignerdranch.mobilecrm.network.NetworkModule2
import android.bignerdranch.mobilecrm.ui.fragments.BaseFragment.Companion.exceptionHandler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

class ClientCardViewModel: ViewModel() {

    val clientCard = MutableLiveData<ClientCard>()                // Карта абонента из CRM
    val clientCardBilling = MutableLiveData<ClientCardBilling>()  // Карта абонента из billing
    val cableTest = MutableLiveData<ResultCableTest>()            // Результат кабель теста
    val linkStatus = MutableLiveData<ResultLinkStatus>()          // Результат линк теста
    val countErrors = MutableLiveData<ResultErrorTest>()          // Результат колличества ошибок
    val speedPort = MutableLiveData<ResultSpeedPort>()            // Результат скорости порта
    val historyTaskList = MutableLiveData<List<HistoryTask>>()    // Список истории заявок
    val actionTaskList = MutableLiveData<List<ActionTask>>()
    val itemsCard = listOf("Адрес:", "Телефон:", "Номер договора:",
                           "Учетная запись:", "Баланс:", "Тариф:",
                           "Активная сессия:", "Номер порта:",
                           "Модель свитча:", "Адрес узла:")

    @ExperimentalSerializationApi
    private val networkApi: NetworkApiService = NetworkModule().networkApiService
    @ExperimentalSerializationApi
    private val networkApi2: NetworkApiService = NetworkModule2().networkApiService


    @ExperimentalSerializationApi
    fun getClientCardCRM(id_client: String) {
        this.viewModelScope.launch(exceptionHandler) {
            clientCard.postValue(networkApi2.getClientCard(id_client))
        }
    }

    @ExperimentalSerializationApi
    fun getClientCardBilling(number_contract: String) {
        this.viewModelScope.launch(exceptionHandler) {
            clientCardBilling.postValue(networkApi.getClientCardBilling(number_contract))
        }
    }

    @ExperimentalSerializationApi
    fun getCableTest() {
        this.viewModelScope.launch(exceptionHandler) {
            cableTest.postValue (
                networkApi.getCableTest(getIpSwitch(), getPortSwitch(), getTypeSwitch())
            )
        }
    }

    @ExperimentalSerializationApi
    fun getLinkStatus() {
        this.viewModelScope.launch(exceptionHandler) {
            linkStatus.postValue(
                networkApi.getLinkStatus(getIpSwitch(), getPortSwitch(), getTypeSwitch())
            )
        }
    }

    @ExperimentalSerializationApi
    fun getCountErrors() {
        this.viewModelScope.launch(exceptionHandler) {
            countErrors.postValue(
                networkApi.getErrors(getIpSwitch(), getPortSwitch(), getTypeSwitch())
            )
        }
    }

    @ExperimentalSerializationApi
    fun getSpeedPort() {
        this.viewModelScope.launch(exceptionHandler) {
            speedPort.postValue(
                networkApi.getSpeedPort(getIpSwitch(), getPortSwitch(), getTypeSwitch())
            )
        }
    }

    @ExperimentalSerializationApi
    fun getHistoryTaskList() {
        this.viewModelScope.launch(exceptionHandler) {
            historyTaskList.postValue(
                networkApi.getHistoryListTask(getIdClient())
            )
        }
    }

    @ExperimentalSerializationApi
    fun getActionForTask(id_task: String) {
        this.viewModelScope.launch() {
            actionTaskList.postValue(
                networkApi.getActionForTask(id_task)
            )
        }
    }

    private fun getIpSwitch(): String = clientCard.value?.ip_switch.toString()
    private fun getTypeSwitch(): String = clientCard.value?.type_switch.toString()
    private fun getPortSwitch(): String = clientCard.value?.number_port.toString()
    private fun getIdClient(): String = clientCard.value?.id_client.toString()

}