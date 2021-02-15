package android.bignerdranch.photounit.network

import android.bignerdranch.photounit.model.TokenFirebase
import android.bignerdranch.photounit.model.modelsDB.Home
import android.bignerdranch.photounit.model.modelsDB.Street
import android.bignerdranch.photounit.utilits.GET_AUTHORIZATION
import android.bignerdranch.photounit.utilits.GET_HOME
import android.bignerdranch.photounit.utilits.GET_STREET
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

    companion object {
        /**Параметры запросов*/

        const val DISTRICT_ID = "district_id"
        const val STREET_ID = "street_id"
        const val LOGIN = "login_auth"
        const val PASSWORD = "password_auth"
    }
}