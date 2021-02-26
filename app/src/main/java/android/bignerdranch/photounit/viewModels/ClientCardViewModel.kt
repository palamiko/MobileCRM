package android.bignerdranch.photounit.viewModels

import android.bignerdranch.photounit.fragments.BaseFragment.Companion.exceptionHandler
import android.bignerdranch.photounit.model.modelsDB.ClientCard
import android.bignerdranch.photounit.model.modelsDB.ClientCardBilling
import android.bignerdranch.photounit.model.networkModel.ResultCableTest
import android.bignerdranch.photounit.network.NetworkApiService
import android.bignerdranch.photounit.network.NetworkModule
import android.bignerdranch.photounit.network.NetworkModule2
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

class ClientCardViewModel: ViewModel() {

    val clientCard = MutableLiveData<ClientCard>()  // Хранит карту абонента
    val clientCardBilling = MutableLiveData<ClientCardBilling>()
    val cableTest = MutableLiveData<ResultCableTest>()

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

    private fun getIpSwitch(): String = clientCard.value?.ip_switch.toString()
    private fun getTypeSwitch(): String = clientCard.value?.type_switch.toString()
    private fun getPortSwitch(): String = clientCard.value?.number_port.toString()

}