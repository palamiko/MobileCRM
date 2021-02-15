package android.bignerdranch.photounit.viewModels

import android.bignerdranch.photounit.model.Photo
import android.bignerdranch.photounit.model.modelsDB.District
import android.bignerdranch.photounit.model.modelsDB.Home
import android.bignerdranch.photounit.model.modelsDB.Street
import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.districtMap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class SharedViewModel : ViewModel(), DataBaseCommunication {
    var photoLiveData = MutableLiveData<Bitmap>() // Содержит сфотографироанное изображение .bmp
    var filePhoto = MutableLiveData<Photo>() // Содержит класс Photo

    var streetNameList: ArrayList<String> = arrayListOf() // Список названий улиц
    var streetListResponse: List<Street> = mutableListOf() // Список экземпляров улиц полученный с сервера

    var homeNumberList: ArrayList<String> = arrayListOf()  // Список названий домов
    var homeListResponse: List<Home> = mutableListOf()  // Список экземпляров домов полученный с сервера

    lateinit var mDistrict: District // Хранит экземпляр выбранного района
    lateinit var mStreet: Street  // Хранит экземпляр выбранной улицы
    lateinit var mHome: Home  // Хранит экземпляр выбранного дома


    /**Вспомогательные временные переменные*/

    var tempSelectNameDistrict: String = "" // Здесь содержится строковое имя выбранного района
    var tempSelectNameStreet: String = "" // Здесь содержится строковое имя выбранной улицы
    var tempSelectNameHome: String = "" // Здесь содержится строковый номер дома
    var textFullAddress: String = ""


    // Bitmap LiveData для хранения и установки полученого изображения в ImageView
    fun setPhotoJpeg(photo: Photo) {
        photoLiveData.value = compressToJpeg(photo)
    }

    fun getPhotoJpeg(): Bitmap {
        return photoLiveData.value!!
    }

    // Сюда помещается экземпляр класса Photo
    fun setPhoto(photo: Photo) {
        filePhoto.value = photo
    }

    fun getPhoto(): Photo? {
        return filePhoto.value
    }

    fun getSelectDistrict(nameDistrict: String) {
        mDistrict = District(districtMap[nameDistrict]!!, nameDistrict )
        tempSelectNameDistrict = "$nameDistrict "  // Формируем строку полного адреса для TextView
    }

    fun getSelectStreet(position: Int) {
        mStreet = streetListResponse[position]
        tempSelectNameStreet = "${mStreet.name} " // Формируем строку полного адреса для TextView
    }

    fun getSelectHome(position: Int) {
        mHome = homeListResponse[position] // Извлекаем id дома по его номеру
        tempSelectNameHome = mHome.number
    }

    suspend fun createListStreet() = withContext(Dispatchers.IO) {
        val tempListNameStreet = arrayListOf<String>()
        streetListResponse.forEach {
            tempListNameStreet.add(it.name)
        }
        streetNameList = tempListNameStreet
    }

    suspend fun createListHome() = withContext(Dispatchers.IO){
        val tempListNameHome = arrayListOf<String>()
        homeListResponse.forEach {
            tempListNameHome.add(it.number)
        }
        homeNumberList = tempListNameHome
    }

    fun setCurrentTextFullAddress() {
        textFullAddress = tempSelectNameDistrict + tempSelectNameStreet + tempSelectNameHome
    }

    fun compressToJpeg(photo: Photo): Bitmap {
        /** Пережимает фото до Jpeg */
        val takenImage = BitmapFactory.decodeFile(photo.filePath) // Само фото
        val stream = ByteArrayOutputStream()
        takenImage.compress(Bitmap.CompressFormat.JPEG, 100, stream) // Компрессия до JPEG
        return takenImage
    }
}