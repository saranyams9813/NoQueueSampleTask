package com.noqueue.noqueuesampletask.Utils

import android.app.Application
import android.content.Context

class ApplicationCommon :Application() {
    companion object{
        var context:Context ?=null
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}