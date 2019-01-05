# 实现循环语音识别、合成简单封装
## 需求背景
最近项目上遇到一个需求：语音识别获取文字后后台交互获取结果并语音合成播报
## 需求拆解
- 语音识别（可打断识别）
- 语音播报（可打断，打断后开启语音识别）
```
graph LR
A[开始识别]-->B[是否识别成功]
B-->|是|C[识别成功]
B-->|否|D[识别失败]
D-->|重新识别|A
C-->E[语音合成]
E-->A
```
## 代码解析
1、此demo中集成了讯飞和云知声两个平台，此处解析以讯飞为例展开，云知声解析在代码中有注释；

```
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
                onResult(result);//识别到结果
                result = "";
            } else {
                result = "";
                Logger.i("Listener start");
               RecognizerManager.getInstance().startListening();//若没有识别到结果再次开启识别
            }
        }
    }
    
```
识别失败再次开启识别

```
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
```
音量回调方法

```
    @Override
    public void onVolumeChanged(int volume, byte[] data) {
        onVolumeChanged(volume);
    }

```
2、语音合成部分
播放完成开始识别
```
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
```
3、简单封装的语音管理类RecognizerManager、SpeechManager实现逻辑上的切换
#### RecognizerManager
1、初始化
```
  private void init() {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM)) {//可配置的语音引擎
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
```
2、生命周期绑定

```
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
    
    public void pause() {
        if (PlatformConfig.PLATFORM_USC.equals(PlatformConfig.PLATFORM_USC)) {
            if (mUnderstander != null) {
                mUnderstander.stop();
                mUnderstander.cancel();
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
```
#### SpeechManager
1、合成方法
```
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
```
2、生命周期绑定

```
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
```
### SDK替换
demo的讯飞可直接使用，[云知声需要申请appkey](http://dev.hivoice.cn/sdk_download/schema_sdk.jsp)
### 友情链接
1. [讯飞开发文档](https://doc.xfyun.cn/msc_android/index.html)

2. [云知声文档](http://admin-web-files.ks3-cn-shanghai.ksyun.com/docs/sdk/USC_DevelGuide_Android_common.pdf)

### [demo地址](https://github.com/withyangliu/VoiceDemo)