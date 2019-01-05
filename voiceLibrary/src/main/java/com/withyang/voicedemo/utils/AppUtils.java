package com.withyang.voicedemo.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by CYLiu on 2019/1/3
 */
public class AppUtils {
    private static Context mContext;

    public static void init(Context context) { //在Application中初始化
        mContext = context;
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static Resources getResource() {
        return mContext.getResources();
    }
}
