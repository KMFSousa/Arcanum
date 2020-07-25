package org.o7planning.android2dgame;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends Activity {

    public static Button healthBar;
    public final int RECORD_AUDIO = 0, WRITE_EXTERNAL_STORAGE = 1;
    private ScreenRecorder screenRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.activity_main);
        RelativeLayout myLayout = findViewById(R.id.main);
        final GameSurface gameSurface = new GameSurface(this);
        myLayout.addView(gameSurface);

        JoystickView joystick = findViewById(R.id.joystickView);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                double radAngle = angle * Math.PI/180;
                for (Character character: gameSurface.characterList) {

                    double movingVectorX =  Math.cos(radAngle) * 10 * strength;
                    double movingVectorY = Math.sin(radAngle) * -10 * strength;
                    //Log.d("Joystick", angle + ": "+movingVectorX+", "+movingVectorY);
                    character.setMovingVector((int)movingVectorX, (int)movingVectorY);
                }
                // do whatever you want
            }
        });

        Button attackButton = findViewById(R.id.attackButton);
        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Character character: gameSurface.characterList) {
                    character.attack();
                }
            }
        });
        if (requestNecessaryPermissions()) {
            initializeScreenRecorder();
        }

    }
    private boolean requestNecessaryPermissions() {
        //returns true if all permissions are already granted
        boolean alreadyGranted = true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
            alreadyGranted = false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
            alreadyGranted = false;
        }
        return alreadyGranted;
    }

    private void initializeScreenRecorder(){
        Button shareButton = findViewById(R.id.shareButton);
        screenRecorder = new ScreenRecorder(this);
        screenRecorder.startRecording();
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenRecorder.stopRecording();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Permissions", requestCode+"");
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeScreenRecorder();
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
    }

}