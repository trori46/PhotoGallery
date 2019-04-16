package com.example.demo.photogallery

class GalleryItem constructor(var mId: String, private var mCaption: String, var mUrl: String) {
    override fun toString(): String {
        return mCaption
    }
}