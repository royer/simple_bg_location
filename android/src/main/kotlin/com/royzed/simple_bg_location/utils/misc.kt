package com.royzed.simple_bg_location.utils

import android.content.Context

fun getAppName(context: Context): String {
    val pm = context.packageManager
    val ai = context.applicationContext.applicationInfo
    return pm.getApplicationLabel(ai).toString()
}

fun parseFlutterIntToLong(value: Any?): Long? {
    return when(value) {
        is Int -> value.toLong()
        is Long -> value
        is String -> value.toLongOrNull()
        else -> null
    }
}

fun distanceToString(d: Double): String {
    if (d < 1000) {
        return String.format("%.0fm", d)
    } else {
        return String.format("%.1fkm", d / 1000)
    }
}
