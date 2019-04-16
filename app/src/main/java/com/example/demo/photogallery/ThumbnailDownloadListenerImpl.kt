package com.example.demo.photogallery

import android.graphics.Bitmap

class ThumbnailDownloadListenerImpl<T> : ThumbnailDownloader.ThumbnailDownloadListener<T> {
    override fun onThumbnailDownloaded(target: T, thumbnail: Bitmap) {

    }
}