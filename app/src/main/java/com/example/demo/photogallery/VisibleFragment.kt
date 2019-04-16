package com.example.demo.photogallery

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.support.v4.app.Fragment
import android.util.Log


abstract class VisibleFragment : Fragment() {
    private val mOnShowNotification = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            Toast.makeText(activity, "Got a broadcast:" + intent.action!!, Toast.LENGTH_LONG).show()
            Log.i(TAG, "canceling notification")
            resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(PollService.ACTION_SHOW_NOTIFICATION)
        activity!!.registerReceiver(mOnShowNotification, filter, PollService.PERM_PRIVATE, null)
    }

    override fun onStop() {
        super.onStop()
        activity!!.unregisterReceiver(mOnShowNotification)
    }

    companion object {
        private const val TAG = "VisibleFragment"
    }
}