package android.bignerdranch.photounit.network

import android.bignerdranch.photounit.utilits.AUTH_PASS
import android.bignerdranch.photounit.utilits.AUTH_USER
import android.bignerdranch.photounit.utilits.BASE_URL
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create


class NetworkModule {

    private val baseUrl = BASE_URL  // Адрес сервера API
    private val contentType = "application/json".toMediaType()  // Медиатайп Json
    private val json = Json {
        prettyPrint = true  // Читабельные отступы при показе json
        ignoreUnknownKeys = true  // Игнорировать неизвестные поля в Json объекте
    }
    private val httpClient =  OkHttpClient.Builder()
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

class BasicAuthInterceptor(username: String, password: String): Interceptor {
    private var credentials: String = Credentials.basic(username, password)

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        request = request.newBuilder().header("Authorization", credentials).build()
        return chain.proceed(request)
    }
}


