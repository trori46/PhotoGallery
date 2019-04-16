package com.example.demo.photogallery

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.*
import android.support.v7.app.AppCompatActivity

abstract class BaseFragmentActivity : AppCompatActivity() {
    protected abstract fun createFragment(): Fragment

    @LayoutRes
    protected open fun getLayoutResId(): Int {
        return R.layout.fragment_containers
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        var fm: FragmentManager = supportFragmentManager
        var fragment: Fragment? = fm.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            fragment = createFragment()
        }
        var tran: FragmentTransaction = fm.beginTransaction()
        if (!fragment.isAdded) {
            tran.add(R.id.fragment_container, fragment)
        }
        tran.commit()
    }
}