package com.withyang.voicedemo.utils;

import android.widget.Toast;

/**
 * Created by CYLiu on 2019/1/3
 */
public class ToastUtils {
    public static void show(String text) {
        Toast.makeText(AppUtils.getAppContext(),text,Toast.LENGTH_SHORT).show();
    }
}
