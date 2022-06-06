package com.royzed.simple_bg_location

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.flutter.Log

/// Juest test Application.ActivityLifecycleCallbacks
class ActivityLifecycleMonitor(): Application.ActivityLifecycleCallbacks {

    private var mActivity: Activity? = null

    fun setActivity(activity: Activity?) {
        if (activity != null) {
            if (mActivity != null) {
                if (mActivity.hashCode() == activity.hashCode()) return;
                Log.w(TAG,"set a new Activity but mActivity has value and not equal this new activity. unregister this lifecycleCallbacks");
                mActivity?.application?.unregisterActivityLifecycleCallbacks(this)
            }

            activity.application.registerActivityLifecycleCallbacks(this)
        } else if (mActivity != null) {
            val app = mActivity?.application;
            if (app != null) {
                app.unregisterActivityLifecycleCallbacks(this)
            } else {
                Log.w(TAG,"In setActivity(null), but mActivity.app == null! can't unregister this lifecycleCallbacks.")
            }
        }

        mActivity = activity
        Log.d(TAG,"setActivity($mActivity)")
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        check(activity, "onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
        check(activity, "onActivityStarted")
    }

    override fun onActivityResumed(activity: Activity) {
        check(activity, "onActivityResumed")
    }

    override fun onActivityPaused(activity: Activity) {
        check(activity, "onActivityPaused")
    }

    override fun onActivityStopped(activity: Activity) {
        check(activity, "onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        check(activity, "onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        check(activity, "onActivityDestroyed")
    }

    fun check(activity: Activity, where: String) {
        if (activity != mActivity) {
            Log.w(TAG,"in $where activity($activity) != mActivity($mActivity)")
        }
    }

    companion object {
        private const val TAG = "ActivityLifecycleMonitor"
    }
}