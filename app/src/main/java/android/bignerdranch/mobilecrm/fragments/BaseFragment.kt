package android.bignerdranch.mobilecrm.fragments

import android.system.ErrnoException
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import kotlinx.serialization.SerializationException
import okio.IOException
import retrofit2.HttpException
import java.net.ConnectException


open class BaseFragment(layout: Int) : Fragment(layout) {

    var coroutineScope = createCoroutineScope()

    private fun createCoroutineScope() = CoroutineScope(Job() + Dispatchers.IO)

    fun getText(editText: EditText): String = editText.text.toString()

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(
            "WS02SolutionFragment",
            "Coroutine exception, scope active:${coroutineScope.isActive}",
            throwable
        )
        coroutineScope = createCoroutineScope()

        val errorTextId = when (throwable) {
            is ConnectException, is ErrnoException -> "Нет интернета"
            is IOException, is HttpException -> "Ошибка интернет соединения"
            is SerializationException -> "Ошибка сериализации"
            else -> "Неизвестная ошибка"
        }

        Log.d("Coroutine Fail", errorTextId)
        //Toast.makeText(requireContext(), errorTextId, Toast.LENGTH_SHORT).show()
    }

    fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDetach() {
        coroutineScope.cancel("It's time")

        super.onDetach()
    }

    companion object {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->

            val errorTextId = when (throwable) {
                is ConnectException, is ErrnoException -> "Нет интернета"
                is IOException, is HttpException -> "Ошибка интернет соединения"
                is SerializationException -> "Ошибка сериализации"
                else -> "Неизвестная ошибка"
            }

            Log.d("Coroutine Fail", errorTextId)
            //Toast.makeText(requireContext(), errorTextId, Toast.LENGTH_SHORT).show()
        }
    }

}