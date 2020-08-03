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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends Activity {

    private GameSurface gameSurface;
    private int difficultySetting = 1;
    public final int RECORD_AUDIO = 0, WRITE_EXTERNAL_STORAGE = 1, MAX_DIFF = 3;
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

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);

    }
    protected void mainMenu() {
        difficultySetting = 1;
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

    private void setHUDVisible(boolean newVisible){
        RelativeLayout myLayout = findViewById(R.id.game_hud);
        View js = myLayout.findViewById(R.id.joystickView);
        View js2 = myLayout.findViewById(R.id.joystickView2);
        View pb = myLayout.findViewById(R.id.pauseButton);
        View tw = myLayout.findViewById(R.id.attackToggleButton);
        int visibleInt = newVisible ? View.VISIBLE : View.GONE;
        js.setVisibility(visibleInt);
        js2.setVisibility(visibleInt);
        pb.setVisibility(visibleInt);
        tw.setVisibility(visibleInt);
    }

    protected void pauseMenu(final RelativeLayout myLayout) {
        gameSurface.setRunning(false);
        gameSurface.setPausedByPlayer(true);
        final View child = getLayoutInflater().inflate(R.layout.pause_menu, null);
        myLayout.addView(child);
        setHUDVisible(false);

        final Button resumeGameButton = findViewById(R.id.resumeGameButton);
        resumeGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                setHUDVisible(true);
                myLayout.removeView(child);
                gameSurface.setRunning(true);
                gameSurface.setPausedByPlayer(false);
            }
        });

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
        gameSurface.setRunning(false);
        final View child = getLayoutInflater().inflate(R.layout.death_menu, null);
        ViewGroup parent = findViewById(R.id.game_hud);
        parent.addView(child);
        setHUDVisible(false);

        final Button restartGameButton = findViewById(R.id.restartGameButton);
        restartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startGame();
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
        gameSurface.setRunning(false);
        final View child = getLayoutInflater().inflate(R.layout.victory_menu, null);
        ViewGroup parent = findViewById(R.id.game_hud);
        parent.addView(child);
        setHUDVisible(false);

        final Button restartGameButton = findViewById(R.id.nextLevelButton);
        if(difficultySetting < MAX_DIFF) {
            difficultySetting++;
        } else {
            restartGameButton.setText("Restart");
        }
        restartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startGame();
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

    protected void startGame() {
        gameSurface = null;
        //TODO: Add View that says "Loading....."
        this.setContentView(R.layout.game_hud);

        gameSurface = new GameSurface(this, difficultySetting);
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
                    character.setMovingVector((int)movingVectorX, (int)movingVectorY);
                }
            }
        });

        JoystickView attackJoystick = findViewById(R.id.joystickView2);
        attackJoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                double radAngle = angle * Math.PI/180;
                for (Character character: gameSurface.characterList) {
                    PlayerAI playerAI = (PlayerAI) character.ai;

                    if (strength >= 50) {
                        double attackVectorX = Math.cos(radAngle) * 10;
                        double attackVectorY = Math.sin(radAngle) * -10;

                        // Set character attack vector
                        playerAI.setAttackVector((int) attackVectorX, (int) attackVectorY);
                        playerAI.attack();
                    } else {
                        playerAI.setAttackVector(0, 0);
                    }
                }
            }
        });

        final ImageButton attackToggleButton = findViewById(R.id.attackToggleButton);
        attackToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Character character: gameSurface.characterList) {
                    PlayerAI playerAI = (PlayerAI) character.ai;
                    if (playerAI.attackStyle == "Melee") {
                        playerAI.attackStyle = "Ranged";
                        attackToggleButton.setImageResource(R.drawable.melee_icon);
                    } else {
                        playerAI.attackStyle = "Melee";
                        attackToggleButton.setImageResource(R.drawable.ranged_icon);
                    }
                }
            }
        });


        final ImageButton pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                // TODO: Pause the Game. Stop movement, stop drawing?

                pauseMenu(myLayout);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initializeScreenRecorder();
        }

    }

    private void initializeScreenRecorder(){
        screenRecorder = new ScreenRecorder(this);
        if(!screenRecorder.isPrepared) return;
        final ImageButton shareButton = findViewById(R.id.shareButton);
        shareButton.setVisibility(View.VISIBLE);
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
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Screen Recorder disabled: No storage access!", Toast.LENGTH_SHORT).show();
                }
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