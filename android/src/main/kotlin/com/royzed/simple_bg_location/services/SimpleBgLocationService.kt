package com.royzed.simple_bg_location.services

import android.app.*
import android.content.ComponentName
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.MainThread
import com.royzed.simple_bg_location.SimpleBgLocationModule
import com.royzed.simple_bg_location.data.Position
import com.royzed.simple_bg_location.domain.ForegroundNotification
import com.royzed.simple_bg_location.domain.ForegroundNotificationConfig
import com.royzed.simple_bg_location.domain.RequestOptions
import com.royzed.simple_bg_location.domain.State
import com.royzed.simple_bg_location.domain.location.PositionChangedCallback
import com.royzed.simple_bg_location.domain.location.SimpleBgLocationManager
import com.royzed.simple_bg_location.errors.ErrorCallback
import com.royzed.simple_bg_location.errors.ErrorCodes
import com.royzed.simple_bg_location.utils.distanceToString
import io.flutter.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.min

class SimpleBgLocationService : Service() {
    private val localBinder: LocalBinder = LocalBinder()

    private var isConfigChanged = false
    private var serviceRunningInForeground = false
    private var _isTracking = false
    val isTracking: Boolean get() = _isTracking

    private val positions: MutableList<Position> = mutableListOf()
    private var distance: Double = 0.0
    private var elapsedSeconds: Long = 0
    private var timer: Timer? = null


    private lateinit var notification: ForegroundNotification

    private lateinit var requestOptions: RequestOptions

    private lateinit var locationManager: SimpleBgLocationManager

    private val positionCallback: PositionChangedCallback = { location ->

        if (location != null) {
            val position = Position.fromLocation(location)
            positions.add(position)
            accumulateDistance()
            SimpleBgLocationModule.getInstance().dispatchPositionEvent(position)
            if (notification.config.textHasTemplateTag()) {
                replaceNotifyByTemplate()
            }
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
            Log.d(TAG,"service will start: service: $this")
            startForeground(notification.id, notification.build())
            locationManager.startPositionUpdate(requestOptions, positionCallback, errorCallback)
        } else if (action.contains(ForegroundNotification.ACTION_PREFIX)) {
            val actionId = ForegroundNotification.extractActionId(action)
            SimpleBgLocationModule.getInstance().dispatchNotificationActionEvent(actionId)
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
        Log.d(TAG,"requestPositionUpdate this time service is: $this ")
        return if (!_isTracking) {
            requestOptions = options
            notification = ForegroundNotification(applicationContext, requestOptions.notificationConfig)

            val startIntent = Intent(applicationContext, SimpleBgLocationService::class.java).apply {
                action = ACTION_START
            }
            startService(startIntent)
            if (notification.config.textHasElapsedTemplateTag()) {
                timer = timer(period = 1000)  {

                    runBlocking(Dispatchers.Main) {
                        elapsedSeconds++
                        replaceNotifyByTemplate()
                    }

                }
            }


            _isTracking = true
            true
        } else {
            false
        }

    }

    fun stopPositionUpdate() {
        _isTracking = false
        positions.clear()
        distance = 0.0
        elapsedSeconds = 0
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        locationManager.stopPositionUpdate()
        Log.d(TAG, "Position Update Stopped.")
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            stopForeground(true)
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        stopSelf()
    }

    fun getState(): State {
        val state = State()

        state.isTracking = isTracking
        if (isTracking) {
            state.positions = positions
            state.requestOptions = requestOptions
        }

        return state
    }

    private fun accumulateDistance() {
        if (positions.size < 2) {
            return
        }
        val length = positions.size
        val result: FloatArray = FloatArray(3)
        Location.distanceBetween(positions[length-2].latitude, positions[length-2].longitude,
        positions[length-1].latitude, positions[length-1].longitude, result)

        distance += result[0].toDouble()

    }
    @MainThread
    private fun replaceNotifyByTemplate() {
        if (notification.config.textHasTemplateTag()) {
            var newText = notification.config.text.replace(ForegroundNotificationConfig.distance_tag, distanceToString(distance), true)
            if (timer != null) {
                newText = newText.replace(ForegroundNotificationConfig.elapsed_tag, formatElapsedTime(), true)
            }
            val newConfig = notification.config.copy(text = newText)
            notification.updateNotification(newConfig, true)
        }
    }


    private fun formatElapsedTime(): String {
        val seconds: Long = elapsedSeconds % 60
        val minutes: Long = (elapsedSeconds / 60) % 60
        val hours: Long = elapsedSeconds / 3600

        return (String.format("%02d:%02d:%02d", hours, minutes, seconds))
    }

    inner class LocalBinder : Binder() {
        internal val service: SimpleBgLocationService
            get() = this@SimpleBgLocationService
    }
    companion object {
        private const val TAG = "SimpleBgLocationService"

        const val ACTION_START = "Start"
        const val PACKAGE_NAME = "com.royzed.simple_bg_location"
        const val EXTRA_CANCEL_LOCATION_UPDATE_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.cancel_location_update_from_notification"


    }
}