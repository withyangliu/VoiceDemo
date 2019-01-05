package com.withyang.voicedemo.tts;

import com.iflytek.cloud.SpeechError;
import com.withyang.voicedemo.iat.RecognizerManager;
import com.withyang.voicedemo.utils.Logger;


/**
 * Created by cyliu on 2018/1/11 0011.
 */
public class SimpleSynthesizer extends Synthesizer {

    /**x
     * 播报完成是否监听
     */
    private boolean isListening;

    public SimpleSynthesizer(boolean isListening) {
        this.isListening = isListening;
    }

    @Override
    public void onCompleted(SpeechError error) {
        if (error == null) {
            Logger.i("播放完成");
        } else if (error != null) {
        }
        completed();
        SpeechManager.getInstance().setStatus(Status.STATES_STOP);
        if (isListening) {
            Logger.i("Listener start");
            RecognizerManager.getInstance().startListening();
        }
    }

    @Override
    public void speakBegin() {

    }

    @Override
    public void speakPaused() {

    }

    @Override
    public void speakResumed() {

    }

    @Override
    public void bufferProgress(int percent, int beginPos, int endPos, String info) {

    }

    @Override
    public void speakProgress(int percent, int beginPos, int endPos) {

    }

    @Override
    public void completed() {
        SpeechManager.getInstance().setStatus(Status.STATES_STOP);
        if (isListening) {
            Logger.i("Listener start");
            RecognizerManager.getInstance().startListening();
        } else {
            Logger.i("Listener stop");
            RecognizerManager.getInstance().release();
        }
    }
}

