package com.example.demo.photogallery

import android.support.v4.app.NotificationManagerCompat
import android.app.Activity
import android.app.Notification
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Parcelable
import android.util.Log


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        Log.i(TAG, "received result: $resultCode")
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        val requestCode = i.getIntExtra(PollService.REQUEST_CODE, 0)
        val notification = i.getParcelableExtra<Parcelable>(PollService.NOTIFICATION) as Notification
        val notificationManager = NotificationManagerCompat.from(c)
        notificationManager.notify(requestCode, notification)
    }

    companion object {
        private const val TAG = "NotificationReceiver"
    }
}