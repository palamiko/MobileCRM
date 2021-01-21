package android.bignerdranch.photounit.viewModels

import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.districtMap
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class SharedViewModel : ViewModel(), DataBaseCommunication {
    var photoLiveData = MutableLiveData<Bitmap>() // Содержит сфотографироанное изображение .bmp
    var filePhoto = MutableLiveData<ByteArray>() // Содержит фотку в ByteArray для передачи в POST
    lateinit var currentPhotoPath: File // Содержит полный путь до файла фотографии

    var textFullAddress: String = ""

    val listStreet = MutableLiveData<ArrayList<String>>() // Получает данные из createListStreet()
    val mapStreet = MutableLiveData<Map<String, String>>() // Сюда данные придут из Get запроса
    val listHome = MutableLiveData<ArrayList<String>>()
    val mapHome = MutableLiveData<Map<String, String>>()

    var idDistrict = MutableLiveData<Int>() // Сюда id помещается при выборе на экране-фрагменте района
    var idStreet = MutableLiveData<String>()
    var idHome = MutableLiveData<String>()


    /**Вспомогательные временные переменные*/

    var tempSelectNameDistrict: String = "" // Здесь содержится строковое имя выбранного района
    var tempSelectNameStreet: String = "" // Здесь содержится строковое имя выбранной улицы
    var tempSelectNameHome: String = "" // Здесь содержится строковый номер дома


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

    fun getIdDistrictFromMap(nameDistrict: String) {
        idDistrict.value =
            districtMap.getValue(nameDistrict) // Извлекаем id района по его имени и помещаем в LiveData
    }

    fun getIdStreetFromMap(nameStreet: String) {
        idStreet.value =
            mapStreet.value?.getValue(nameStreet)// Извлекаем id улицы из словаря по его имени и помещаем в LiveData
    }

    fun getIdHomeFromMap(nameHome: String) {
        idHome.value =
            mapHome.value?.getValue(nameHome)// Извлекаем id дома из словаря по его имени и помещаем в LiveData
    }

    fun createListStreet(_mapStreet: Map<String, String>) {
    /**Функция из словаря делает список с названиями улиц для ListView*/

       listStreet.value = ArrayList(_mapStreet.keys.toList())
    }

    fun createListHome(_mapHome: Map<String, String>) {
        listHome.value = ArrayList(_mapHome.keys.toList())
    }

    fun setCurrentTextFullAddress() {
        textFullAddress = tempSelectNameDistrict + tempSelectNameStreet + tempSelectNameHome
    }
}