package com.royzed.simple_bg_location.streams

import com.royzed.simple_bg_location.Events
import io.flutter.plugin.common.EventChannel

class PositionStreamHandler : StreamHandler(Events.position)  {
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        super.onListen(arguments, events)
    }

    override fun onCancel(arguments: Any?) {
        super.onCancel(arguments)
    }
}