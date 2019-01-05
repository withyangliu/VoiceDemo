package com.withyang.voicedemo.app;

import android.app.Application;

import com.withyang.voicedemo.tts.SpeechManager;
import com.withyang.voicedemo.utils.AppUtils;


/**
 * Created by CYLiu on 2019/1/3
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.init(this);
        SpeechManager.init(this);

    }
}
