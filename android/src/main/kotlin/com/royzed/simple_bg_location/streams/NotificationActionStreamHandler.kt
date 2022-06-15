package com.royzed.simple_bg_location.streams

import com.royzed.simple_bg_location.Events
import com.royzed.simple_bg_location.SimpleBgLocationModule
import com.royzed.simple_bg_location.callbacks.NotificationActionCallback
import io.flutter.plugin.common.EventChannel

class NotificationActionStreamHandler: StreamHandler(Events.notificationAction), NotificationActionCallback {

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        super.onListen(arguments, events)
        SimpleBgLocationModule.getInstance().registerNotificationActionListener(this)

    }
    override fun onClick(action: String) {
        eventSink.success(action)
    }

}