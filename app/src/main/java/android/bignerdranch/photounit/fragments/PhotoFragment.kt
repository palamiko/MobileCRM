package android.bignerdranch.photounit.fragments

import android.Manifest
import android.app.Activity
import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.databinding.FragmentPhotoBinding
import android.bignerdranch.photounit.utilits.UploadUtility
import android.bignerdranch.photounit.viewModels.SharedViewModel
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.drjacky.imagepicker.ImagePicker
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class PhotoFragment : BaseFragment(R.layout.fragment_photo) {

    private val sharedModel: SharedViewModel by activityViewModels()
    private var binding: FragmentPhotoBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentPhotoBinding.bind(view)
        binding = fragmentBinding
    }

    override fun onStart() {
        super.onStart()
        setLiveDataObserve()
        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        bindAllView()
    }

    private fun getPhoto() {
    /**Функция делает фото путем открытия другого активити и возвращает результат сюда.*/

        ImagePicker.with(this)
            .compress(1024)         //Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
            .start { resultCode, data ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //Image Uri will not be null for RESULT_OK
                        //val fileUri = data?.data
                        //You can get File object from intent
                        //val file: File? = ImagePicker.getFile(data)
                        //You can also get File Path from intent
                        val filePath: String? = ImagePicker.getFilePath(data)
                        val takenImage = BitmapFactory.decodeFile(filePath) // Само фото
                        //val rotateImage: Bitmap = rotateImageFun(takenImage) // Переворачиваем фото в функции
                        val stream = ByteArrayOutputStream()
                        takenImage.compress(Bitmap.CompressFormat.JPEG, 100, stream) // Компрессия до JPEG
                        val byteArray: ByteArray = stream.toByteArray() // Конвертация в ByteArray для передачи в POST
                        // Помещаем JPEG и ByteArray каждый в свой LiveData во ViewModel
                        sharedModel.setFilePhotoByteArr(byteArray) // Нужно для передачи в POST
                        sharedModel.setPhoto(takenImage) // Нужно для отображения на экране в ImageView
                    }
                    ImagePicker.RESULT_ERROR -> {
                        Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }


    private fun bindAllView() {
        // Биндим кнопки
        binding?.btnGetPhoto?.setOnClickListener { getPhoto() } // Сделать фото

        binding?.btnLoadPhoto?.setOnClickListener { uploadImageToServer() } // Отправить на сервер
        if (sharedModel.textFullAddress != "") { // Пока нет полного аддреса кнопку не видно
            binding?.btnLoadPhoto?.isVisible = true
        } else binding?.btnLoadPhoto?.isInvisible = true

        binding?.btnAddAddress?.setOnClickListener {
            findNavController().navigate(R.id.action_photoFragment_to_districtListFragment)
        }

        if (sharedModel.photoLiveData.value != null) { // Пока нет фото кнопку не видно
            binding?.btnAddAddress?.isVisible = true
        } else binding?.btnAddAddress?.isInvisible = true

       binding?.textViewFullAddress?.text = sharedModel.textFullAddress
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
            binding?.ivPhoto?.setImageBitmap(it) // Выставляет фотку в imageView
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

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}



