package com.mrntlu.notificationguide

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.mrntlu.notificationguide.databinding.ActivityMainBinding
import com.mrntlu.notificationguide.service.FirebaseMessagingService.Companion.DATA_EXTRA
import com.mrntlu.notificationguide.service.FirebaseMessagingService.Companion.PATH_EXTRA

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val navController by lazy { findNavController(R.id.nav_host_fragment_activity_main) }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            Log.d("Test", "Granted")
        } else {
            // TODO: Inform user that that your app will not show notifications.
            Log.d("Test", "Failed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            navView.visibility = if (destination.id == R.id.navigation_dashboard) View.GONE else View.VISIBLE
        }

        val extras = intent.extras
        if (extras != null) {
            val data = extras.getString(DATA_EXTRA)

            when(extras.getString(PATH_EXTRA)) {
                "dashboard" -> {
                    navController.navigate(R.id.action_global_navigation_dashboard, bundleOf(
                        "data" to data
                    ))
                }
            }
        }

        getFCMToken()
        askNotificationPermission()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.d("Test", "onNewIntent: ${intent?.extras?.getString(PATH_EXTRA)} ${intent?.getStringExtra(PATH_EXTRA)} ${intent?.getStringExtra(DATA_EXTRA)}")

        val extras = intent?.extras
        if (extras != null) {
            val data = extras.getString(DATA_EXTRA)

            when(extras.getString(PATH_EXTRA)) {
                "dashboard" -> {
                    navController.navigate(R.id.action_global_navigation_dashboard, bundleOf(
                        "data" to data
                    ))
                }
            }
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("Test", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("Test", "Token is $token")
        })
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }
}