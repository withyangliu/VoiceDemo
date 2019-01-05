package com.withyang.voicedemo.utils;


/**
 * Created by yunwen on 2017/12/22 0022.
 */

public class PathConfig {

    public static String getWakePath() {
        return FileUtils.getRootPath() + "/msc/ivw.wav";
    }

    public static String getRecognizerPath() {
        return FileUtils.getRootPath() + "/msc/iat.wav";
    }

    public static String getSpeechPath() {
        return FileUtils.getRootPath() + "/msc/tts.wav";
    }
}
