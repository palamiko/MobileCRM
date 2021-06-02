package android.bignerdranch.mobilecrm.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.model.networkModel.UserToken
import android.bignerdranch.mobilecrm.model.otherModel.User
import android.bignerdranch.mobilecrm.network.NetworkApiService
import android.bignerdranch.mobilecrm.network.NetworkModule
import android.bignerdranch.mobilecrm.ui.activity.MainActivity
import android.bignerdranch.mobilecrm.utilits.helpers.KEY_USER_DATA
import android.bignerdranch.mobilecrm.utilits.helpers.SHARED_PREF_NAME
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json


class PushService : FirebaseMessagingService() {

    private fun createCoroutineScope() = CoroutineScope(Job() + Dispatchers.IO)


    @ExperimentalSerializationApi
    override fun onNewToken(newToken: String) {
        val coroutineScope = createCoroutineScope()

        val user = getUserFromSharedPref()
        if (user != null) coroutineScope.launch {
            sendTokenToServer(user.id.toString(), user.login, newToken)
            Log.e(TAG, "Токен изменился и отправлен")
        }
    }

    private fun getUserFromSharedPref(): User? {
        var user: User? = null
        // Получаем настройки сохраненые в Authorization
        val sharedPref = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)

        val userDataString = sharedPref.getString(KEY_USER_DATA, null)
        if (userDataString != null) {
            user = Json.decodeFromString(User.serializer(), userDataString)
        }
        return user
    }

    @ExperimentalSerializationApi
    private suspend fun sendTokenToServer(id_master: String, login_master: String, token: String) =
        withContext(Dispatchers.IO) {
            /** Отправляет данные пользователя на сервер СРМ */

            val networkApi: NetworkApiService = NetworkModule().networkApiService
            networkApi.sendTokenFirebase(UserToken(id_master, login_master, token))
        }


    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.name_channel_notify)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_new_task)
            .setContentTitle("Добавлена заявка")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {

        private const val TAG = "PushService"
    }
}

