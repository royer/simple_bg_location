package com.royzed.simple_bg_location.domain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.royzed.simple_bg_location.R
import com.royzed.simple_bg_location.services.SimpleBgLocationService
import io.flutter.Log

class ForegroundNotification(
    private val context: Context,
    private var _config: ForegroundNotificationConfig,

    ) {

    val id: Int get() = _config.notificationId
    val config: ForegroundNotificationConfig get() = _config

    private val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, _config.channelId)
    init {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = when(_config.priority) {
                NotificationCompat.PRIORITY_HIGH -> NotificationManager.IMPORTANCE_HIGH
                NotificationCompat.PRIORITY_LOW -> NotificationManager.IMPORTANCE_LOW
                NotificationCompat.PRIORITY_MAX -> NotificationManager.IMPORTANCE_MAX
                else -> NotificationManager.IMPORTANCE_DEFAULT
            }
            val channel = NotificationChannel(_config.channelId, _config.channelName, importance)
            channel.description = _config.channelDescription
            channel.enableLights(true)
            channel.enableVibration(true)
            // Register the channel with the system
            // It's safe to call this repeatedly because creating an existing notification
            // channel performs no operation.
            val manager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        updateNotification(_config,false)
    }

    private fun buildBringToFrontIntent(): PendingIntent? {
        return context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            `package` = null
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }.run {
            var flags = PendingIntent.FLAG_UPDATE_CURRENT
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                flags = flags or PendingIntent.FLAG_IMMUTABLE
            }
            PendingIntent.getActivity(context, 0, this, flags)
        }
    }

    private fun buildCancelIntent(): PendingIntent {
        val cancelIntent = Intent(context, SimpleBgLocationService::class.java)
        cancelIntent.putExtra(SimpleBgLocationService.EXTRA_CANCEL_LOCATION_UPDATE_FROM_NOTIFICATION, true)
        var flags = 0
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            flags = flags or PendingIntent.FLAG_IMMUTABLE
        }
        return PendingIntent.getService(context, 0, cancelIntent, flags)
    }

    private fun getIcon(name: String, defType: String): Int {
        var iconId = context.resources.getIdentifier(name, defType, context.packageName)
        if (iconId == 0) {
            Log.w(TAG,"Can not get icon(name:${name}, defType: ${defType}) from resource.")
            iconId = context.resources.getIdentifier("ic_launcher", "mipmap", context.packageName)
            if (iconId == 0) {
                Log.e(TAG,"Can not get launcher icon. Notification must have icon to work correct. please check android/app/res directory")
            }
        }

        return iconId

    }

    private fun getBitmap(name: String, defType: String): Bitmap? {
        val id = context.resources.getIdentifier(name, defType, context.packageName)
        if (id == 0) {
            return null
        }
        return BitmapFactory.decodeResource(context.resources, id)
    }

    private fun buildActionIntent(actionId: String): PendingIntent {
        val actionIntent = Intent(context, SimpleBgLocationService::class.java).apply {
            action = makeActionName(actionId)
        }
        var flags = 0
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            flags = flags or PendingIntent.FLAG_IMMUTABLE
        }
        return PendingIntent.getService(context, 0, actionIntent, flags)

    }

    fun updateNotification(newConfig: ForegroundNotificationConfig , notify: Boolean) {
        val iconId = getIcon(newConfig.smallIcon.name, newConfig.smallIcon.defType)
        val largeIcon: Bitmap? = getBitmap(newConfig.largeIcon.name, newConfig.largeIcon.defType)

        var replacedText = newConfig.text.replace(ForegroundNotificationConfig.distance_tag, "0m", true)
        replacedText = replacedText.replace(ForegroundNotificationConfig.elapsed_tag, "00:00:00", true)
        builder.apply {
            setContentTitle(newConfig.title)
            setContentText(replacedText)
            setSmallIcon(iconId)
            if (largeIcon != null)
                setLargeIcon(largeIcon)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setOngoing(true)
            priority = newConfig.priority
            setStyle(NotificationCompat.BigTextStyle().bigText(replacedText))
            setContentIntent(buildBringToFrontIntent())

//            addAction(
//                R.drawable.ic_simple_bg_location_cancel,
//                context.getString(R.string.simple_bg_location_cancel_text),
//                buildCancelIntent()
//            )

            for(actionName in newConfig.actions) {
                if (actionName.equals("cancel", true)) {
                    addAction(
                        R.drawable.ic_simple_bg_location_cancel,
                        actionName,
                        buildCancelIntent()
                    )

                } else {
                    addAction(
                        0,
                        actionName,
                        buildActionIntent(actionName)
                    )
                }
            }
        }

        if (notify) {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(_config.notificationId, builder.build())
        }
    }

    /// accepct new config, ignore notificationId, channelId and channelName
    fun updateConfig(config: ForegroundNotificationConfig, visible: Boolean) {
        this._config = this._config.copy(
            layout = config.layout,
            title = config.title,
            text = config.text,
            smallIcon = config.smallIcon,
            largeIcon = config.largeIcon,
            priority = config.priority,
            actions = config.actions
        )
        updateNotification(_config, visible)
    }

    fun build(): Notification {
        return builder.build()
    }

    companion object {

        private const val TAG = "ForegroundNotification"
        const val ACTION_PREFIX = "com.royzed.simple_bg_location/custom_action/"

        fun makeActionName(actionId: String): String {
            return "$ACTION_PREFIX$actionId"
        }

        fun extractActionId(fullActionName: String): String {
            return fullActionName.removePrefix(ACTION_PREFIX)
        }

    }
}