package com.mrntlu.notificationguide.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mrntlu.notificationguide.R

fun Context.setNotification(
    channelId: String,
    title: String?,
    body: String?,
    soundUri: Uri?,
    groupId: String?,
    pendingIntent: PendingIntent,
): NotificationCompat.Builder {
    val notification = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.ic_stat_test)
        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_stat_test))
        .setColor(ContextCompat.getColor(applicationContext, R.color.notification))
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true)
        .setSound(soundUri)
        .setGroupSummary(false)

    if (groupId != null)
        notification.setGroup(groupId)

    notification.setContentIntent(pendingIntent)

    return notification
}

fun Context.setGroupNotification(
    channelId: String,
    groupId: String,
    groupSummary: Boolean,
    lineText: String,
    bigContentTitle: String,
    summaryText: String,
): Notification = NotificationCompat.Builder(this, channelId)
    .setSmallIcon(R.drawable.ic_stat_test)
    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_stat_test))
    .setColor(ContextCompat.getColor(applicationContext, R.color.notification))
    .setStyle(
        NotificationCompat.InboxStyle()
            .addLine(lineText)
            .setBigContentTitle(bigContentTitle)
            .setSummaryText(summaryText)
    )
    .setGroup(groupId)
    .setGroupSummary(groupSummary)
    .setAutoCancel(true)
    .build()
