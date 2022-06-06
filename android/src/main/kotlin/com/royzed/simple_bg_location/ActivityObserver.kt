package com.royzed.simple_bg_location

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.royzed.simple_bg_location.domain.location.PositionChangedCallback
import com.royzed.simple_bg_location.domain.location.SimpleBgLocationManager
import com.royzed.simple_bg_location.errors.ErrorCallback
import com.royzed.simple_bg_location.services.SimpleBgLocationService
import io.flutter.Log
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

class ActivityObserver : DefaultLifecycleObserver, ActivityPluginBinding.OnSaveInstanceStateListener {
    private var _activity: Activity? = null
    val activity: Activity
        get() = _activity!!

    private lateinit var mService: SimpleBgLocationService
    private lateinit var sbgLocationManager: SimpleBgLocationManager
    private var mBound = false


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SimpleBgLocationService.LocalBinder
            mService = binder.service
            mBound = true
            Log.d(TAG,"service connected .")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
            Log.d(TAG,"service disconnected.")
        }

    }

    override fun onCreate(owner: LifecycleOwner) {
        Log.d(TAG, "activity onCreated($owner) and _activity = $_activity")
        super.onCreate(owner)
        _activity = owner as Activity
        sbgLocationManager = SimpleBgLocationManager(owner.applicationContext)
    }

    fun getCurrentPosition(
        forceLocationManager: Boolean,
        positionChangedCallback: PositionChangedCallback,
        errorCallback: ErrorCallback
    ) {

        sbgLocationManager.getCurrentPosition(forceLocationManager, positionChangedCallback, errorCallback)
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d(TAG,"onStart($owner)")
        super.onStart(owner)
        Intent(activity, SimpleBgLocationService::class.java).also {
            activity.bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG,"onResume($owner)")
        super.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d(TAG,"onPause($owner)")
        super.onPause(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG,"onStop($owner)")
        if (mBound) {
            activity.unbindService(serviceConnection)
        }
        super.onStop(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(TAG,"onDestroy($owner)")
        super.onDestroy(owner)
        _activity = null
    }

    fun onDetachFromActivity() {
        Log.d(TAG,"onDetachFromActivity")
        _activity = null
    }

    companion object {
        private const val TAG = "SimpleBgl.ActivityObserver"

    }

    override fun onSaveInstanceState(bundle: Bundle) {
        Log.d(TAG,"onSaveInstanceState")
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        Log.d(TAG,"onRestoreInstanceState")
    }
}