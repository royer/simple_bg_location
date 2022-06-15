package com.royzed.simple_bg_location.callbacks

import com.royzed.simple_bg_location.Events
import com.royzed.simple_bg_location.data.Position
import com.royzed.simple_bg_location.errors.ErrorCodes
import io.flutter.Log

class CallbacksManager {

    private val _positionCallbacks: MutableList<PositionCallback> = mutableListOf()
    val positionCallbacks: List<PositionCallback> = _positionCallbacks.toList()

    private val _notificationCallbacks: MutableList<NotificationActionCallback> = mutableListOf()
    val nofificationActionCallbacks: List<NotificationActionCallback> = _notificationCallbacks.toList()

    fun registerPositionListener(callback: PositionCallback) {
        Log.d(TAG, "A PositionCallback registered. $callback")
        synchronized(_positionCallbacks) {
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

    fun registerNotificationActionListener(callback: NotificationActionCallback) {
        Log.d(TAG,"A notificationActionCallback registered. $callback")
        synchronized(_notificationCallbacks) {
            _notificationCallbacks.add(callback)
        }
    }

    fun dispatchNotificationAction(action: String) {
        synchronized(_notificationCallbacks) {
            for (callback in _notificationCallbacks) {
                callback.onClick(action)
            }
        }
    }

    fun unregisterListener(eventName: String, callback: Any) {
        when(eventName) {
            Events.position -> {
                synchronized(_positionCallbacks) {
                    val finded = _positionCallbacks.remove(callback)
                    if (finded == false) {
                        Log.w(TAG, "remove PositionCallback failed.")
                    } else {
                        Log.d(TAG,"A PositionCallback removed.")
                    }
                }
            }
            Events.notificationAction -> {
                synchronized(_notificationCallbacks) {
                    val finded = _notificationCallbacks.remove(callback)
                    if (finded == false) {
                        Log.w(TAG,"remove notificationActionCallback failed.")
                    } else {
                        Log.d(TAG,"A NotificationActionCallback removed.")
                    }
                }
            }
        }
    }

    fun unregisterAll() {
        synchronized(_positionCallbacks) {
            _positionCallbacks.clear()
            Log.d(TAG,"All PositionCallback removed.")
        }
        synchronized(_notificationCallbacks) {
            _notificationCallbacks.clear()
            Log.d(TAG,"All NotificationActionCallback removed.")
        }
    }

    companion object {
        private const val TAG = "CallbacksManager"
    }
}