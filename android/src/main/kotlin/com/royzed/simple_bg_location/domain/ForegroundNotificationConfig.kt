package com.royzed.simple_bg_location.domain

class ForegroundNotificationConfig(

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


    companion object {
        const val NOTIFICATION_PRIORITY_DEFAULT: Int = 0;
        const val NOTIFICATION_PRIORITY_HIGH: Int = 1;
        const val NOTIFICATION_PRIORITY_LOW: Int = -1;
        const val NOTIFICATION_PRIORITY_MAX: Int = 2;
        const val NOTIFICATION_PRIORITY_MIN: Int = -2;

        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun fromMap(map: Map<String, Any?>?): ForegroundNotificationConfig {
            if (map == null)
                return ForegroundNotificationConfig()
            val smallIcon = map["smallIcon"]?.let {
                AndroidResource.fromMap(it as Map<String, Any?>)
            } ?: AndroidResource.defaultAppIcon
            val largeIcon = map["largeIcon"]?.let {
                AndroidResource.fromMap(it as Map<String, Any?>)
            } ?: AndroidResource()

            return ForegroundNotificationConfig(
                layout = (map["layout"] as? String) ?: "",
                title = map["title"] as? String ?: "{AppName}",
                text = map["text"] as? String ?: "Location service activated",
                smallIcon = smallIcon,
                largeIcon = largeIcon,
                priority = map["priority"] as? Int ?: 0,
                channelName = map["channelName"] as? String ?: "",
                actions = map["actions"] as? List<String> ?: emptyList(),
                enableWifiLock = map["enableWifiLock"] as? Boolean ?: false,
                enableWakeLock = map["enableWakeLock"] as? Boolean ?: false
            )
        }

    }

}