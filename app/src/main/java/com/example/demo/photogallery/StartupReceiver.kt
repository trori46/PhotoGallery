package com.example.demo.photogallery

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log


class StartupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.action!!)
        val isOn = QueryPreferences.isAlarmOn(context)
        PollService.setServiceAlarm(context, isOn)
    }

    companion object {
        private const val TAG = "StartupReceiver"
    }
}