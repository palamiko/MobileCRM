package android.bignerdranch.mobilecrm.service

import kotlinx.coroutines.*

/**
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private fun createCoroutineScope() = CoroutineScope(Job() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")


        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")


            handleNow()

        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */


    @ExperimentalSerializationApi
    override fun onNewToken(token: String) {
        val coroutineScope = createCoroutineScope()

        val user = getUserFromSharedPref()
        if (user != null) coroutineScope.launch {
            sendTokenToServer(user.id.toString(), user.login, token)
        }
    }


    /**
     * Schedule async work using WorkManager.

    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }
     */
    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */

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
            networkApi.sendTokenFirebase(id_master, login_master, token)
        }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
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

        private const val TAG = "MyFirebaseMsgService"
    }



}

 */