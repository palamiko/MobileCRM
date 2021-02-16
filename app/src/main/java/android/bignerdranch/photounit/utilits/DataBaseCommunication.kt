package android.bignerdranch.photounit.utilits


import android.bignerdranch.photounit.model.DataCloseTask
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


interface DataBaseCommunication {

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
}