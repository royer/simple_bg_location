package com.royzed.simple_bg_location.domain

import android.content.Context
import com.royzed.simple_bg_location.utils.getAppName

data class ForegroundNotificationConfig(

    val notificationId: Int = DEFAULT_NOTIFICATION_ID,
    /**
     * default applicationName from manifest file.
     */
    val title: String = "",
    /**
     * default to "Location service activated"
     */
    val text: String = DEFAULT_TEXT,
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
     * ```
     */
    val priority: Int = NOTIFICATION_PRIORITY_DEFAULT,

    val channelId: String = DEFAULT_CHANNEL_ID,

    /**
     * Defaults to "Position Update"
     */
    val channelName: String = DEFAULT_CHANNEL_NAME,

    val channelDescription: String = DEFAULT_CHANNEL_DESCRIPTION,

    /**
     * custom button ids
     */
    val actions: List<String> = emptyList(),

    ) {

    fun toMap(): Map<String, Any> {
        val map : MutableMap<String, Any> = mutableMapOf()
        map["notificationId"] = notificationId
        map["title"] = title
        map["text"] = text
        map["smallIcon"] = smallIcon.toMap()
        map["largeIcon"] = largeIcon.toMap()
        map["priority"] = priority
        map["channelId"] = channelId
        map["channelName"] = channelName
        map["channelDescription"] = channelDescription
        map["actions"] = actions

        return map.toMap()
    }

    fun textHasTemplateTag(): Boolean {
        return text.contains(distance_tag, true)
                || text.contains(elapsed_tag, true)
    }

    fun textHasElapsedTemplateTag(): Boolean {
        return text.contains(elapsed_tag, true)
    }

    companion object {
        const val NOTIFICATION_PRIORITY_DEFAULT: Int = 0
        const val NOTIFICATION_PRIORITY_HIGH: Int = 1
        const val NOTIFICATION_PRIORITY_LOW: Int = -1
        const val NOTIFICATION_PRIORITY_MAX: Int = 2

        const val DEFAULT_NOTIFICATION_ID = 198964
        const val DEFAULT_CHANNEL_ID = "com.royzed.simple_bg_location.channel_1989.6.4"

        private const val DEFAULT_TEXT = "traced: {distance}  elapsed time: {elapsed}"
        private const val DEFAULT_CHANNEL_NAME = "Position Update"
        private const val DEFAULT_CHANNEL_DESCRIPTION = "Notify user location service is running."

        const val distance_tag = "{distance}"
        const val elapsed_tag = "{elapsed}"

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
                notificationId = (map["notificationId"] as? Int) ?: DEFAULT_NOTIFICATION_ID,
                title = map["title"] as? String ?: getAppName(context),
                text = map["text"] as? String ?: DEFAULT_TEXT,
                smallIcon = smallIcon,
                largeIcon = largeIcon,
                priority = map["priority"] as? Int ?: NOTIFICATION_PRIORITY_DEFAULT,
                channelId = map["channelId"] as? String ?: DEFAULT_CHANNEL_ID,
                channelName = map["channelName"] as? String ?: DEFAULT_CHANNEL_NAME,
                channelDescription = map["channelDescription"] as? String ?: DEFAULT_CHANNEL_DESCRIPTION,
                actions = map["actions"] as? List<String> ?: emptyList()
            )
        }

        @JvmStatic
        fun makeDefaultConfig(context: Context): ForegroundNotificationConfig {
            val appName = getAppName(context)
            return ForegroundNotificationConfig(
                notificationId = DEFAULT_NOTIFICATION_ID,
                title = appName,
                text = DEFAULT_TEXT,
                smallIcon = AndroidResource.defaultAppIcon,
                largeIcon = AndroidResource(),
                priority = NOTIFICATION_PRIORITY_DEFAULT,
                channelId = DEFAULT_CHANNEL_ID,
                channelName = DEFAULT_CHANNEL_NAME,
                channelDescription = DEFAULT_CHANNEL_DESCRIPTION,
                actions = emptyList()
            )
        }

    }

}