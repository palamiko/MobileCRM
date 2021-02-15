package android.bignerdranch.photounit.fragments.photo

import android.Manifest
import android.app.Activity
import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.databinding.FragmentPhotoBinding
import android.bignerdranch.photounit.fragments.BaseFragment
import android.bignerdranch.photounit.model.Photo
import android.bignerdranch.photounit.utilits.UploadUtility
import android.bignerdranch.photounit.viewModels.SharedViewModel
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.drjacky.imagepicker.ImagePicker
import java.io.File
import java.util.*


class PhotoFragment : BaseFragment(R.layout.fragment_photo) {

    private val sharedModel: SharedViewModel by activityViewModels()
    private var binding: FragmentPhotoBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentPhotoBinding.bind(view)
        binding = fragmentBinding

        setLiveDataObserve()
        requestPermissions()
        bindAllView()
    }

    override fun onResume() {
        super.onResume()
        detectButtonVisible()
    }

    private fun getPhoto() {
    /**Функция делает фото путем открытия другого активити и возвращает результат сюда.*/

        ImagePicker.with(this)
            .compress(1024)         //Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
            .start { resultCode, data ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val fileUri = data?.data
                        val file: File? = ImagePicker.getFile(data)
                        val filePath: String? = ImagePicker.getFilePath(data)
                        val myPhoto = Photo(fileUri, file, filePath)

                        sharedModel.setPhoto(myPhoto) // Помещаем клас Photo в ViewModel
                        sharedModel.setPhotoJpeg(myPhoto) // Нужно для отображения на экране в ImageView
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
        binding?.apply {
            btnGetPhoto.setOnClickListener { getPhoto() } // Сделать фото
            btnLoadPhoto.setOnClickListener { uploadImageToServer() } // Отправить на сервер
            btnAddAddress.setOnClickListener {
                findNavController().navigate(R.id.action_photoFragment_to_districtListFragment)
            }
            textViewFullAddress.text = sharedModel.textFullAddress
        }
    }

    private fun detectButtonVisible() {
        if (sharedModel.textFullAddress != "") { // Пока нет полного аддреса кнопку не видно
            binding?.btnLoadPhoto?.isVisible = true
        } else binding?.btnLoadPhoto?.isInvisible = true

        if (sharedModel.photoLiveData.value != null) { // Пока нет фото кнопку не видно
            binding?.btnAddAddress?.isVisible = true
        } else binding?.btnAddAddress?.isInvisible = true
    }

    private fun uploadImageToServer() {
        val photo: Photo? = sharedModel.getPhoto()
        if (photo != null) {
            //File(sharedModel.currentPhotoPath.absolutePath).writeBytes(photo) // Создаем фаил фото из ByteArray

            UploadUtility(requireActivity(), sharedModel.mHome.id.toString())
                .uploadFile(photo.filePath!!) // Отправляем фото в POST
        } else {
            Toast.makeText(requireContext(), "Сделай фото", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLiveDataObserve() {
        sharedModel.photoLiveData.observe(viewLifecycleOwner, {
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

    override fun onDestroyView() {
        sharedModel.photoLiveData.removeObservers(this)
        binding = null
        super.onDestroyView()
    }
}
