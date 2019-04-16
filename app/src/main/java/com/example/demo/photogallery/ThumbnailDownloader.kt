package com.example.demo.photogallery

import android.os.HandlerThread
import android.util.Log
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Handler
import java.util.concurrent.ConcurrentHashMap
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Message
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.io.IOException
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R.attr.bitmap






class ThumbnailDownloader<T>  constructor(private var responseHandler: Handler): HandlerThread(TAG) {
    private var mHasQuit: Boolean? = false
    private var mRequestHandler: Handler? = null
    private val mRequestMap = ConcurrentHashMap<T, String>()
    private var mThumbnailDownloadListener: ThumbnailDownloadListener<T>? = null

    interface ThumbnailDownloadListener<T> {
        fun onThumbnailDownloaded(target: T, thumbnail: Bitmap)
    }

    fun setThumbnailDownloadListener(listener: ThumbnailDownloadListener<T>) {
        mThumbnailDownloadListener = listener
    }

    override fun onLooperPrepared() {
        mRequestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what === MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL: " + mRequestMap[target])
                    handleRequest(target)
                }
            }
        }
    }

    private fun handleRequest(target: T) {
        try {
            val url = mRequestMap[target] ?: return
            val bitmapBytes = FlickrFetchr().getUrlBytes(url)
            val bitmap = BitmapFactory
                .decodeByteArray(bitmapBytes, 0, bitmapBytes.size)

           responseHandler.post(Runnable {
                if (mRequestMap[target] !== url || mHasQuit!!) {
                    return@Runnable
                }
                mRequestMap.remove(target)
                mThumbnailDownloadListener!!.onThumbnailDownloaded(target, bitmap)
            })
            Log.i(TAG, "Bitmap created")
        } catch (ioe: IOException) {
            Log.e(TAG, "Error downloading image", ioe)
        }

    }

    override fun quit(): Boolean {
        mHasQuit = true
        return super.quit()
    }

    fun queueThumbnail(target: T, url: String) {
        if (url == null) {
            mRequestMap.remove(target)
        } else {
            mRequestMap[target] = url
            mRequestHandler!!.obtainMessage(MESSAGE_DOWNLOAD, target)
                .sendToTarget()
        }
        Log.i(TAG, "Got a URL: $url")
    }

    companion object {
        private const val TAG = "ThumbnailDownloader"
        private const val MESSAGE_DOWNLOAD = 0
    }
}