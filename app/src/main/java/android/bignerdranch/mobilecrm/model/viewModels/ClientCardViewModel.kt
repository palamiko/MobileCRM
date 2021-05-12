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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

class ClientCardViewModel: ViewModel() {

    // Карта абонента из CRM
    private val _clientCard = MutableLiveData<ClientCard>()
    val clientCard: LiveData<ClientCard> = _clientCard

    // Карта абонента из billing
    private val _clientCardBilling = MutableLiveData<ClientCardBilling>()
    val clientCardBilling: LiveData<ClientCardBilling> = _clientCardBilling

    // Результат кабель теста
    private val _cableTest = MutableLiveData<ResultCableTest>()
    val cableTest: LiveData<ResultCableTest> = _cableTest

    // Результат линк теста
    private val _linkStatus = MutableLiveData<ResultLinkStatus>()
    val linkStatus: LiveData<ResultLinkStatus> = _linkStatus

    // Результат колличества ошибок
    private val _countErrors = MutableLiveData<ResultErrorTest>()
    val countErrors: LiveData<ResultErrorTest> = _countErrors

    // Результат скорости порта
    private val _speedPort = MutableLiveData<ResultSpeedPort>()
    val speedPort: LiveData<ResultSpeedPort> = _speedPort

    // Список истории заявок
    private val _historyTaskList = MutableLiveData<List<HistoryTask>>()
    val historyTaskList: LiveData<List<HistoryTask>> = _historyTaskList

    // Список действий на заявке
    private val _actionTaskList = MutableLiveData<List<ActionTask>>()
    val actionTaskList: LiveData<List<ActionTask>> = _actionTaskList


    @ExperimentalSerializationApi
    private val networkApi: NetworkApiService = NetworkModule().networkApiService
    @ExperimentalSerializationApi
    private val networkApi2: NetworkApiService = NetworkModule2().networkApiService


    @ExperimentalSerializationApi
    fun getClientCardCRM(id_client: String) {
        this.viewModelScope.launch(exceptionHandler) {
            _clientCard.postValue(networkApi2.getClientCard(id_client))
        }
    }

    @ExperimentalSerializationApi
    fun getClientCardBilling(number_contract: String) {
        this.viewModelScope.launch(exceptionHandler) {
            _clientCardBilling.postValue(networkApi.getClientCardBilling(number_contract))
        }
    }

    @ExperimentalSerializationApi
    fun getCableTest() {
        this.viewModelScope.launch(exceptionHandler) {
            _cableTest.postValue (
                networkApi.getCableTest(getIpSwitch(), getPortSwitch(), getTypeSwitch())
            )
        }
    }

    @ExperimentalSerializationApi
    fun getLinkStatus() {
        this.viewModelScope.launch(exceptionHandler) {
            _linkStatus.postValue(
                networkApi.getLinkStatus(getIpSwitch(), getPortSwitch(), getTypeSwitch())
            )
        }
    }

    @ExperimentalSerializationApi
    fun getCountErrors() {
        this.viewModelScope.launch(exceptionHandler) {
            _countErrors.postValue(
                networkApi.getErrors(getIpSwitch(), getPortSwitch(), getTypeSwitch())
            )
        }
    }

    @ExperimentalSerializationApi
    fun getSpeedPort() {
        this.viewModelScope.launch(exceptionHandler) {
            _speedPort.postValue(
                networkApi.getSpeedPort(getIpSwitch(), getPortSwitch(), getTypeSwitch())
            )
        }
    }

    @ExperimentalSerializationApi
    fun getHistoryTaskList() {
        this.viewModelScope.launch(exceptionHandler) {
            _historyTaskList.postValue(
                networkApi.getHistoryListTask(getIdClient())
            )
        }
    }

    @ExperimentalSerializationApi
    fun getActionForTask(id_task: String) {
        this.viewModelScope.launch(exceptionHandler) {
            _actionTaskList.postValue(
                networkApi.getActionForTask(id_task)
            )
        }
    }

    private fun getIpSwitch(): String = clientCard.value?.ip_switch.toString()
    private fun getTypeSwitch(): String = clientCard.value?.type_switch.toString()
    private fun getPortSwitch(): String = clientCard.value?.number_port.toString()
    private fun getIdClient(): String = clientCard.value?.id_client.toString()



    companion object {
        val itemsCard = listOf("Адрес:", "Телефон:", "Номер договора:",
            "Учетная запись:", "Баланс:", "Тариф:",
            "Активная сессия:", "Номер порта:",
            "Модель свитча:", "Адрес узла:")
    }
}