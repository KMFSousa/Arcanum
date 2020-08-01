package org.o7planning.android2dgame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends Activity {

    public static Button healthBar;

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

        final Button toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
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

//        Button healthBar = findViewById(R.id.healthBar);
//        this.healthBar = healthBar;
    }

}