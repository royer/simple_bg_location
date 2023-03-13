package com.royzed.simple_bg_location.permission

enum class AccuracyPermission {
    Precise, // manifest android.permission.ACCESS_FINE_LOCATION
    Approximate, // manifest android.permission.ACCESS_COARSE_LOCATION
    Denied

}