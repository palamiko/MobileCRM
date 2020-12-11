package android.bignerdranch.photounit.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.REQUEST_IMAGE_CAPTURE
import android.bignerdranch.photounit.utilits.SharedViewModel
import android.bignerdranch.photounit.utilits.UploadUtility
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_photo.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PhotoFragment : Fragment(R.layout.fragment_photo) {

    private val sharedModel: SharedViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        setLiveDataObserve()
        bindButton()
        requestPermissions()
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

    private fun bindButton() {
        // Биндим кнопки
        btn_get_photo.setOnClickListener { (dispatchTakePictureIntent()) } // Сделать фото
        btn_load_photo.setOnClickListener { uploadImageToServer() } // Отправить на сервер
        btn_add_address.setOnClickListener {
            sharedModel.idCurrentFragment.value = R.layout.fragment_list_distric
        }
        //btn_add_address.setOnClickListener { mViewModel.httpGetJson(GET_STREET, "12") }
    }

    private fun uploadImageToServer() {
        val photo: ByteArray? = sharedModel.getFilePhotoByteArr()
        if (photo != null) {
            File(sharedModel.currentPhotoPath.absolutePath).writeBytes(photo) // Создаем фаил фото из ByteArray
            UploadUtility(requireActivity()).uploadFile(sharedModel.currentPhotoPath.absolutePath) // Отправляем фото в POST
        } else {
            Toast.makeText(requireContext(), "Сделай фото", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLiveDataObserve() {
        sharedModel.photoLiveData.observe(this, androidx.lifecycle.Observer {
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
}


