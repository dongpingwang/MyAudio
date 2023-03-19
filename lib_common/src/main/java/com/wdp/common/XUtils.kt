package com.wdp.common

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
@SuppressLint("PrivateApi")
object XUtils {

    private lateinit var sApplication: Application

    fun init(application: Application) {
        sApplication = application
    }

    @Synchronized
    fun getApplication(): Application {
        if (!this::sApplication.isInitialized) {
            currentApplication()?.let {
                sApplication = it
            }
        }
        return sApplication
    }

    fun getContext(): Context = getApplication().applicationContext

    private fun currentApplication(): Application? {
        return kotlin.runCatching {
            val activityThread = Class.forName("android.app.ActivityThread")
            val currentApplication = activityThread.getMethod("currentApplication")
            currentApplication.isAccessible = true
            currentApplication.invoke(null, null) as Application
        }.also {
            it.exceptionOrNull()?.printStackTrace()
        }.getOrNull()
    }
}