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
    static RouteController route;
    boolean destinationMode = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculating);

        Intent myIntent = getIntent();
        if(myIntent.getStringExtra("input").equals("current_location")) {
            destinationMode = false;
        }

        route = new RouteController(this);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.ENGLISH);

                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        // success
                        if(destinationMode)
                            rooms = getResources().getStringArray(R.array.destinationRooms);
                        else
                            rooms = getResources().getStringArray(R.array.sourceRooms);
                        String dst = homepage.strDst;
                        dst = dst.replaceAll("\\s","");
                        int roomIndex = findRoom(dst);
                        // TODO : make sure dst is not the same as src
                        if(roomIndex < 0) {
                            rooms = getResources().getStringArray(R.array.multiEntranceRooms);
                            roomIndex = findRoom(dst);
                            if(roomIndex < 0) {
                                rooms = getResources().getStringArray(R.array.sourceRooms);
                                roomIndex = findRoom(dst);
                                if(roomIndex < 0) {
                                    speak("Room not found, please try again.");
                                    Intent intent = new Intent(calculating.this, homepage.class);
                                    if(destinationMode)
                                        intent.putExtra("input", "destination");
                                    else
                                        intent.putExtra("input", "current_location");
                                    startActivity(intent);
                                } else {
                                    route.setCurrLocation(dst);
                                    speak("Calculating best route to your destination. Please wait.");
                                    route.debug();
                                    route.calculateRoute();
                                    waitTTS();
                                    Intent intent = new Intent(calculating.this, navigation.class);
                                    startActivity(intent);
                                }
                            } else {
                                speak("Room has multiple entrances, please specify which.");
                                Intent intent = new Intent(calculating.this, homepage.class);
                                intent.putExtra("input", "current_location");
                                startActivity(intent);
                            }
                        } else {
                            if(destinationMode) {
                                route.setDestination(dst);
                                speak("Please enter your current location.");
                                Intent intent = new Intent(calculating.this, homepage.class);
                                intent.putExtra("input", "current_location");
                                startActivity(intent);
                            } else {
                                route.setCurrLocation(dst);
                                speak("Calculating best route to your destination. Please wait.");
                                route.debug();
                                route.calculateRoute();
                                waitTTS();
                                Intent intent = new Intent(calculating.this, navigation.class);
                                startActivity(intent);
                            }

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
        for(int i = 0; i < rooms.length; i++) {
            if(r.equalsIgnoreCase(rooms[i]))
                return i;
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

    private void waitTTS() {
        boolean speakingEnd = tts.isSpeaking();
        do{
            speakingEnd = tts.isSpeaking();
        } while (speakingEnd);
    }
}
