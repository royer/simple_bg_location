package com.royzed.simple_bg_location.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import io.flutter.Log

private const val TAG = "SettingsUtils"
object SettingsUtils {
    fun openLocationSettings(context: Context): Boolean {
        try {
            Intent().apply {
                action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            }.let {
                context.startActivity(it)
            }
            
            return true
        } catch (ex: Exception) {
            Log.e(TAG, "openLocationSettings() failed. $ex")
            return false
        }
    }
}