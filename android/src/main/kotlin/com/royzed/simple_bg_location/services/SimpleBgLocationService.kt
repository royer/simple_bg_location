package com.royzed.simple_bg_location.services

import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.IBinder
import com.royzed.simple_bg_location.domain.ForegroundNotification
import com.royzed.simple_bg_location.domain.ForegroundNotificationConfig
import io.flutter.Log

class SimpleBgLocationService : Service() {
    private val localBinder: LocalBinder = LocalBinder()

    private var isConfigChanged = false
    private var serviceRunningInForeground = false
    private var isTracking = false

    private lateinit var notification: ForegroundNotification

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"Service created.")

        notification = ForegroundNotification(
            applicationContext,
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_ID,
            ForegroundNotificationConfig("Tracking", "distance: 3.4km  Elapsed: 00:30:23")
        )
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action?:"<null>"
        Log.d(TAG, "onStartCommand(Intent.action: $action, flags: $flags, startId: $startId)")

        val cancelFromNotification = intent?.getBooleanExtra(
            EXTRA_CANCEL_LOCATION_UPDATE_FROM_NOTIFICATION, false) ?: false
        if (cancelFromNotification) {
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG,"onBind() $intent")
        stopForeground(true)
        serviceRunningInForeground = false
        isConfigChanged = false
        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "onRebind()")
        stopForeground(true)
        serviceRunningInForeground = false
        isConfigChanged = false
        super.onRebind(intent)
    }


    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG,"onUnbind()")
        if (!isConfigChanged && isTracking) {
            startForeground(NOTIFICATION_ID, notification.build())
            serviceRunningInForeground = true
        }
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG,"onConfigurationChanged()")
        isConfigChanged = true
        super.onConfigurationChanged(newConfig)
    }


    override fun onDestroy() {
        Log.d(TAG,"onDestroy()")
        super.onDestroy()
    }

    fun startLocationUpdate() {
        isTracking = true
        startService(Intent(applicationContext, SimpleBgLocationService::class.java))

    }

    fun stopLocationUpdate() {
        isTracking = false
        stopSelf()
    }



    inner class LocalBinder : Binder() {
        internal val service: SimpleBgLocationService
            get() = this@SimpleBgLocationService
    }
    companion object {
        private const val TAG = "SimpleBgLocationService"

        const val PACKAGE_NAME = "com.royzed.simple_bg_location"
        const val NOTIFICATION_ID = 373737
        const val NOTIFICATION_CHANNEL_ID = "simple_background_location_channel_01"
        const val EXTRA_CANCEL_LOCATION_UPDATE_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.cancel_location_update_from_notification"
    }
}