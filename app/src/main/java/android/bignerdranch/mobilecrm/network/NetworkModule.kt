package android.bignerdranch.mobilecrm.network

import android.bignerdranch.mobilecrm.utilits.helpers.AUTH_PASS
import android.bignerdranch.mobilecrm.utilits.helpers.AUTH_USER
import android.bignerdranch.mobilecrm.utilits.helpers.BASE_URL
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.create


class NetworkModule {
    private val baseUrl = BASE_URL  // Адрес сервера API
    private val contentType = "application/json".toMediaType()  // Медиатайп Json
    private val json = Json {
        prettyPrint = true  // Читабельные отступы при показе json
        ignoreUnknownKeys = true  // Игнорировать неизвестные поля в Json объекте
        isLenient = true
    }
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(BasicAuthInterceptor(AUTH_USER, AUTH_PASS))  // Базовая авторизация
        .build()

    @ExperimentalSerializationApi
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory(contentType))
        .client(httpClient)
        .build()

    @ExperimentalSerializationApi
    val networkApiService: NetworkApiService = retrofitBuilder.create()
}

class NetworkModule2 {

    private val baseUrl = BASE_URL  // Адрес сервера API
    private val contentType = "application/json".toMediaType()  // Медиатайп Json
    private val json = Json {
        prettyPrint = true  // Читабельные отступы при показе json
        ignoreUnknownKeys = true  // Игнорировать неизвестные поля в Json объекте
        isLenient = true
    }
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(BasicAuthInterceptor(AUTH_USER, AUTH_PASS))  // Базовая авторизация
        .addInterceptor(MyInterceptor())
        .build()

    @ExperimentalSerializationApi
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory(contentType))
        .client(httpClient)
        .build()

    @ExperimentalSerializationApi
    val networkApiService: NetworkApiService = retrofitBuilder.create()
}

class BasicAuthInterceptor(username: String, password: String) : Interceptor {
    private var credentials: String = Credentials.basic(username, password)

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().header("Authorization", credentials).build()
        return chain.proceed(request)
    }
}

class MyInterceptor : Interceptor {
    /**Перехватывает ответ на запрос клиентской карточкит биллинга и удаляет из него []
     * для корректной десериализации */

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response = chain.proceed(request)
        val body = response.body
        val newBody = body?.string()?.dropWhile { it == '[' }?.dropLastWhile { it == ']' }
        return response.newBuilder().body(
            newBody?.toResponseBody(body.contentType())
        ).build()
    }
}
