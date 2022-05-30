package com.royzed.simple_bg_location.domain.location

import com.royzed.simple_bg_location.errors.ErrorCodes

interface LocationServiceListener {
    fun onLocationServiceResult(isEnabled: Boolean)
    fun onLocationServiceError(errorCode: ErrorCodes)
}