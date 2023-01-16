package com.mrntlu.notificationguide

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import com.mrntlu.notificationguide.service.FirebaseMessagingService.Companion.CHANNEL_NAME
import com.mrntlu.notificationguide.service.FirebaseMessagingService.Companion.GROUP_ID
import com.mrntlu.notificationguide.service.FirebaseMessagingService.Companion.GROUP_NAME

class NotificationApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val channelId = getString(R.string.notification_channel_id)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannelGroup(NotificationChannelGroup(GROUP_ID, GROUP_NAME))
        val channel = setChannel(channelId, defaultSoundUri)
        notificationManager.createNotificationChannel(channel)
    }

    private fun setChannel(channelId: String, defaultSoundUri: Uri): NotificationChannel {
        val attributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val channel = NotificationChannel(
            channelId,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        channel.apply {
            enableLights(true)
            enableVibration(true)
            setShowBadge(true)
            setSound(defaultSoundUri, attributes)
            group = GROUP_ID
            description = "This is a test description message for notification."
        }

        return channel
    }
}