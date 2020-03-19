package com.example.slim_walking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class homepage extends AppCompatActivity {
    private TextToSpeech tts;
    private static final int RECOGNIZER_RESULT = 1;
    ImageButton inputButton;
    EditText dst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        inputButton = findViewById(R.id.inputButton);
        dst = findViewById(R.id.dst);

        // initialize text-to-speech variable
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.ENGLISH);

                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        // success
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        inputButton.setOnTouchListener(new View.OnTouchListener() {
            Timer t;
            boolean held;
//            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        inputButton.setAlpha(0.85f);
                        //start timer
                        t = new Timer();
                        held = false;
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                speak(""); // cancel tts if tts is running
                                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your destination");
                                startActivityForResult(speechIntent, RECOGNIZER_RESULT);

                                held = true;
                            }
                        }, 1000); //time out 5s
                        return true;
                    case MotionEvent.ACTION_UP:
                        inputButton.setAlpha(1f);
                        t.cancel();
                        if(!held) {
                            speak("Hold button to enter destination. Double tap to hear instructions.");
                        }
                        return true;
                }
                return false;
            }
        });



    }

    // text-to-speech function
    private void speak(String text) {
        tts.setPitch(1);
        tts.setSpeechRate(1);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            dst.setText(matches.get(0).toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
