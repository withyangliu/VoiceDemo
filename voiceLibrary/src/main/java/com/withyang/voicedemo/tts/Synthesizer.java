package com.withyang.voicedemo.tts;

import android.os.Bundle;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizerListener;
import com.withyang.voicedemo.iat.RecognizerManager;
import com.withyang.voicedemo.utils.Logger;


/**
 * Created by cyliu on 2017/12/21 0021.
 */

public abstract class Synthesizer implements SynthesizerListener, SpeechSynthesizerListener {

    /**
     * usc
     *
     * @param type
     */
    @Override
    public void onEvent(int type) {
        switch (type) {
            case SpeechConstants.TTS_EVENT_INIT:
                // 初始化成功回调
                break;
            case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
                // 开始合成回调
                break;
            case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
                // 合成结束回调
                break;
            case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
                // 开始缓存回调
                break;
            case SpeechConstants.TTS_EVENT_BUFFER_READY:
                // 缓存完毕回调
                break;
            case SpeechConstants.TTS_EVENT_PLAYING_START:
                // 开始播放回调
                Logger.i("开始播放");
                SpeechManager.getInstance().setStatus(Status.STATES_PLAYING);
                speakBegin();
                break;
            case SpeechConstants.TTS_EVENT_PLAYING_END:
                // 播放完成回调
                SpeechManager.getInstance().setStatus(Status.STATES_STOP);
                Logger.i("Listener start");
                RecognizerManager.getInstance().startListening();
                completed();
                break;
            case SpeechConstants.TTS_EVENT_PAUSE:
                // 暂停回调
                Logger.i("暂停播放");
                SpeechManager.getInstance().setStatus(Status.STATES_PAUSE);
                speakPaused();
                break;
            case SpeechConstants.TTS_EVENT_RESUME:
                // 恢复回调
                Logger.i("继续播放");
                SpeechManager.getInstance().setStatus(Status.STATES_PLAYING);
                speakResumed();
                break;
            case SpeechConstants.TTS_EVENT_STOP:
                // 停止回调
                break;
            case SpeechConstants.TTS_EVENT_RELEASE:
                // 释放资源回调
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

    }

    @Override
    public void onSpeakBegin() {
        Logger.i("开始播放");
        SpeechManager.getInstance().setStatus(Status.STATES_PLAYING);
        speakBegin();
    }

    @Override
    public void onSpeakPaused() {
        Logger.i("暂停播放");
        SpeechManager.getInstance().setStatus(Status.STATES_PAUSE);
        speakPaused();
    }

    @Override
    public void onSpeakResumed() {
        Logger.i("继续播放");
        SpeechManager.getInstance().setStatus(Status.STATES_PLAYING);
        speakResumed();
    }

    @Override
    public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        bufferProgress(percent, beginPos, endPos, info);
    }

    @Override
    public void onSpeakProgress(int percent, int beginPos, int endPos) {
        speakProgress(percent, beginPos, endPos);
    }

    @Override
    public void onCompleted(SpeechError error) {
        if (error == null) {
            Logger.i("播放完成");
        }
        completed();
        SpeechManager.getInstance().setStatus(Status.STATES_STOP);
        Logger.i("Listener start");
        RecognizerManager.getInstance().startListening();
    }

    @Override
    public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
    }

    public abstract void speakBegin();

    public abstract void speakPaused();

    public abstract void speakResumed();

    public abstract void bufferProgress(int percent, int beginPos, int endPos, String info);

    public abstract void speakProgress(int percent, int beginPos, int endPos);

    public abstract void completed( );
}
