package com.example.slim_walking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class calculating extends AppCompatActivity {
    private TextToSpeech tts;
    private String[] rooms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculating);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.ENGLISH);

                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        // success
                        speak("Calculating best route to your destination. Please wait.");
                        rooms = getResources().getStringArray(R.array.rooms);
                        String dst = homepage.strDst;
                        dst = dst.replaceAll("\\s","");
                        int roomIndex = findRoom(dst);
                        if(roomIndex < 0) {
                            speak("Room not found, please try again.");
                            Intent intent = new Intent(calculating.this, homepage.class);
                            startActivity(intent);
                        } else {


                            Intent intent = new Intent(calculating.this, navigation.class);
                            startActivity(intent);
                        }
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

    }

    // text-to-speech function
    private void speak(String text) {
        tts.setPitch(1);
        tts.setSpeechRate(1);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private int findRoom(String r) {
        int first = 0, last = rooms.length -1;
        while(first <= last) {
            int mid = first + (last - first)/2;
            if(rooms[mid].equals(r))
                return mid;
            if(r.compareTo(rooms[mid]) < 0)
                last = mid - 1;
            if(r.compareTo(rooms[mid]) > 0)
                first = mid + 1;
        }
        return -1;
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
