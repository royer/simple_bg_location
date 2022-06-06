package com.royzed.simple_bg_location.callbacks

import com.royzed.simple_bg_location.data.Position
import com.royzed.simple_bg_location.errors.ErrorCodes

interface PositionCallback {
    fun onPosition(position: Position)
    fun onError(errorCode: ErrorCodes)
}