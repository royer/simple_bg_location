package com.royzed.simple_bg_location.streams

import android.content.Context
import androidx.annotation.CallSuper
import com.royzed.simple_bg_location.SimpleBgLocationModule
import com.royzed.simple_bg_location.SimpleBgLocationPlugin
import io.flutter.Log
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel

open class StreamHandler(val eventName: String) : EventChannel.StreamHandler {
    protected lateinit var context: Context
    protected lateinit var eventSink: EventChannel.EventSink
    private lateinit var channel: EventChannel

    fun register(ctx: Context, messenger: BinaryMessenger): EventChannel.StreamHandler {
        context = ctx
        val path = SimpleBgLocationPlugin.EVENT_CHANNEL_PATH + "/$eventName"

        channel = EventChannel(messenger, path)
        channel.setStreamHandler(this)
        return this
    }

    @CallSuper
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events!!
    }

    @CallSuper
    override fun onCancel(arguments: Any?) {
        Log.d(TAG,"$eventName onCancel($arguments)")
        SimpleBgLocationModule.getInstance().unregisterListener(eventName, this)
    }

    companion object {
        private const val TAG = "StreamHandler"
    }
}