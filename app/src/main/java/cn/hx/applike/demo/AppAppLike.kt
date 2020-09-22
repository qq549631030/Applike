package cn.hx.applike.demo

import android.content.Context
import cn.hx.applike.annotation.AppLike
import cn.hx.applike.api.IAppLike

@AppLike
class AppAppLike : IAppLike {
    override fun getPriority(): Int {
        return 1
    }

    override fun onCreate(context: Context) {
        (context as? AppApplication)?.str = "hello applike"
    }

    override fun onTerminate(context: Context) {
    }
}