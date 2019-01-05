package com.withyang.voicedemo.iat;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechUnderstander;
import com.unisound.client.SpeechUnderstanderListener;
import com.withyang.voicedemo.PlatformConfig;
import com.withyang.voicedemo.R;
import com.withyang.voicedemo.UscConfig;
import com.withyang.voicedemo.utils.AppUtils;
import com.withyang.voicedemo.utils.Logger;
import com.withyang.voicedemo.utils.PathConfig;
import com.withyang.voicedemo.utils.ToastUtils;

/**
 * 语音听写
 * Created by yunwen on 2017/12/21 0021.
 */

public class RecognizerManager {

    private SpeechRecognizer mIat;
    private static volatile RecognizerManager instance;

    private RecognizerListener mRecognizerListener;
    private SpeechUnderstanderListener mSpeechUnderstanderListener;

    private SpeechUnderstander mUnderstander;

    public static RecognizerManager getInstance() {
        if (instance == null) {
            synchronized (RecognizerManager.class) {
                if (instance == null) {
                    instance = new RecognizerManager();
                }
            }
        }
        return instance;
    }

    public SpeechUnderstander getUnderstander() {
        return mUnderstander;
    }

    public void start(Recognizer recognizer) {
        setRecognizerListener(recognizer);
        init();
    }

    private void init() {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM)) {
            mUnderstander = new SpeechUnderstander(AppUtils.getAppContext(), UscConfig.appKey,
                    UscConfig.secret);
            mUnderstander.setListener(mSpeechUnderstanderListener);
            setUscParam();
            mUnderstander.start();
        } else {
            mIat = SpeechRecognizer.createRecognizer(AppUtils.getAppContext(), mInitListener);
            Logger.d(mIat);
        }
    }

    public void setRecognizerListener(Recognizer recognizer) {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM)) {
            this.mSpeechUnderstanderListener = recognizer;
        } else {
            this.mRecognizerListener = recognizer;
        }
    }

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Logger.i("SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                ToastUtils.show("初始化失败，错误码：" + code);
            } else {
                setIflytekParam();
                Logger.i("Listener start");
                startListening();
            }
        }
    };

    public void startListening() {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM)) {
            if (mUnderstander == null) {
                init();
                return;
            }
            mUnderstander.start();
        } else {
            if (mIat == null) {
                init();
                return;
            }
            int ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                Logger.i("听写失败,错误码：" + ret);
            } else {
                Logger.i(AppUtils.getAppContext().getString(R.string.text_begin));
            }
        }
    }

    private void setUscParam() {
        mUnderstander.setOption(SpeechConstants.ASR_OPT_TEMP_RESULT_ENABLE, true);
        mUnderstander.init("");
        mUnderstander.setOption(SpeechConstants.ASR_SAMPLING_RATE, SpeechConstants
                .ASR_SAMPLING_RATE_BANDWIDTH_AUTO);
        // 修改识别领域
        mUnderstander.setOption(SpeechConstants.ASR_DOMAIN, "general");
        // 修改识别语音
        mUnderstander.setOption(SpeechConstants.ASR_LANGUAGE, SpeechConstants.LANGUAGE_MANDARIN);
    }

    private void setIflytekParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        // 即用户多长时间不说话则当做超时处理1000~10000
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        // 即用户停止说话多长时间内即认为不再输入， 自动停止录音0~10000
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, PathConfig.getRecognizerPath());
    }

    public void pause() {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM_USC)) {
            if (mUnderstander != null) {
                mUnderstander.stop();
            }
        } else {
            if (null != mIat) {
                mIat.stopListening();
            }
        }
    }

    public void release() {
        if (PlatformConfig.PLATFORM.equals(PlatformConfig.PLATFORM_USC)) {
            if (mUnderstander != null) {
                mUnderstander.stop();
                mUnderstander.cancel();
                mUnderstander.release(SpeechConstants.ASR_RELEASE_ENGINE, "");
            }
        } else {
            if (mIat != null) {
                mIat.stopListening();
                mIat.cancel();
                mIat.destroy();
            }
        }
    }
}
