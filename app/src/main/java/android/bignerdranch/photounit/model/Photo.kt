package android.bignerdranch.photounit.model

import android.net.Uri
import java.io.File

data class Photo(
    val fileUri: Uri?,
    val file: File?,
    val filePath: String?
)
