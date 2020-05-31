package com.myproject.mytranslator.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.myproject.mytranslator.PlayState;
import com.myproject.mytranslator.R;
import com.myproject.mytranslator.TextPlayer;

import java.util.Locale;

public class AddEnglishTextActivity extends AppCompatActivity implements TextPlayer, View.OnClickListener {

    private final Bundle params = new Bundle();
    private final BackgroundColorSpan colorSpan = new BackgroundColorSpan(Color.YELLOW);
    private TextToSpeech tts;
    private Button playBtn;
    private Button stopBtn;
    private EditText inputEditText;

    private PlayState playState = PlayState.STOP;
    private Spannable spannable;
    private int standbyIndex = 0;
    private int lastPlayIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_english_text);
        initTTS();
        initView();
    }

    private void initView() {
        playBtn = findViewById(R.id.btn_play);
        stopBtn = findViewById(R.id.btn_stop);
        inputEditText = findViewById(R.id.et_input);


        playBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
    }

    private void initTTS() {
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int state) {
                if (state == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.ENGLISH);
                } else {
                    showState("TTS 객체 초기화 중 문제가 발생했습니다.");
                }
            }
        });

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                clearAll();
            }

            @Override
            public void onError(String s) {
                showState("재생 중 에러가 발생했습니다.");
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                /*changeHighlight(standbyIndex + start, standbyIndex + end);
                lastPlayIndex = start;*/
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                startPlay();
                break;


            case R.id.btn_stop:
                stopPlay();
                break;
        }
        showState(playState.getState());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void startPlay() {
        String content = inputEditText.getText().toString();
        if (playState.isStopping() && !tts.isSpeaking()) {
            startSpeak(content);
        }
        /*else if (playState.isWaiting()) {
            standbyIndex += lastPlayIndex;
            startSpeak(content.substring(standbyIndex));
        }*/
        playState = PlayState.PLAY;
    }

    @Override
    public void pausePlay() {
        if (playState.isPlaying()) {
            playState = PlayState.WAIT;
            tts.stop();
        }
    }

    @Override
    public void stopPlay() {
        tts.stop();
        clearAll();
    }

    /*private void changeHighlight(final int start, final int end) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spannable.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        });
    }*/



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startSpeak(final String text) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, params, text);
    }

    private void clearAll() {
        playState = PlayState.STOP;
        standbyIndex = 0;
        lastPlayIndex = 0;

        /*if (spannable != null) {
            changeHighlight(0, 0); // remove highlight
        }*/
    }

    private void showState(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (playState.isPlaying()) {
            pausePlay();
        }
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        if (playState.isWaiting()) {
            startPlay();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        tts.stop();
        tts.shutdown();
        super.onDestroy();
    }

    public void onClickSaveText(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddEnglishTextActivity.this);
        builder.setMessage(inputEditText.getText().toString()+"를 저장하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra("EnglishText", inputEditText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();

    }
}
