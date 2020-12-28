package android.bignerdranch.photounit.utilits

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.result.Result
import org.json.JSONArray
import org.json.JSONObject

/**Класс для приема и десериализации Json сообщений*/
class FuelJson(val content: String) {
    fun obj(): JSONObject = JSONObject(content)
    fun array(): JSONArray = JSONArray(content)
}

fun Request.responseJson(handler: (Request, Response, Result<FuelJson, FuelError>) -> Unit) =
    response(jsonDeserializer(), handler)

fun Request.responseJson(handler: ResponseHandler<FuelJson>) = response(jsonDeserializer(), handler)

fun Request.responseJson() = response(jsonDeserializer())

fun jsonDeserializer() = object : ResponseDeserializable<FuelJson> {
    override fun deserialize(response: Response): FuelJson = FuelJson(String(response.data))
}