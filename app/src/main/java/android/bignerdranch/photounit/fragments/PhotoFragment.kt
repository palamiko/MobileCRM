package android.bignerdranch.photounit.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.TODAY
import android.bignerdranch.photounit.utilits.UploadUtility
import android.bignerdranch.photounit.viewModels.SharedViewModel
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_photo.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_IMAGE_CAPTURE = 1

class PhotoFragment : BaseFragment(R.layout.fragment_photo) {

    private val sharedModel: SharedViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        setLiveDataObserve()
        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        bindAllView()
    }

    private fun dispatchTakePictureIntent() {
        // Открываем камеру для фото и создаем фаил с именем даты сегодня.
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                sharedModel.currentPhotoPath = photoFile!!
                // Continue only if the File was successfully created
                photoFile.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "android.bignerdranch.photounit.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Создает фаил с именем
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Получает от активити камеры снимок назад
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            val takenImage =
                BitmapFactory.decodeFile(sharedModel.currentPhotoPath.absolutePath) // Само фото
            val rotateImage: Bitmap =
                rotateImageFun(takenImage) // Переворачиваем фото в функции
            val stream = ByteArrayOutputStream()
            rotateImage.compress(Bitmap.CompressFormat.JPEG, 100, stream) // Компрессия до JPEG
            val byteArray: ByteArray =
                stream.toByteArray() // Конвертация в ByteArray для передачи в POST
            // Помещаем JPEG и ByteArray каждый в свой LiveData во ViewModel
            sharedModel.setFilePhotoByteArr(byteArray) // Нужно для передачи в POST
            sharedModel.setPhoto(rotateImage) // Нужно для отображения на экране в ImageView
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun rotateImageFun(image: Bitmap): Bitmap {
        // Переворачивает фотографию и возвращает нормальный вариант
        val mMatrix = Matrix()
        mMatrix.setRotate(90F, image.width.toFloat(), image.height.toFloat())
        return Bitmap.createBitmap(image, 0, 0, image.width, image.height, mMatrix, true)
    }

    private fun bindAllView() {
        // Биндим кнопки
        btn_get_photo.setOnClickListener { (dispatchTakePictureIntent()) } // Сделать фото

        btn_load_photo.setOnClickListener { uploadImageToServer() } // Отправить на сервер
        if (sharedModel.textFullAddress != "") { // Пока нет полного аддреса кнопку не видно
            btn_load_photo.isVisible = true
        } else btn_load_photo.isInvisible = true

        btn_add_address.setOnClickListener {
            navController.navigate(R.id.action_photoFragment_to_districtListFragment)
            println(object : DataBaseCommunication{}.getDateTime(TODAY))

        }
        if (sharedModel.photoLiveData.value != null) { // Пока нет фото кнопку не видно
            btn_add_address.isVisible = true
        } else btn_add_address.isInvisible = true

        textViewFullAddress.text = sharedModel.textFullAddress


    }

    private fun uploadImageToServer() {
        val photo: ByteArray? = sharedModel.getFilePhotoByteArr()
        if (photo != null) {
            File(sharedModel.currentPhotoPath.absolutePath).writeBytes(photo) // Создаем фаил фото из ByteArray
            UploadUtility(requireActivity(), sharedModel.idHome.value.toString()) // Указываем Id дома
                .uploadFile(sharedModel.currentPhotoPath.absolutePath) // Отправляем фото в POST
        } else {
            Toast.makeText(requireContext(), "Сделай фото", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLiveDataObserve() {
        sharedModel.photoLiveData.observe(this, {
            imageView.setImageBitmap(it) // Выставляет фотку в imageView
        })
    }

    private fun requestPermissions() {
        // Проверяет и запрашивает разрешения
        val permissions = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.CAMERA)
        }
    }

    override fun onStop() {
        sharedModel.photoLiveData.removeObservers(this)
        super.onStop()
    }
}



