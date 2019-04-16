package com.example.demo.photogallery

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.*
import android.support.v7.widget.SearchView


class PhotoGalleryFragment : VisibleFragment() {
    private lateinit var mPhotoRecyclerView: RecyclerView
    private var mItems = ArrayList<GalleryItem>()
    private lateinit var mThumbnailDownloader: ThumbnailDownloader<PhotoHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
        updateItems()

//        PollService.setServiceAlarm(activity!!, true)

        var responseHandler: Handler = Handler()
        mThumbnailDownloader = ThumbnailDownloader<PhotoHolder>(responseHandler)


        mThumbnailDownloader.setThumbnailDownloadListener(
            object : ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder> {
                override fun onThumbnailDownloaded(photoHolder: PhotoHolder, bitmap: Bitmap) {
                    var drawable: Drawable = BitmapDrawable(resources, bitmap)
                    photoHolder.bindDrawable(drawable)
                }
            })
        mThumbnailDownloader.start()
        mThumbnailDownloader.looper


        Log.i(TAG, "Background thread started")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v: View = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        mPhotoRecyclerView = v.findViewById(R.id.photo_recycler_view)
        mPhotoRecyclerView.layoutManager = GridLayoutManager(activity!!, 3) as RecyclerView.LayoutManager?
        setupAdapter()
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(keyWord: String): Boolean {
                Log.d(TAG, "QueryTextSubmit: $keyWord")
                QueryPreferences.setStoredQuery(activity!!, keyWord)
                updateItems()
                return true
            }

            override fun onQueryTextChange(keyWord: String): Boolean {
                Log.d(TAG, "QueryTextChange: $keyWord")
                return false
            }
        })
        searchView.setOnClickListener {
            val query = QueryPreferences.getStoredQuery(activity!!)
            searchView.setQuery(query, false)
        }
        val toggleItem = menu.findItem(R.id.menu_item_toggle_polling)
        if (PollService.isServiceAlarmOn(activity!!)) {
            toggleItem.setTitle(R.string.stop_polling)
        } else {
            toggleItem.setTitle(R.string.start_polling)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.menu_item_clear -> {
                QueryPreferences.setStoredQuery(activity!!, null)
                updateItems()
                true
            }
            R.id.menu_item_toggle_polling -> {
                var shouldStartAlarm: Boolean = !PollService.isServiceAlarmOn(activity!!)
                PollService.setServiceAlarm(activity!!, shouldStartAlarm)
                activity!!.invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mThumbnailDownloader.quit()
        Log.i(TAG, "Background thread destroyed")
    }

    private fun setupAdapter() {
        if (isAdded) {
            mPhotoRecyclerView.adapter = PhotoAdapter(mItems)
        }
    }

    private fun updateItems() {
        val keyWord: String? = QueryPreferences.getStoredQuery(activity!!)
        FetchItemsTask(keyWord).execute()
    }

    private inner class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mItemImageView: ImageView = itemView.findViewById(R.id.imageView) as ImageView

        fun bindDrawable(drawable: Drawable) {
            mItemImageView.setImageDrawable(drawable)
        }
    }

    private inner class PhotoAdapter(private val mGalleryItems: List<GalleryItem>) :
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PhotoHolder {
            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.item_list_gallery, viewGroup, false)
            return PhotoHolder(view)
        }

        override fun onBindViewHolder(photoHolder: PhotoHolder, position: Int) {
            val galleryItem = mGalleryItems[position]
            val placeholder = resources.getDrawable(R.mipmap.ic_launcher_round)
            photoHolder.bindDrawable(placeholder)
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.mUrl)
        }

        override fun getItemCount(): Int {
            return mGalleryItems.size
        }
    }

    private inner class FetchItemsTask constructor(var keyWord: String?) :
        AsyncTask<Void, Void, ArrayList<GalleryItem>>() {
        override fun doInBackground(vararg params: Void): ArrayList<GalleryItem> {
            return FlickrFetchr(keyWord)
                .getItems("https://www.baidu.com")
        }

        override fun onPostExecute(result: ArrayList<GalleryItem>?) {
            mItems = result as ArrayList<GalleryItem>
            setupAdapter()
        }
    }

    companion object {
        private const val TAG = "PhotoGalleryFragment"
        fun newInstance(): Fragment {
            return PhotoGalleryFragment()
        }
    }
}