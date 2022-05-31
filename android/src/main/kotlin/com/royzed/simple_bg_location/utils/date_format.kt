package com.royzed.simple_bg_location.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
fun Date.toIso8601String(utc: Boolean = false): String {
    val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    if (utc) {
        sdf.timeZone = TimeZone.getTimeZone("UTC")
    }
    return sdf.format(this)
}