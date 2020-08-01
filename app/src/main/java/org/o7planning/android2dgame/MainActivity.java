package org.o7planning.android2dgame;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends Activity {

    private RelativeLayout[] layouts = new RelativeLayout[3];
    private GameSurface gameSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        RelativeLayout[] layouts = new RelativeLayout[3];

//        this.layouts[0] = findViewById(R.id.main_menu);;
//        this.layouts[1] = findViewById(R.id.pause_menu);;
//        this.layouts[2] = findViewById(R.id.game_hud);;

        // Set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mainMenu();

    }
    protected void mainMenu() {
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

//        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = vi.inflate(R.layout.pause_menu , null);
//
//        ViewGroup container = (ViewGroup) myLayout;
//        container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

//        this.setContentView(container);
        final View child = getLayoutInflater().inflate(R.layout.pause_menu, null);

//        View game_hud = myLayout.findViewById(R.id.game_hud);
//        ((ViewGroup) game_hud.getParent()).removeView(game_hud);

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
        //layouts[2].setVisibility(View.VISIBLE);
        //this.setContentView(R.layout.pause_menu);

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
//
//    protected void deathScreen() {
//        this.setContentView(R.layout.pause_menu);
//
//        final Button restartGameButton = findViewById(R.id.restartGameButton);
//        restartGameButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View view) {
//                return;
//            }
//        });
//
//        final Button mainMenuButton = findViewById(R.id.mainMenuButton);
//        mainMenuButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View view) {
//                mainMenu();
//            }
//        });
//    }

    protected void startGame() {
        this.setContentView(R.layout.game_hud);

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
            public void onClick (View view) {
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
                //layouts[1].setVisibility(View.GONE);
                pauseMenu(myLayout);
//                for (Character character : gameSurface.characterList) {
//                    PlayerAI playerAI = (PlayerAI) character.ai;
//                }
            }
        });

        // TODO: Remove, this is old
        //Button attackButton = findViewById(R.id.attackButton);
        //attackButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        for (Character character: gameSurface.characterList) {
        //            character.ai.attack();
        //        }
        //    }
        //});
        
    }

}