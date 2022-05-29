package com.royzed.simple_bg_location.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.royzed.simple_bg_location.R
import com.royzed.simple_bg_location.services.SimpleBgLocationService
import io.flutter.Log

class ForegroundNotification(
    private val context: Context,
    private val notficationId: Int,
    channelId: String,
    private val notificationConfig: ForegroundNotificationConfig,

) {

    private val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
    init {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, notificationConfig.channelName, importance)
                .apply {
                    description = notificationConfig.channelDescription
                }

            // Register the channel with the system
            // It's safe to call this repeatedly because creating an existing notification
            // channel performs no operation.
            val manager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        updateNotification(notificationConfig, false)
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

    private fun updateNotification(config: ForegroundNotificationConfig, notify: Boolean) {
        val iconId = getIcon(config.icon.name, config.icon.defType)


        builder.apply {
            setContentText(config.text)
            setSmallIcon(iconId)
            setContentTitle(config.title)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setOngoing(true)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setStyle(NotificationCompat.BigTextStyle().bigText(config.text))
            setContentIntent(buildBringToFrontIntent())
            addAction(
                R.drawable.ic_simple_bg_location_cancel,
                context.getString(R.string.simple_bg_location_cancel_text),
                buildCancelIntent()
            )
        }

        if (notify) {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notficationId, builder.build())
        }
    }

    fun updateConfig(config: ForegroundNotificationConfig, visible: Boolean) {
        updateNotification(config, visible)
    }

    fun build(): Notification {
        return builder.build()
    }

    companion object {

        private const val TAG = "ForegroundNotification"

    }
}