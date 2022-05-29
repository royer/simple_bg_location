package com.royzed.simple_bg_location.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import io.flutter.Log

private const val TAG = "SettingsUtils"
object SettingsUtils {
    @JvmStatic
    fun openLocationSettings(context: Context): Boolean {
        return try {
            Intent().apply {
                action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            }.let {
                context.startActivity(it)
            }
            
            true
        } catch (ex: Exception) {
            Log.e(TAG, "openLocationSettings() failed. $ex")
            false
        }
    }

    @JvmStatic
    fun openAppSettings(context: Context): Boolean {
        return try {
            val settingsIntent = Intent()
            settingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
            settingsIntent.data = Uri.parse("package:" + context.packageName)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            context.startActivity(settingsIntent)
            true
        } catch (ex: Exception) {
            Log.e(TAG,"openAppSettings() failed. $ex")
            false
        }

    }
}