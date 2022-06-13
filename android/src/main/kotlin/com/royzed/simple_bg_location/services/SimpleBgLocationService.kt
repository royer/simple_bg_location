package com.royzed.simple_bg_location.services

import android.app.*
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.content.res.Configuration
import android.os.Binder
import android.os.IBinder
import com.royzed.simple_bg_location.SimpleBgLocationModule
import com.royzed.simple_bg_location.data.Position
import com.royzed.simple_bg_location.domain.ForegroundNotification
import com.royzed.simple_bg_location.domain.RequestOptions
import com.royzed.simple_bg_location.domain.State
import com.royzed.simple_bg_location.domain.location.PositionChangedCallback
import com.royzed.simple_bg_location.domain.location.SimpleBgLocationManager
import com.royzed.simple_bg_location.errors.ErrorCallback
import com.royzed.simple_bg_location.errors.ErrorCodes
import io.flutter.Log

class SimpleBgLocationService : Service() {
    private val localBinder: LocalBinder = LocalBinder()

    private var isConfigChanged = false
    private var serviceRunningInForeground = false
    private var _isTracking = false
    val isTracking: Boolean get() = _isTracking

    private val positions: MutableList<Position> = mutableListOf()


    private lateinit var notification: ForegroundNotification

    private lateinit var requestOptions: RequestOptions

    private lateinit var locationManager: SimpleBgLocationManager

    private val positionCallback: PositionChangedCallback = { location ->

        if (location != null) {
            val position = Position.fromLocation(location)
            positions.add(position)
            SimpleBgLocationModule.getInstance().dispatchPositionEvent(position)
        } else {
            Log.w(TAG,"got location is null. do nothing!")

        }

    }
    private val errorCallback: ErrorCallback = {
        SimpleBgLocationModule.getInstance().dispatchPositionErrorEvent(it)
        Log.d(TAG,"Position Update hit error: code: $it")
        stopPositionUpdate()
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"Service created.")

        locationManager = SimpleBgLocationManager(applicationContext)

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action?:"<null>"
        Log.d(TAG, "Service onStartCommand(intent:$intent, Intent.action: $action, flags: $flags, startId: $startId)")
        val comp = ComponentName(this.applicationContext, SimpleBgLocationService::class.java)
        val cancelFromNotification = intent?.getBooleanExtra(
            EXTRA_CANCEL_LOCATION_UPDATE_FROM_NOTIFICATION, false) ?: false
        if (cancelFromNotification) {
            stopPositionUpdate()
            SimpleBgLocationModule.getInstance().dispatchPositionErrorEvent(ErrorCodes.canceled)
        } else if (action == ACTION_START && intent?.component == comp) {
            startForeground(NOTIFICATION_ID, notification.build(), FOREGROUND_SERVICE_TYPE_LOCATION)
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {

        Log.d(TAG,"Service onBind()")
        //stopForeground(true)
        serviceRunningInForeground = false
        isConfigChanged = false
        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "Service onRebind()")
        //stopForeground(true)
        serviceRunningInForeground = false
        isConfigChanged = false
        super.onRebind(intent)
    }


    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG,"Service onUnbind()")
        if (!isConfigChanged && _isTracking) {
            //startForeground(NOTIFICATION_ID, notification.build(), FOREGROUND_SERVICE_TYPE_LOCATION)
            serviceRunningInForeground = true
        }
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG,"Service onConfigurationChanged()")
        isConfigChanged = true
        super.onConfigurationChanged(newConfig)
    }


    override fun onDestroy() {
        Log.d(TAG,"Service onDestroy()")
        super.onDestroy()
    }

    fun requestPositionUpdate(options: RequestOptions): Boolean {
        Log.d(TAG,"requestPositionUpdate ")
        return if (!_isTracking) {
            requestOptions = options
            notification = ForegroundNotification(applicationContext, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID, requestOptions.notificationConfig)

            val startIntent = Intent(applicationContext, SimpleBgLocationService::class.java).apply {
                action = ACTION_START
            }
            startService(startIntent)

            locationManager.startPositionUpdate(options, positionCallback, errorCallback)
            _isTracking = true
            true
        } else {
            false
        }

    }

    fun stopPositionUpdate() {
        _isTracking = false
        positions.clear()
        locationManager.stopPositionUpdate()
        Log.d(TAG, "Position Update Stopped.")
        stopForeground(true)
        stopSelf()
    }

    fun getState(): State {
        val state = State();

        state.isTracking = isTracking;
        if (isTracking) {
            state.positions = positions;
            state.requestOptions = requestOptions;
        }

        return state;
    }



    inner class LocalBinder : Binder() {
        internal val service: SimpleBgLocationService
            get() = this@SimpleBgLocationService
    }
    companion object {
        private const val TAG = "SimpleBgLocationService"

        const val ACTION_START = "Start"
        const val PACKAGE_NAME = "com.royzed.simple_bg_location"
        const val NOTIFICATION_ID = 373737
        const val NOTIFICATION_CHANNEL_ID = "simple_background_location_channel_01"
        const val EXTRA_CANCEL_LOCATION_UPDATE_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.cancel_location_update_from_notification"
    }
}