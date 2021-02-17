package android.bignerdranch.photounit.fragments

import android.util.Log
import android.widget.EditText
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import kotlinx.serialization.SerializationException
import okio.IOException
import retrofit2.HttpException


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
            is IOException, is HttpException -> "Ошибка интернет соединения"
            is SerializationException -> "Ошибка сериализации"
            else -> "Неизвестная ошибка"
        }
        Log.d("Coroutine Fail", errorTextId)
    }

    override fun onDetach() {
        coroutineScope.cancel("It's time")

        super.onDetach()
    }

}