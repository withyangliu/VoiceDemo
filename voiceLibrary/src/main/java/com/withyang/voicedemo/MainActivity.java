package com.withyang.voicedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.withyang.voicedemo.iat.Recognizer;
import com.withyang.voicedemo.iat.RecognizerManager;
import com.withyang.voicedemo.tts.SpeechManager;
import com.withyang.voicedemo.tts.Synthesizer;
import com.withyang.voicedemo.utils.FileUtils;
import com.withyang.voicedemo.utils.Logger;
import com.withyang.voicedemo.utils.PermissionHelper;


import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private PermissionHelper mPermissionHelper;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.tv_text);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        Button start = (Button) findViewById(R.id.bt_start);
        Button end = (Button) findViewById(R.id.bt_end);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecognizerManager.getInstance().start(recognizer);
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SpeechManager.getInstance().isSpeeching()) {
                    SpeechManager.getInstance().stop(false);
                }else {
                    RecognizerManager.getInstance().pause();
                    RecognizerManager.getInstance().release();
                }
            }
        });

        requestPermission();
    }


    Recognizer recognizer = new Recognizer() {

        @Override
        public void onVolumeChanged(int volume) {
            Logger.e(volume + "");
            seekBar.setProgress(volume);

        }

        @Override
        public void onResult(String result) {
            SpeechManager.getInstance().speak(result, synthesizer);
            textView.setText(result);
        }
    };

    Synthesizer synthesizer = new Synthesizer() {
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

        }
    };


    private void requestPermission() {
        PermissionHelper.PermissionModel[] mPermissionModels =
                new PermissionHelper.PermissionModel[]{
                        PermissionHelper.PERMISSION_WRITE_EXTERNAL_STORAGE,
                        PermissionHelper.PERMISSION_RECORD_AUDIO,
                        PermissionHelper.PERMISSION_READ_PHONE,
    };
        mPermissionHelper = new PermissionHelper(this, mPermissionModels);
        mPermissionHelper.checkPermission(new PermissionHelper.OnResult() {
            @Override
            public void onNext() {
                FileUtils.deleteFile(new File(FileUtils.getSignInPath()));

            }

            @Override
            public void onCancel() {
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionHelper != null) {
            mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
