package com.example.demo.photogallery

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment

class PhotoGalleryActivity : BaseFragmentActivity() {
    override fun createFragment(): Fragment {
        return PhotoGalleryFragment.newInstance()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PhotoGalleryActivity::class.java)
        }
    }
}
