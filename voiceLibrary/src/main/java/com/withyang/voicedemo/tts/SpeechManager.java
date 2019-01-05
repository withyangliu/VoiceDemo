package com.withyang.voicedemo.tts;

import android.content.Context;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.unisound.client.SpeechConstants;
import com.withyang.voicedemo.PlatformConfig;
import com.withyang.voicedemo.R;
import com.withyang.voicedemo.UscConfig;
import com.withyang.voicedemo.iat.RecognizerManager;
import com.withyang.voicedemo.utils.AppUtils;
import com.withyang.voicedemo.utils.Logger;
import com.withyang.voicedemo.utils.PathConfig;
import com.withyang.voicedemo.utils.ToastUtils;




/**
 * 语音播报
 * Created by cyliu on 2017/12/20 0020.
 */

public class SpeechManager {

    private int status;

    private SpeechSynthesizer iflytekTts;

    com.unisound.client.SpeechSynthesizer uscTts;

    private String text;

    private Synthesizer synthesizer;

    private static volatile SpeechManager instance = null;

    private SpeechManager() {
    }

    public static SpeechManager getInstance() {
        if (instance == null) {
            synchronized (SpeechManager.class) {
                if (instance == null) {
                    instance = new SpeechManager();
                }
            }
        }
        return instance;
    }

    /**
     * 播报
     *
     * @param text        播报文本
     * @param synthesizer 播报进度
     */
    public void speak(String text, Synthesizer synthesizer) {
        if (text == null) {
            ToastUtils.show("文本错误");
            return;
        }
        if (text.length() > 4000) {
            ToastUtils.show("文本过长");
            return;
        }
        setText(text);
        setSynthesizer(synthesizer);
        speech();
    }

    /**志玲*/
//        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_NAME, "lzl");
    /**大点女生*/
//        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_NAME, "xiaoli");
    /**小女孩*/
//        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_NAME, "tangtang");
    /**小男孩*/
//        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_NAME, "boy");
    /**唯美*/
//        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_NAME, "sweet");
    /**男生*/
//        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_NAME, "xiaoming");
    private void speech() {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM)) {
            if (uscTts == null) {
                uscTts = new com.unisound.client.SpeechSynthesizer(AppUtils.getAppContext(),
                        UscConfig.appKey, UscConfig.secret);
                uscTts.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants
                        .TTS_SERVICE_MODE_NET);
                uscTts.setOption(SpeechConstants.TTS_KEY_VOICE_NAME, "tangtang");
//                uscTts.setOption(SpeechConstants.TTS_KEY_VOICE_VOLUME, "");
                uscTts.init("");
            }
            uscTts.setTTSListener(synthesizer);
        } else {
            if (iflytekTts == null) {
                iflytekTts = SpeechSynthesizer.createSynthesizer(AppUtils.getAppContext(),
                        mTtsInitListener);
                return;
            }
        }
        play();
    }

    private void play() {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM)) {
            int code = uscTts.playText(text);
            setStatus(Status.STATES_PLAYING);
            Logger.i("Listener stop");
            RecognizerManager.getInstance().release();
        } else {
            int code = iflytekTts.startSpeaking(text, synthesizer);
            if (code != ErrorCode.SUCCESS) {
                setStatus(Status.STATES_STOP);
                Logger.i("语音合成失败,错误码: " + code);
            } else {
                setStatus(Status.STATES_PLAYING);
                Logger.i("Listener stop");
                RecognizerManager.getInstance().release();
            }
        }
    }

    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Logger.i("InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Logger.i("初始化失败,错误码：" + code);
            } else {
                setParameter();
                play();
            }
        }
    };

    public void setText(String text) {
        this.text = text;
    }

    private void setSynthesizer(Synthesizer synthesizer) {
        this.synthesizer = synthesizer;
    }

    private void setParameter() {
        String speed = "50"; // 合成语速
        String pitch = "50"; // 合成语调
        String volume = "100"; // 合成音量
        String streamType = "3"; // 合成音量
        String requestFocus = "true";// 设置播放合成音频打断音乐播放，默认为true
        String audioFormat = "wav";// 保存音频格式支持pcm、wav
        // 设置音频保存路径
        String audioPath = PathConfig.getSpeechPath();
        // 默认云端发音人
        String voicerCloud = "xiaoyan";
        iflytekTts.setParameter(SpeechConstant.PARAMS, null);
        iflytekTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        iflytekTts.setParameter(SpeechConstant.VOICE_NAME, voicerCloud);
        iflytekTts.setParameter(SpeechConstant.SPEED, speed);
        iflytekTts.setParameter(SpeechConstant.PITCH, pitch);
        iflytekTts.setParameter(SpeechConstant.VOLUME, volume);
        iflytekTts.setParameter(SpeechConstant.STREAM_TYPE, streamType);
        iflytekTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, requestFocus);
        iflytekTts.setParameter(SpeechConstant.AUDIO_FORMAT, audioFormat);
        iflytekTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, audioPath);
    }

    private int getCunrrentStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static void init(Context context) {
//        if (!PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM)) {
            StringBuffer param = new StringBuffer();
            param.append("appid=" + context.getString(R.string.app_id));
            param.append(",");
            // 设置使用v5+
            param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
            SpeechUtility.createUtility(context, param.toString());
//        }
    }

    public static int getStatus() {
        return getInstance().getCunrrentStatus();
    }

    /**
     * 是否正在播报
     *
     * @return
     */
    public boolean isSpeeching() {
        return getStatus() == Status.STATES_PLAYING;
    }

    /**
     * 暂停
     */
    public void pause() {
        pauseSpeak();
        if (getStatus() == Status.STATES_PLAYING) {
            setStatus(Status.STATES_PAUSE);
        }
        Logger.i("Listener start");
//        RecognizerManager.getInstance().startListening();
    }

    /**
     * 停止播报
     *
     * @param isListen 停止播报是否开启监听
     */
    public void stop(boolean isListen) {
        stopSpeak();
        if (getStatus() == Status.STATES_PAUSE || getStatus() == Status.STATES_PLAYING) {
            setStatus(Status.STATES_STOP);
        }
        if (isListen) {
            Logger.i("Listener start");
            RecognizerManager.getInstance().startListening();
        }
    }

    /**
     * 恢复播报
     */
    public void resume() {
        getInstance().resumeSpeak();
        if (getStatus() == Status.STATES_PAUSE) {
            getInstance().setStatus(Status.STATES_PLAYING);
        }
        Logger.i("Listener stop");
        RecognizerManager.getInstance().release();
    }

    private void resumeSpeak() {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM)) {
            if (uscTts != null) {
                uscTts.resume();
            }
        } else {
            if (iflytekTts != null) {
                iflytekTts.resumeSpeaking();
            }
        }
    }

    private void stopSpeak() {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM)) {
            if (uscTts != null) {
                uscTts.stop();
                uscTts.cancel();
            }
        } else {
            if (iflytekTts != null) {
                iflytekTts.stopSpeaking();
            }
        }
    }

    private void pauseSpeak() {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM)) {
            if (uscTts != null) {
                uscTts.pause();
            }
        } else {
            if (iflytekTts != null) {
                iflytekTts.pauseSpeaking();
            }
        }
    }

    public void release() {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM)) {
            if (uscTts != null) {
                uscTts.stop();
                uscTts.release(SpeechConstants.TTS_RELEASE_ENGINE, null);
            }
        } else {
            if (iflytekTts != null) {
                iflytekTts.stopSpeaking();
                iflytekTts.destroy();
            }
        }
    }
}
