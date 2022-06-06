package com.royzed.simple_bg_location.callbacks

import com.royzed.simple_bg_location.Events
import io.flutter.Log

class CallbacksManager {

    private val _positionCallbacks: MutableList<PositionCallback> = mutableListOf()
    val positionCallback: List<PositionCallback> = _positionCallbacks.toList()

    fun registerPositionListener(callback: PositionCallback) {
        synchronized(_positionCallbacks) {
            _positionCallbacks.add(callback)
        }
    }

    fun unregisterListener(eventName: String, callback: Any) {
        when(eventName) {
            Events.position -> {
                synchronized(_positionCallbacks) {
                    val finded = _positionCallbacks.remove(callback)
                    if (finded == false) {
                        Log.d(TAG, "remove PositionCallback failed.")
                    }
                }
            }
        }
    }

    fun unregisterAll() {
        synchronized(_positionCallbacks) {
            _positionCallbacks.clear()
        }
    }

    companion object {
        private const val TAG = "CallbacksManager"
    }
}