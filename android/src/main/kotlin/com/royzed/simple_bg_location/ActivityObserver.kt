package com.royzed.simple_bg_location

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.royzed.simple_bg_location.services.SimpleBgLocationService
import io.flutter.Log

class ActivityObserver : DefaultLifecycleObserver {
    private var _activity: Activity? = null
    val activity: Activity
        get() = _activity!!

    private lateinit var mService: SimpleBgLocationService
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
        super.onCreate(owner)
        _activity = owner as Activity
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Intent(activity, SimpleBgLocationService::class.java).also {
            activity.bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        if (mBound) {
            activity.unbindService(serviceConnection)
        }
        super.onStop(owner)

    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        _activity = null
    }

    companion object {
        private const val TAG = "SimpleBgl.ActivityObserver"

    }
}