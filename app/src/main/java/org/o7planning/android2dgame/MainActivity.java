package org.o7planning.android2dgame;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends Activity {

    private GameSurface gameSurface;
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

        mainMenu();

    }
    protected void mainMenu() {
        gameSurface = null;
        this.setContentView(R.layout.main_menu);

        final Button toggleButton = findViewById(R.id.startGameButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startGame();
            }
        });
    }

    protected void pauseMenu(final RelativeLayout myLayout) {
        gameSurface.setRunning(false);

        final View child = getLayoutInflater().inflate(R.layout.pause_menu, null);

        //TODO: Break this out into a function
        final View js = myLayout.findViewById(R.id.joystickView);
        final View js2 = myLayout.findViewById(R.id.joystickView2);
        final View pb = myLayout.findViewById(R.id.pauseButton);
        final View tw = myLayout.findViewById(R.id.attackToggleButton);
        js.setVisibility(View.GONE);
        js2.setVisibility(View.GONE);
        pb.setVisibility(View.GONE);
        tw.setVisibility(View.GONE);
        myLayout.addView(child);

        //TODO: Back stack?

        final Button resumeGameButton = findViewById(R.id.resumeGameButton);
        resumeGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                // TODO: Hide the pause Views or Remove
                js.setVisibility(View.VISIBLE);
                js2.setVisibility(View.VISIBLE);
                pb.setVisibility(View.VISIBLE);
                tw.setVisibility(View.VISIBLE);
                myLayout.removeView(child);
                gameSurface.setRunning(true);
            }
        }); // TODO: There's no way this works

        final Button mainMenuButton = findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                mainMenu();
            }
        });

        final Button startGameButton = findViewById(R.id.restartGameButton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startGame();
            }
        });
    }


    protected void deathScreen() {
        final View child = getLayoutInflater().inflate(R.layout.death_menu, null);
        ViewGroup parent = findViewById(R.id.game_hud);
        parent.addView(child);
        //TODO: Set the game_hud to GONE

        final Button restartGameButton = findViewById(R.id.restartGameButton);
        restartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                return; //TODO: Placeholder for now
            }
        });

        final Button mainMenuButton = findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                mainMenu();
            }
        });
    }

    protected void victoryScreen(){
        //TODO: This.
    }

    protected void startGame() {
        gameSurface = null;
        //TODO: Add View that says "Loading....."
        this.setContentView(R.layout.game_hud);
        // TODO: Possibly need to set to visable again

        gameSurface = new GameSurface(this);
        final RelativeLayout myLayout = findViewById(R.id.game_hud);
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

        final Button attackToggleButton = findViewById(R.id.attackToggleButton);
        attackToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Character character: gameSurface.characterList) {
                    PlayerAI playerAI = (PlayerAI) character.ai;
                    if (playerAI.attackStyle == "Melee") {
                        playerAI.attackStyle = "Ranged";
                        attackToggleButton.setText("Melee");
                    } else {
                        playerAI.attackStyle = "Melee";
                        attackToggleButton.setText("Ranged");
                    }
                }
            }
        });


        final Button pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                // TODO: Pause the Game. Stop movement, stop drawing?

                pauseMenu(myLayout);
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