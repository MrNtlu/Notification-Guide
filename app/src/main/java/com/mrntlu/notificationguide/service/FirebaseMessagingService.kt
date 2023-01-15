package com.mrntlu.notificationguide.service

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mrntlu.notificationguide.MainActivity
import com.mrntlu.notificationguide.R

class FirebaseMessagingService: FirebaseMessagingService() {

    private val channelName = "Test Notification"
    private val groupName = "Test Group Notification"
    private val groupID = "test.notification"

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

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("Test", "Remote Messages: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Test", "Message data payload: ${remoteMessage.data}")

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                //scheduleJob()
            } else {
                // Handle message within 10 seconds
                //handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("Test", "Message Notification Body: ${it.body}")
            sendNotification(it.title, it.body, remoteMessage.data)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(
        title: String?,
        messageBody: String?,
        data: Map<String, String>
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        for (i in 0 until data.size) {
            val key = data.keys.toList()[i]
            val value = data.values.toList()[i]
            intent.putExtra(key, value)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        //Create Channel
        // Since android Oreo notification channel is needed.
        val channelId = getString(R.string.notification_channel_id)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannelGroup(NotificationChannelGroup(groupID, groupName))
        val channel = setChannel(channelId, defaultSoundUri)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_test)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_stat_test))
            .setColor(ContextCompat.getColor(applicationContext, R.color.notification))
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setGroup(groupID)
            .setContentIntent(pendingIntent)
            .build()

        val groupNotification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_test)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_stat_test))
            .setColor(ContextCompat.getColor(applicationContext, R.color.notification))
            .setStyle(
                NotificationCompat.InboxStyle()
                    .addLine("$title $messageBody")
                    .setBigContentTitle("New Notifications")
                    .setSummaryText("Notifications Grouped")
            )
            .setGroup(groupID)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .setGroupSummary(true)
            .build()


        //ID of notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        notificationManager.notify(0, groupNotification)
    }

    private fun setChannel(channelId: String, defaultSoundUri: Uri): NotificationChannel {
        val attributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        channel.apply {
            enableLights(true)
            enableVibration(true)
            setShowBadge(true)
            setSound(defaultSoundUri, attributes)
            group = groupID
        }

        return channel
    }
}