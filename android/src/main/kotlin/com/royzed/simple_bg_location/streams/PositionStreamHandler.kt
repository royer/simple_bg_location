package com.royzed.simple_bg_location.streams

import com.royzed.simple_bg_location.Events
import com.royzed.simple_bg_location.SimpleBgLocationModule
import com.royzed.simple_bg_location.callbacks.PositionCallback
import com.royzed.simple_bg_location.data.Position
import com.royzed.simple_bg_location.errors.ErrorCodes
import io.flutter.Log
import io.flutter.plugin.common.EventChannel

class PositionStreamHandler : StreamHandler(Events.position), PositionCallback  {
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        super.onListen(arguments, events)
        SimpleBgLocationModule.getInstance().registerPositionListener(this)
    }



    override fun onPosition(position: Position) {

        eventSink.success(position.toMap())
    }

    override fun onError(errorCode: ErrorCodes) {
        eventSink.error(errorCode.code, errorCode.description, null)
    }

    companion object {
        private const val TAG = "PositionStreamHandler"
    }
}