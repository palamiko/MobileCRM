package android.bignerdranch.photounit.utilits


import android.bignerdranch.photounit.model.DataCloseTask
import android.bignerdranch.photounit.model.MaterialUsed
import android.bignerdranch.photounit.model.User
import android.bignerdranch.photounit.model.modelsDB.TaskList
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.FuelJson
import com.github.kittinunf.result.Result
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter


interface DataBaseCommunication {


    fun httpGetListStreet(id: String, savedObjects: MutableLiveData<Map<String, String>>) {
        /** Функция принимает whoGet - является продолжением ссылки и названием вызываемой
         * функции со стороны сервера, id - это id района или улицы в базе CRM,
         * после запроса возвращается Json объект кторый десериализуется в Map масив и
         * заносится в LiveData переменную*/
        CoroutineScope(Dispatchers.IO).launch {
            val httpAsync = "$SERVER_ADDRESS/$GET_STREET/$id"
                .httpGet()
                .authentication()
                .basic(AUTH_USER, AUTH_PASS)
                .responseJson { _, _, result ->
                    when (result) {
                        is Result.Failure -> {
                            val ex = result.getException()
                            println(ex)
                        }
                        is Result.Success -> {
                            val data = result.get() // Получаем ответ в виде строки
                            val jsonData = FuelJson(data.content) // Конвектируем в JsonObject
                            val mGson = Gson()
                            val tutorialMap: Map<String, String> = mGson.fromJson(
                                jsonData.obj()
                                    .toString(), // Конвектируем в kotlin Map<String, String>
                                object : TypeToken<Map<String, String>>() {}.type
                            )
                            savedObjects.postValue(tutorialMap) // Помещаем результат в LiveData переменную
                        }
                    }
                }
            httpAsync.join()
        }
    }

    fun httpGetListHome(
        idDistrict: String,
        idStreet: String,
        savedObjects: MutableLiveData<Map<String, String>>
    ) {
        /** Функция принимает whoGet - является продолжением ссылки и названием вызываемой
         * функции со стороны сервера, idDistrict - это id района в базе CRM, idStreet это id улицы
         * после запроса возвращается Json объект с номерами и id домов,
         * который десериализуется в Map масив и заносится в LiveData переменную*/

        CoroutineScope(Dispatchers.IO).launch {
            val httpAsync = "$SERVER_ADDRESS/$GET_HOME/$idDistrict/$idStreet"
                .httpGet()
                .authentication()
                .basic(AUTH_USER, AUTH_PASS)
                .responseJson { _, _, result ->
                    when (result) {
                        is Result.Failure -> {
                            val ex = result.getException()
                            println(ex)
                        }
                        is Result.Success -> {
                            val data = result.get() // Получаем ответ в виде строки
                            val jsonData = FuelJson(data.content) // Конвектируем в JsonObject
                            val mGson = Gson()
                            val tutorialMap: Map<String, String> = mGson.fromJson(
                                jsonData.obj()
                                    .toString(), // Конвектируем в kotlin Map<String, String>
                                object : TypeToken<Map<String, String>>() {}.type
                            )
                            savedObjects.postValue(tutorialMap) // Помещаем результат в LiveData переменную
                        }
                    }
                }
            httpAsync.join()
        }
    }

    fun httpGetListTask(
        savedObjects: MutableLiveData<ArrayList<TaskList>>,
        id_master: String,
        state_task: Char = TASK_APPOINTED,
        date_making: String = getDateTime(TODAY)
    ) {
        /** Функция делает запрос на сервер и возвращает список заявок на данного мастера*/

        CoroutineScope(Dispatchers.IO).launch {
            val httpAsync =
                "$SERVER_ADDRESS/$GET_TASK_FOR_MASTER/$id_master/$state_task/$date_making"
                    .httpGet()
                    .authentication()
                    .basic(AUTH_USER, AUTH_PASS)
                    .responseJson { _, _, result ->
                        when (result) {
                            is Result.Failure -> {
                                val ex = result.getException()
                                println(ex)
                            }
                            is Result.Success -> {
                                val data = result.get() // Получаем ответ в виде строки
                                val jsonData = FuelJson(data.content) // Конвектируем в JsonObject
                                val mGson = Gson()


                                val tutorialMap: ArrayList<TaskList> = mGson.fromJson(
                                    jsonData.array()
                                        .toString(), // Конвектируем в kotlin Map<String, String>
                                    object : TypeToken<ArrayList<TaskList>>() {}.type
                                )
                                savedObjects.postValue(tutorialMap) // Помещаем результат в LiveData переменную
                            }
                        }
                    }
            httpAsync.join()
        }
    }

    // Функция отправляет POST с login pass, при успешной аутентификации получает токен FireBase
    fun sendPostForAuth(
        login: EditText,
        password: EditText,
        liveDataToken: MutableLiveData<String>
    ) {
        val timeout = 10000 // 5000 milliseconds = 10 seconds.
        val timeoutRead = 60000 // 60000 milliseconds = 1 minute.

        CoroutineScope(Dispatchers.IO).launch {
            Fuel.post("$SERVER_ADDRESS/$AUTH_IN_APP").timeout(timeout).timeoutRead(timeoutRead)
                .authentication()
                .basic(AUTH_USER, AUTH_PASS)
                .jsonBody(
                    """
  { "login" : "${login.text}",
    "password" : "${password.text}"
  }
  """
                )
                .response { _, response, _ ->
                    liveDataToken.postValue(
                        response.body().asString("text/html")
                    )  // Записывает полученый токен в LiveData
                }
        }

    }

    fun closeTask (
        /**Ф-ия закрывает заявку*/
        dataCloseTask: DataCloseTask,
        result: MutableLiveData<String>
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            //val serialazer: KSerializer<List<MaterialUsed>> = ListSerializer(MaterialUsed.serializer()) // Сериализатор для кастомного класса

            val jsonDataCloseTask = Json.encodeToString(dataCloseTask)
            println(jsonDataCloseTask)
            Fuel.post("$SERVER_ADDRESS/$CLOSE_TASK")
                .authentication()
                .basic(AUTH_USER, AUTH_PASS)
                .jsonBody(jsonDataCloseTask)
                .response { request, response, _ ->
                    println(request)
                    result.postValue(
                        response.body().asString("text/html")
                    )  // Записывает полученый ответ в LiveData
                }
        }
    }

    fun getUserDataFromFirebase1(userRef: DatabaseReference, ldUserData: MutableLiveData<User>) {
        /** Функция получает из Firebase данные пользователя после загрузки TaskFragment*/
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ldUserData.value = snapshot.getValue(User::class.java) ?: User()
            }

            override fun onCancelled(error: DatabaseError) {
                println("message: ${error.message} details: ${error.details}")
            }
        }
        )
    }

    fun getUserDataFromFirebase(userRef: DatabaseReference, liveData: MutableLiveData<String> ) {
        /** Функция получает из Firebase данные пользователя после загрузки TaskFragment*/

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                liveData.postValue(Json.encodeToString(snapshot.getValue(User::class.java) ?: User()))
            }

            override fun onCancelled(error: DatabaseError) {
                println("message: ${error.message} details: ${error.details}")
            }
        }
        )
    }

    fun httpGetListMaterial(savedObjects: MutableLiveData<ArrayList<MaterialUsed>>) {
        CoroutineScope(Dispatchers.IO).launch {
            val httpAsync = "$SERVER_ADDRESS/$GET_MATERIAL_LIST"
                .httpGet()
                .authentication()
                .basic(AUTH_USER, AUTH_PASS)
                .responseJson { _, _, result ->
                    when (result) {
                        is Result.Failure -> {
                            val ex = result.getException()
                            println(ex)
                        }
                        is Result.Success -> {
                            val data = result.get() // Получаем ответ в виде строки
                            val jsonData = FuelJson(data.content) // Конвектируем в JsonObject
                            val mGson = Gson()
                            val tutorialMap: ArrayList<MaterialUsed> = mGson.fromJson(
                                jsonData.array()
                                    .toString(), // Конвектируем в kotlin Map<String, String>
                                object : TypeToken<ArrayList<MaterialUsed>>() {}.type
                            )

                            savedObjects.postValue(tutorialMap) // Помещаем результат в LiveData переменную
                        }
                    }
                }
            httpAsync.join()
        }
    }

    fun getDateTime(day: Int): String {
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