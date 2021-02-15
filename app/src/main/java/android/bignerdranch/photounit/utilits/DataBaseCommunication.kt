package android.bignerdranch.photounit.utilits


import android.bignerdranch.photounit.model.DataCloseTask
import android.bignerdranch.photounit.model.MaterialUsed
import android.bignerdranch.photounit.model.User
import android.bignerdranch.photounit.model.modelsDB.TaskList
import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.FuelJson
import com.github.kittinunf.result.Result
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


interface DataBaseCommunication {

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


    fun closeTask (
        /**Ф-ия закрывает заявку*/
        dataCloseTask: DataCloseTask
    ) {
        val timeout = 10000 // 5000 milliseconds = 10 seconds.
        val timeoutRead = 60000 // 60000 milliseconds = 1 minute.

        CoroutineScope(Dispatchers.IO).launch {
            //val serialazer: KSerializer<List<MaterialUsed>> = ListSerializer(MaterialUsed.serializer()) // Сериализатор для кастомного класса

            val jsonDataCloseTask = Json.encodeToString(dataCloseTask)
            println(jsonDataCloseTask)
            Fuel.post("$SERVER_ADDRESS/$CLOSE_TASK").timeout(timeout).timeoutRead(timeoutRead)
                .authentication()
                .basic(AUTH_USER, AUTH_PASS)
                .jsonBody(jsonDataCloseTask)
                .response { _, response, _ ->
                    println(response.body().asString("text/html"))
                }
        }
    }

    suspend fun getDataFromFirebase(reference: DatabaseReference?): String = withContext(Dispatchers.IO) {
        var data = ""
        if (reference != null) {data = Json.encodeToString(reference.get().await().getValue(User::class.java))}
        return@withContext data
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