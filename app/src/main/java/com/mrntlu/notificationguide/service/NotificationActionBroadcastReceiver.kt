package com.mrntlu.notificationguide.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationActionBroadcastReceiver: BroadcastReceiver() {
    companion object {
        const val FIRST_ACTION = "first"
        const val FIRST_ACTION_EXTRA = "first_extra"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Test", "onReceive: ${intent?.action}")
        intent?.extras?.let {
            Log.d("Test", "onReceiveExtra: ${it.getString(FIRST_ACTION_EXTRA)}")
        }
    }
}