package com.mrntlu.notificationguide.ui.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mrntlu.notificationguide.MainActivity
import com.mrntlu.notificationguide.R
import com.mrntlu.notificationguide.databinding.FragmentNotificationsBinding
import com.mrntlu.notificationguide.service.NotificationActionBroadcastReceiver
import com.mrntlu.notificationguide.service.NotificationActionBroadcastReceiver.Companion.FIRST_ACTION
import com.mrntlu.notificationguide.service.NotificationActionBroadcastReceiver.Companion.FIRST_ACTION_EXTRA
import com.mrntlu.notificationguide.utils.setNotification

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val notificationManager by lazy { context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            sendLocalTestNotificationWithRedirect()
        }

        binding.button2.setOnClickListener {
            sendLocalTestNotificationWithAction()
        }

        binding.button3.setOnClickListener {
            cancelNotificationById(1)
        }

        binding.button4.setOnClickListener {
            cancelNotificationById(2)
        }
    }

    private fun sendLocalTestNotificationWithRedirect() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP

        intent.putExtra("redirect", true)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification = context?.setNotification(
            getString(R.string.notification_channel_id),
            "Local Notification Ch 1",
            "Notification with redirection",
            defaultSoundUri,
            null,
            pendingIntent
        )

        notificationManager.notify(1, notification?.build())
    }


    /**
     * A notification can offer up to three action buttons that allow the user to respond quickly.
     * These action buttons should not duplicate the action performed when the user
     */
    private fun sendLocalTestNotificationWithAction() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP

        intent.putExtra("redirect", true)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val actionIntent = Intent(context, NotificationActionBroadcastReceiver::class.java).apply {
            action = FIRST_ACTION
            putExtra(FIRST_ACTION_EXTRA, "Action Extra")
        }
        val actionPendingIntent = PendingIntent.getBroadcast(
            context, 0, actionIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification = context?.setNotification(
            getString(R.string.notification_channel_id),
            "Local Notification Ch 2",
            "Notification with action",
            defaultSoundUri,
            null,
            pendingIntent
        )

        notification?.apply {
            addAction(R.drawable.ic_dashboard_black_24dp, "Action", actionPendingIntent)
        }

        notificationManager.notify(2, notification?.build())
    }

    private fun cancelNotificationById(id: Int) {
        notificationManager.cancel(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}