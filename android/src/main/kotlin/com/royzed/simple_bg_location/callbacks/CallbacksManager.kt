package com.royzed.simple_bg_location.callbacks

import com.royzed.simple_bg_location.Events
import com.royzed.simple_bg_location.data.Position
import com.royzed.simple_bg_location.errors.ErrorCodes
import io.flutter.Log

class CallbacksManager {

    private val _positionCallbacks: MutableList<PositionCallback> = mutableListOf()
    val positionCallbacks: List<PositionCallback> = _positionCallbacks.toList()

    fun registerPositionListener(callback: PositionCallback) {
        synchronized(_positionCallbacks) {
            Log.d(TAG, "A PositionCallback registered. $callback")
            _positionCallbacks.add(callback)
        }
    }

    fun dispatchPostionEvent(position: Position) {
        synchronized(_positionCallbacks) {
            for(callback in _positionCallbacks) {
                callback.onPosition(position)
            }
        }
    }

    fun dispatchPositionErrorEvent(errorCode: ErrorCodes) {
        synchronized(_positionCallbacks) {
            for(callback in _positionCallbacks) {
                callback.onError(errorCode)
            }
        }
    }

    fun unregisterListener(eventName: String, callback: Any) {
        when(eventName) {
            Events.position -> {
                synchronized(_positionCallbacks) {
                    val finded = _positionCallbacks.remove(callback)
                    if (finded == false) {
                        Log.d(TAG, "remove PositionCallback failed.")
                    } else {
                        Log.d(TAG,"one PositionCallback removed.")
                    }
                }
            }
        }
    }

    fun unregisterAll() {
        synchronized(_positionCallbacks) {
            _positionCallbacks.clear()
            Log.d(TAG,"All PositionCallback removed")
        }
    }

    companion object {
        private const val TAG = "CallbacksManager"
    }
}