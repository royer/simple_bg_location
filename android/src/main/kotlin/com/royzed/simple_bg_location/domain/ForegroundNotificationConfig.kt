package com.royzed.simple_bg_location.domain

import android.content.Context
import com.royzed.simple_bg_location.utils.getAppName

data class ForegroundNotificationConfig(

    /**
     * name of custom layout xml file name
     */
    val layout: String = "",
    val title: String = "",
    val text: String = "",
    val smallIcon: AndroidResource = AndroidResource("mipmap/ic_launcher",""),
    val largeIcon: AndroidResource = AndroidResource(),

    /** The priority of notification
     *
     * The following `priority` values defined as static constants:
     *
     * ```
     * | Value                                | Description                                                                                             |
     * |--------------------------------------|---------------------------------------------------------------------------------------------------------|
     * | [NOTIFICATION_PRIORITY_DEFAULT]      | Notification weighted to top of list; notification-bar icon weighted left                               |
     * | [NOTIFICATION_PRIORITY_HIGH]         | Notification **strongly** weighted to top of list; notification-bar icon **strongly** weighted to left  |
     * | [NOTIFICATION_PRIORITY_LOW]          | Notification weighted to bottom of list; notification-bar icon weighted right                           |
     * | [NOTIFICATION_PRIORITY_MAX]          | Same as `NOTIFICATION_PRIORITY_HIGH`                                                                    |
     * | [NOTIFICATION_PRIORITY_MIN]          | Notification **strongly** weighted to bottom of list; notification-bar icon **hidden**                  |
     * ```
     */
    val priority: Int = NOTIFICATION_PRIORITY_DEFAULT,

    /**
     * Defaults to application's name from **AndroidManifest**
     */
    val channelName: String = "",

    /**
     * custom button ids
     */
    val actions: List<String> = emptyList(),

    val enableWifiLock: Boolean = false,

    val enableWakeLock: Boolean = false,
    ) {

    fun toMap(): Map<String, Any> {
        val map : MutableMap<String, Any> = mutableMapOf()
        map["layout"] = layout
        map["title"] = title
        map["text"] = text
        map["smallIcon"] = smallIcon.toMap()
        map["largeIcon"] = largeIcon.toMap()
        map["priority"] = priority
        map["channelName"] = channelName
        map["actions"] = actions
        map["enableWifiLock"] = enableWifiLock
        map["enableWakeLock"] = enableWakeLock

        return map.toMap()
    }

    companion object {
        const val NOTIFICATION_PRIORITY_DEFAULT: Int = 0;
        const val NOTIFICATION_PRIORITY_HIGH: Int = 1;
        const val NOTIFICATION_PRIORITY_LOW: Int = -1;
        const val NOTIFICATION_PRIORITY_MAX: Int = 2;
        const val NOTIFICATION_PRIORITY_MIN: Int = -2;

        const val DEFAULT_TEXT = "Location service activated"
        const val DEFAULT_CHANNEL_NAME = "Position Update"

        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun fromMap(context: Context, map: Map<String, Any?>?): ForegroundNotificationConfig {
            if (map == null)
                return makeDefaultConfig(context)
            val smallIcon = map["smallIcon"]?.let {
                AndroidResource.fromMap(it as Map<String, Any?>)
            } ?: AndroidResource.defaultAppIcon
            val largeIcon = map["largeIcon"]?.let {
                AndroidResource.fromMap(it as Map<String, Any?>)
            } ?: AndroidResource()

            return ForegroundNotificationConfig(
                layout = (map["layout"] as? String) ?: "",
                title = map["title"] as? String ?: getAppName(context),
                text = map["text"] as? String ?: DEFAULT_TEXT,
                smallIcon = smallIcon,
                largeIcon = largeIcon,
                priority = map["priority"] as? Int ?: NOTIFICATION_PRIORITY_DEFAULT,
                channelName = map["channelName"] as? String ?: DEFAULT_CHANNEL_NAME,
                actions = map["actions"] as? List<String> ?: emptyList(),
                enableWifiLock = map["enableWifiLock"] as? Boolean ?: false,
                enableWakeLock = map["enableWakeLock"] as? Boolean ?: false
            )
        }

        @JvmStatic
        fun makeDefaultConfig(context: Context): ForegroundNotificationConfig {
            val appName = getAppName(context)
            return ForegroundNotificationConfig(
                layout = "",
                title = appName,
                text = DEFAULT_TEXT,
                smallIcon = AndroidResource.defaultAppIcon,
                largeIcon = AndroidResource(),
                priority = NOTIFICATION_PRIORITY_DEFAULT,
                channelName = DEFAULT_CHANNEL_NAME,
                actions = emptyList(),
                enableWifiLock =  false,
                enableWakeLock = false
            )
        }

    }

}