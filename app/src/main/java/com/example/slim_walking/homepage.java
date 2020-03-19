package com.example.slim_walking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class homepage extends AppCompatActivity {
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

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



        ImageButton inputButton = findViewById(R.id.inputButton);
        inputButton.setOnTouchListener(new View.OnTouchListener() {
            Timer t;
            boolean held;
//            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //start timer
                        t = new Timer();
                        held = false;
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                System.out.println("held");
                                held = true;
                            }
                        }, 1000); //time out 5s
                        return true;
                    case MotionEvent.ACTION_UP:
                        t.cancel();
                        if(!held) {
                            speak("Hold button to enter destination. Triple tap to hear instructions.");
                        }
                        return true;
                }
                return false;
            }
        });



    }
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
}
