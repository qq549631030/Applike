package cn.hx.applike.api;

import android.content.Context;

public interface IAppLike {
    int getPriority();

    void onCreate(Context context);

    void onTerminate(Context context);
}
