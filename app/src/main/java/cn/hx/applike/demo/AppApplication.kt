package cn.hx.applike.demo

import android.app.Application
import cn.hx.applike.api.AppLikeManager

class AppApplication : Application() {

    var str: String = ""
    override fun onCreate() {
        super.onCreate()
        AppLikeManager.onCreate(this)
    }

    override fun onTerminate() {
        AppLikeManager.onTerminate(this)
        super.onTerminate()
    }
}