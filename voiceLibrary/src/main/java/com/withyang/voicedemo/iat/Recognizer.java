package com.withyang.voicedemo.iat;

import android.os.Bundle;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechUnderstanderListener;
import com.withyang.voicedemo.tts.SpeechManager;
import com.withyang.voicedemo.utils.JsonParser;
import com.withyang.voicedemo.utils.Logger;
import com.withyang.voicedemo.utils.UnderstanderResultUtil;


/**
 * Created by yunwen on 2017/12/21 0021.
 */

public abstract class Recognizer implements RecognizerListener, SpeechUnderstanderListener {

    private String result = "";

    /**
     * usc
     *
     * @param type
     * @param timeMs
     */
    @Override
    public void onEvent(int type, int timeMs) {
        switch (type) {
            case SpeechConstants.ASR_EVENT_NET_END:
               Logger.i("ASR_EVENT_NET_END");
                if (!SpeechManager.getInstance().isSpeeching()) {
                    Logger.i("Listener start");
                    RecognizerManager.getInstance().startListening();
                }
                break;
            case SpeechConstants.ASR_EVENT_VOLUMECHANGE:
                int volume = (Integer) RecognizerManager.getInstance().getUnderstander().getOption(SpeechConstants
                        .GENERAL_UPDATE_VOLUME);
                onVolumeChanged(volume);
                break;
            case SpeechConstants.ASR_EVENT_VAD_TIMEOUT:
                // 收到用户停止说话事件，停止录音
                Logger.i("mUnderstander stop");
               RecognizerManager.getInstance().getUnderstander().stop();
                break;
            case SpeechConstants.ASR_EVENT_RECORDING_STOP:
                // 停止录音，请等待识别结果回调
                Logger.i("onRecordingStop");
                break;
            case SpeechConstants.ASR_EVENT_RECOGNITION_END:
                // 识别结束
                Logger.i("ASR_EVENT_RECOGNITION_END");
                break;
            case SpeechConstants.ASR_EVENT_SPEECH_DETECTED:
                //用户开始说话
                Logger.i("onSpeakStart");
                break;
            case SpeechConstants.ASR_EVENT_RECORDING_START:
                //录音设备打开，开始识别，用户可以开始说话
                break;
            default:
                break;
        }
    }

    /**
     * usc
     *
     * @param type
     * @param errorMSG
     */
    @Override
    public void onError(int type, String errorMSG) {
        if (!SpeechManager.getInstance().isSpeeching()) {
            Logger.i("Listener start");
           RecognizerManager.getInstance().startListening();
        }
    }

    /**
     * usc
     *
     * @param type
     * @param jsonResult
     */
    @Override
    public void onResult(int type, String jsonResult) {
        switch (type) {
            case SpeechConstants.ASR_RESULT_NET:
                if (jsonResult.contains("net_asr") && jsonResult.contains("net_nlu")) {
                    result = UnderstanderResultUtil.asrResultOperate(jsonResult);
                    Logger.i("onResult" + result);
                    if (result.trim().length() > 0) {
                        onResult(result);
                        result = "";
                    } else {
                        result = "";
                        continueListening();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBeginOfSpeech() {
        // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
        Logger.i("onBeginOfSpeech");
    }

    @Override
    public void onError(SpeechError error) {
        // Tips：
        // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
        //         10114 网络异常
        //         20002 获取结果超时
        Logger.i(error.getPlainDescription(true));
        if (error.getErrorCode() == 10118 || error.getErrorCode() == 10114
                || error.getErrorCode() == 20002) {
            Logger.i("Listener start");
           RecognizerManager.getInstance().startListening();
        }
    }

    @Override
    public void onEndOfSpeech() {
        // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
        Logger.i("onEndOfSpeech");
    }

    /**
     * iflytek讯飞
     * @param results
     * @param isLast
     */
    @Override
    public void onResult(RecognizerResult results, boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());
        result += text;
        if (isLast) {
            if (result.trim().length() > 0) {
                onResult(result);
                result = "";
            } else {
                result = "";
                Logger.i("Listener start");
              RecognizerManager.getInstance().startListening();
            }
        }
    }

    @Override
    public void onVolumeChanged(int volume, byte[] data) {
        onVolumeChanged(volume);
    }

    @Override
    public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

    }

    private void continueListening() {
        result = "";
       RecognizerManager.getInstance().startListening();
    }

    public abstract void onVolumeChanged(int volume);

    public abstract void onResult(String result);

}
