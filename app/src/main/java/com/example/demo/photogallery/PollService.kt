package com.example.demo.photogallery

import android.app.*
import android.content.Intent
import android.content.Context
import android.util.Log
import android.net.ConnectivityManager
import android.os.SystemClock
import java.util.concurrent.TimeUnit
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.NotificationCompat


class PollService : IntentService(TAG) {
    override fun onHandleIntent(intent: Intent?) {
        if (!isNetworkAvailableAndConnected()) {
            return
        }
        val query = QueryPreferences.getStoredQuery(this)
        val lastResultId = QueryPreferences.getLastResultId(this)
        val items: List<GalleryItem> = FlickrFetchr(query).getItems("https://www.baidu.com/") as List<GalleryItem>

        if (items.isEmpty()) {
            return
        }
        val resultId = items[0].mId
        if (resultId == lastResultId) {
            Log.i(TAG, "Got an old result: $resultId")
        } else {
            Log.i(TAG, "Got a new result: $resultId")
            val intent = PhotoGalleryActivity.newIntent(this)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            val notification = NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_pictures_title))
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
//            val notificationManager = NotificationManagerCompat.from(this)
//            notificationManager.notify(0, notification)
//            sendBroadcast(Intent(ACTION_SHOW_NOTIFICATION),PERM_PRIVATE)
            showBackgroundNotification(0, notification);
        }
        QueryPreferences.setLastResultId(this, resultId)
    }

    private fun showBackgroundNotification(requestCode: Int, notification: Notification) {
        val i = Intent(ACTION_SHOW_NOTIFICATION)
        i.putExtra(REQUEST_CODE, requestCode)
        i.putExtra(NOTIFICATION, notification)
        sendOrderedBroadcast(
            i, PERM_PRIVATE, null, null,
            Activity.RESULT_OK, null, null
        )
    }

    private fun isNetworkAvailableAndConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isNetworkAvailable = cm.activeNetworkInfo != null
        return isNetworkAvailable && cm.activeNetworkInfo.isConnected
    }

    companion object {
        const val TAG = "PollService"
        private var POLL_INTERVAL_MS: Long = TimeUnit.MINUTES.toMillis(1)
        const val ACTION_SHOW_NOTIFICATION = "com.example.demo.photogallery.SHOW_NOTIFICATION"
        const  val PERM_PRIVATE = "com.example.demo.photogallery.PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
        private fun newIntent(context: Context): Intent {
            return Intent(context, PollService::class.java)
        }

        fun setServiceAlarm(context: Context, isOn: Boolean) {
            val i = PollService.newIntent(context)
            val pi = PendingIntent.getService(context, 0, i, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (isOn) {
                alarmManager.setRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pi
                )
            } else {
                alarmManager.cancel(pi)
                pi.cancel()
            }
            QueryPreferences.setAlarmOn(context, isOn);
        }

        fun isServiceAlarmOn(context: Context): Boolean {
            val i = PollService.newIntent(context)
            val pi = PendingIntent
                .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE)
            return pi != null
        }
    }
}