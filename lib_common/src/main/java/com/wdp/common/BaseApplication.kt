package com.wdp.common

import android.app.Application

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
abstract class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        XUtils.init(this)
        Logger.setConfig(LogConfig(prefix = "wdp_"))
    }
}