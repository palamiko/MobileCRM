package android.bignerdranch.photounit.utilits

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.FuelJson
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*

class SharedViewModel : ViewModel() {
    var photoLiveData = MutableLiveData<Bitmap>()
    var filePhoto = MutableLiveData<ByteArray>()
    lateinit var currentPhotoPath: File

    val idCurrentFragment = MutableLiveData<Int>()

    val listStreet = MutableLiveData<ArrayList<String>>() // Получает данные из createListStreet()
    val mapStreet = MutableLiveData<Map<String, String>>() // Сюда данные придут из Get запроса
    val mapHome = MutableLiveData<Map<String, Any>>()

    var idDistrict =
        MutableLiveData<Int>() // Сюда id помещается при выборе на экране-фрагменте района


    // Bitmap LiveData для хранения и установки полученого изображения в ImageView
    fun setPhoto(photo: Bitmap) {
        photoLiveData.value = photo
    }

    fun getPhoto(): Bitmap? {
        return photoLiveData.value
    }

    // Сюда помещается фото переделаное в ByteArray для отпраки в POST запросе на сервер.
    fun setFilePhotoByteArr(photo: ByteArray) {
        filePhoto.value = photo
    }

    fun getFilePhotoByteArr(): ByteArray? {
        return filePhoto.value
    }

    fun httpGetJson(whoGet: String, id: String) {
        /** Функция принимает whoGet - является продолжением ссылки и названием вызываемой
         * функции со стороны сервера, id - это id района или улицы в базе CRM,
         * после запроса возвращается Json объект кторый десериализуется в Map масив и
         * заносится в LiveData переменную*/

        val httpAsync = "$SERVER_ADDRESS/$whoGet/$id"
            .httpGet()
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
                            jsonData.obj().toString(), // Конвектируем в kotlin Map<String, String>
                            object : TypeToken<Map<String, String>>() {}.type
                        )
                        mapStreet.postValue(tutorialMap) // Помещаем результат в LiveData переменную
                    }
                }
            }
        httpAsync.join()
    }

    fun getIdDistrictFromMap(nameDistrict: String) {
        idDistrict.value =
            districtMap.getValue(nameDistrict) // Извлекаем id района по его имени и помещаем в LiveData
    }

    fun createListStreet(_mapStreet: Map<String, String>) {
    /**Функция из словаря делает список с названиями улиц для ListView*/

       listStreet.value = ArrayList(_mapStreet.keys.toList())
    }
}