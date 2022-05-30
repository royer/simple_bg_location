package com.royzed.simple_bg_location.permission

enum class AccuracyPermission {
    precise, // manifest android.permission.ACCESS_FINE_LOCATION
    approximate, // manifest android.permission.ACCESS_COARSE_LOCATION
    denied

}