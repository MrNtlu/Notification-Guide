package com.mrntlu.notificationguide.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.RemoteInput
import com.mrntlu.notificationguide.R
import com.mrntlu.notificationguide.utils.setNotification

class NotificationActionBroadcastReceiver: BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID_EXTRA = "channel_id"

        const val FIRST_ACTION = "first"
        const val FIRST_ACTION_EXTRA = "first_extra"

        const val TEXT_ACTION = "text"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val channelId = intent?.extras?.getInt(CHANNEL_ID_EXTRA) ?: 0
        val firstActionExtra = intent?.extras?.getString(FIRST_ACTION_EXTRA)

        when(intent?.action) {
            FIRST_ACTION -> {
                if (firstActionExtra != null) {
                    context?.let {
                        val notificationManager = it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                        val pendingIntent = PendingIntent.getActivity(
                            it, 0, intent,
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        val newNotification = it.setNotification(
                            it.getString(R.string.notification_channel_id),
                            "Action",
                            "Data is, $firstActionExtra",
                            null, null, pendingIntent
                        )

                        notificationManager.notify(channelId, newNotification.build())
                    }
                }
            }
            TEXT_ACTION -> {
                if (getMessageText(intent) != null) {
                    context?.let {
                        val notificationManager = it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                        val pendingIntent = PendingIntent.getActivity(
                            it, 0, intent,
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        val newNotification = it.setNotification(
                            it.getString(R.string.notification_channel_id),
                            "Success, message sent!",
                            getMessageText(intent).toString(),
                            null, null, pendingIntent
                        )

                        notificationManager.notify(channelId, newNotification.build())
                    }
                }
            }
        }
    }

    private fun getMessageText(intent: Intent): CharSequence? {
        return RemoteInput.getResultsFromIntent(intent)?.getCharSequence(TEXT_ACTION)
    }
}