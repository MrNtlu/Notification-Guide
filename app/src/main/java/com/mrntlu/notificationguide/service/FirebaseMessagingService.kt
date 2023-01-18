package com.mrntlu.notificationguide.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mrntlu.notificationguide.MainActivity
import com.mrntlu.notificationguide.R
import com.mrntlu.notificationguide.utils.setGroupNotification
import com.mrntlu.notificationguide.utils.setNotification

class FirebaseMessagingService: FirebaseMessagingService() {

    companion object {
        const val CHANNEL_NAME = "Test Notification"
        const val GROUP_NAME = "Test Group Notification"
        const val GROUP_ID = "test.notification"

        const val PATH_EXTRA = "path"
        const val DATA_EXTRA = "data"
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        handleNewToken(token)
    }

    // After you've obtained the token, you can send it to your app server and store it using your preferred method.
    private fun handleNewToken(token: String) {
        Log.d("Test", "handleNewToken: $token")
    }

    /**
     * Called when message is received.
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("Test", "Remote Messages: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Test", "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("Test", "Message Notification Body: ${it.body}")
            sendNotification(it.title, it.body, remoteMessage.data)
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * @param title FCM message title
     * @param messageBody FCM message body received.
     * @param data FCM message data
     */
    private fun sendNotification(
        title: String?,
        messageBody: String?,
        data: Map<String, String>
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        for (i in 0 until data.size) {
            val key = data.keys.toList()[i]
            val value = data.values.toList()[i]
            intent.putExtra(key, value)
        }

        // Notification tap action
        // Every notification should respond to a tap, usually to open an activity in your app that corresponds to the notification.
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val channelId = getString(R.string.notification_channel_id)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = setNotification(
            channelId,
            title,
            messageBody,
            defaultSoundUri,
            GROUP_ID,
            pendingIntent
        )

        val groupNotification = setGroupNotification(
            channelId,
            GROUP_ID,
            true,
            "$title $messageBody",
            "New Notifications",
            "Notifications Grouped"
        )

        //ID of notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification.build())
        notificationManager.notify(0, groupNotification)
    }
}