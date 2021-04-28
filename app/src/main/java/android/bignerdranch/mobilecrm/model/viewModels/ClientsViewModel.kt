package android.bignerdranch.mobilecrm.model.viewModels

import android.bignerdranch.mobilecrm.model.modelsDB.ClientsEntrance
import android.bignerdranch.mobilecrm.model.modelsDB.Entrance
import android.bignerdranch.mobilecrm.model.modelsDB.Home
import android.bignerdranch.mobilecrm.model.modelsDB.Street
import android.bignerdranch.mobilecrm.network.NetworkApiService
import android.bignerdranch.mobilecrm.network.NetworkModule
import android.bignerdranch.mobilecrm.ui.fragments.BaseFragment.Companion.exceptionHandler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

class ClientsViewModel: ViewModel() {

    private val _streetList = MutableLiveData<List<Street>>()
    val streetList: LiveData<List<Street>> = _streetList

    private val _homeList = MutableLiveData<List<Home>>()
    val homeList: LiveData<List<Home>> = _homeList

    private val _entrance = MutableLiveData<List<Entrance>>()
    val entranceList: LiveData<List<Entrance>> = _entrance

    private val _clientsEntrance = MutableLiveData<List<ClientsEntrance>>()
    val clientsEntrance: LiveData<List<ClientsEntrance>> = _clientsEntrance


    @ExperimentalSerializationApi
    private val networkApi: NetworkApiService = NetworkModule().networkApiService

    @ExperimentalSerializationApi
    fun getStreet(district_id: Int) {
        /** Получает список улиц в микрорайоне */

        viewModelScope.launch(exceptionHandler) {
            _streetList.postValue(
                networkApi.getStreetList(district_id = district_id)
            )
        }
    }

    @ExperimentalSerializationApi
    fun getHomes(district_id: Int, street_id: Int) {
        /** Получает список домов на улице */

        viewModelScope.launch(exceptionHandler) {
            _homeList.postValue(
                networkApi.getHomeList(district_id = district_id, street_id = street_id)
            )
        }
    }

    @ExperimentalSerializationApi
    fun getEntrances(building_id: Int) {
        /** Получает список подъездов в доме */

        viewModelScope.launch(exceptionHandler) {
            _entrance.postValue(
                networkApi.getEntrancesList(building_id = building_id)
            )
        }
    }

    @ExperimentalSerializationApi
    fun getClientsEntrance(entrance_id: Int) {
        /** Получает клиентов в данном подъезде */

        viewModelScope.launch(exceptionHandler) {
            _clientsEntrance.postValue(
                networkApi.getClientsInEntrance(entrance_id = entrance_id)
            )
        }
    }

}