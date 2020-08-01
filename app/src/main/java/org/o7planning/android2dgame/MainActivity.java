package org.o7planning.android2dgame;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends Activity {

    public static Button healthBar;
    public final int RECORD_AUDIO = 0, WRITE_EXTERNAL_STORAGE = 1;
    private ScreenRecorder screenRecorder;
    private GameSurface gameSurface;

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
        gameSurface = new GameSurface(this);
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

        JoystickView attackJoystick = findViewById(R.id.joystickView2);
        attackJoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                double radAngle = angle * Math.PI/180;
                for (Character character: gameSurface.characterList) {
                    PlayerAI playerAI = (PlayerAI) character.ai;

                    Log.i("Blah", "Angle: " + angle + " Strength: " + strength);

                    if (strength >= 90) {
                        double attackVectorX = Math.cos(radAngle) * 10;
                        double attackVectorY = Math.sin(radAngle) * -10;

                        // Set character attack vector
                        playerAI.setAttackVector((int) attackVectorX, (int) attackVectorY);
                        playerAI.attack();
                    }
                }
            }
        });

        final Button toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Character character: gameSurface.characterList) {
                    PlayerAI playerAI = (PlayerAI) character.ai;
                    if (playerAI.attackStyle == "Melee") {
                        playerAI.attackStyle = "Ranged";
                        toggleButton.setText("Melee");
                    } else {
                        playerAI.attackStyle = "Melee";
                        toggleButton.setText("Ranged");
                    }
                }
            }
        });

        if (requestNecessaryPermissions()) {
            initializeScreenRecorder();
        }

    }
    private boolean requestNecessaryPermissions() {
        //returns true if all permissions are already granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    private void initializeScreenRecorder(){
        final ImageButton shareButton = findViewById(R.id.shareButton);
        screenRecorder = new ScreenRecorder(this);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!screenRecorder.isRecording) {
                    if (screenRecorder.startRecording()){
                        shareButton.setImageResource(R.drawable.share_icon_stop);
                    }

                } else {
                    if (screenRecorder.stopRecording()){
                        shareButton.setImageResource(R.drawable.share_icon_start);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeScreenRecorder();
                } else {

                }
        }
    }

        //Button attackButton = findViewById(R.id.attackButton);
        //attackButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        for (Character character: gameSurface.characterList) {
        //            character.ai.attack();
        //        }
        //    }
        //});

        //TODO: Dont uncomment this, it breaks movement

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        screenRecorder.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        screenRecorder.onDestroy();
    }

}