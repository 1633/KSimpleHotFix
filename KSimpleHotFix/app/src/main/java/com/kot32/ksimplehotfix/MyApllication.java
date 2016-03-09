package com.kot32.ksimplehotfix;

import android.app.Application;

import com.kot32.ksimplehotfixlibrary.manager.FixManager;

/**
 * Created by kot32 on 16/3/6.
 */
public class MyApllication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FixManager.getInstance().init(new FixManager.FixConfig("http://115.28.2.96/TomaToDo/jar/patch.apk", null), getApplicationContext());

    }
}
