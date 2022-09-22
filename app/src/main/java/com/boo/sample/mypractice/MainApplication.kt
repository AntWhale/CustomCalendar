package com.boo.sample.mypractice

import android.app.Application

class MainApplication : Application() {
    lateinit var localVolley: LocalVolley

    override fun onCreate() {
        super.onCreate()
        localVolley = LocalVolley(applicationContext)

    }
}