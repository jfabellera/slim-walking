package com.example.slim_walking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class navigation extends FragmentActivity implements OnMapReadyCallback {

    private ArrayList<String> instructionQueue;
    private ArrayList<Integer> counterQueue;
    int counter = 0;
    Toast toast;
    private double[] directions;
    private TextToSpeech tts;
    private static final int RECOGNIZER_RESULT = 1;
    EmergencyController emergency;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageButton inputButton;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        createQueue(calculating.route.route);
        emergency = new EmergencyController(this);
        inputButton = findViewById(R.id.imageButton);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.ENGLISH);

                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        // success
                        speak("When you have taken a step or turned, tap the screen to hear your next instruction.");
                        nextInstruction();
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        inputButton.setOnTouchListener(new View.OnTouchListener() {
            Timer t, t2;
            boolean held;
            long start = 0, curr = 0;
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        inputButton.setAlpha(0.85f);
                        start = curr;
                        curr = System.currentTimeMillis();

                        //start timer
                        t = new Timer();
                        held = false;
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                speak(""); // cancel tts if tts is running
                                held = true;
                            }
                        }, 1000); //time out 1s
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                finishActivity(RECOGNIZER_RESULT);
                                speak("Continue holding button for 5 more seconds for emergency.");
                                held = true;
                            }
                        }, 3000); //time out 1s
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                // TODO : launch phone app and call
                                speak(emergency.callLocal());
                                held = true;
                            }
                        }, 8000); //time out 1s
                        return true;
                    case MotionEvent.ACTION_UP:
                        inputButton.setAlpha(1f);
                        t.cancel();
                        if(!held) {
                            // put next function here
                            nextInstruction();
                        }
                        return true;
                }
                return false;
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();


    }

    private void createQueue(Route route) {
        int currHeading = 0;
        int steps;
        String angleDir = "";
        String temp;
        instructionQueue = new ArrayList<>();
        counterQueue = new ArrayList<>();
        do {
            directions = route.next();
            if(directions[0] > 0) { // edge
                steps = (int)directions[0]/5;
                if(currHeading != (int)directions[1]) {

                    int angleDifference = (int)directions[1] - currHeading;
                    if (angleDifference >= 180) {
                        angleDifference -= 180;
                        angleDifference *= -1;
                    } else if(angleDifference <= -180) {
                        angleDifference += 360;
//                        angleDifference *= -1;
                    }
                    if(angleDifference != 0) {
                        if(angleDifference < 0)
                            angleDir = "to the left";
                        else
                            angleDir = "to the right";
                        currHeading += angleDifference;
                        if(currHeading == -180)
                            currHeading = 180;

                        temp = "Turn " + Math.abs(angleDifference) + " degrees " + angleDir;
                        instructionQueue.add(temp);
                        counterQueue.add(-1);
                    }

                }
                temp = "Take " + steps + " steps forward";
                instructionQueue.add(temp);
                counterQueue.add(steps);
            }
        } while(directions[0] != -1);
    }

    private void nextInstruction() {
        if(counterQueue.isEmpty()) {

            speak("You have reached your destination!");
            return;
        }
        if(counterQueue.get(0) == -1) {
            speak(instructionQueue.remove(0));
            counterQueue.remove(0);
        } else if (counter == 0){
            speak(instructionQueue.get(0));
            counter = counterQueue.get(0);
        } else {
            counter--;
            if(counter == 0) {
                instructionQueue.remove(0);
                counterQueue.remove(0);
                speak("stop");
            } else {
                speak(""+counter);
            }
        }

    }

    private void fetchLastLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(navigation.this);
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I Am Here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 1));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }

    // text-to-speech function
    private void speak(String text) {
        tts.setPitch(1);
        tts.setSpeechRate(1);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        toast(text);
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

    private void toast(String s) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        toast.show();
    }
}
