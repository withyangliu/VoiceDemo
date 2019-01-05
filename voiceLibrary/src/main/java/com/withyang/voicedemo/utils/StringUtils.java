package com.withyang.voicedemo.utils;

import android.text.format.DateUtils;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by yunwen on 2018/1/2 0002.
 */

public class StringUtils {

    /**
     * 毫秒格式化分秒
     *
     * @param milli
     * @return
     */
    public static String formatTime(long milli) {
        if (milli < 0) {
            milli = 0;
        }
        String pattern = "mm:ss";
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        return pattern.replace("mm", mm).replace("ss", ss);
    }

    public static String stringForTime(int timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static int stringToInt(String str) {
        int i = -1;
        try {
            i = Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }
}
