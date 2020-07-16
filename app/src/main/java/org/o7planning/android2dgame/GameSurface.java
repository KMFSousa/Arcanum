package org.o7planning.android2dgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    public Dungeon dungeon;
    private Context context;
    public List<Character> characterList = new ArrayList<Character>();
    public List<Explosion> explosionList = new ArrayList<Explosion>();
    public List<Character> removalList = new ArrayList<Character>();
    public List<Item> itemList = new ArrayList<Item>();
    public GameSurface(Context context)  {
        super(context);

        this.context = context;

        // Make Game Surface focusable so it can handle events.
        this.setFocusable(true);

        // Set callback.
        this.getHolder().addCallback(this);
    }

    // MASTER UPDATE CONTROL
    // CALL ALL UPDATE METHODS FOR OBJECTS HERE
    public void update() {

        for (Character monster : this.dungeon.getCurrentRoom().monsterList) {
            monster.update(this.dungeon.getCurrentRoom());
        }

        for (Explosion explosion : this.explosionList) {
            explosion.update();
        }

        Iterator<Explosion> iterator = this.explosionList.iterator();
        while (iterator.hasNext()) {
            Explosion explosion = iterator.next();

            if (explosion.isFinish()) {
                iterator.remove();
            }
        }

        this.dungeon.getCurrentRoom().monsterList.removeAll(removalList);

        for (Character character : characterList) {
            if(!characterList.isEmpty()){
                character.update(this.dungeon.getCurrentRoom());

                //TODO: Dont uncomment this, it breaks movement
//                if(characterList.get(0).hitPoints > 0){
//                    MainActivity.healthBar.setText("" + characterList.get(0).hitPoints + " HP");
//
//                }else{
//                    MainActivity.healthBar.setText("DEAD :(");
//                }
            }
        }

        characterList.removeAll(removalList);
    }

    @Override
    // MASTER DRAW CONTROL
    // CALL ALL DRAW METHODS FOR OBJECTS HERE
    public void draw(Canvas canvas) {
        super.draw(canvas);

        this.dungeon.draw(canvas);

        // Call the draw method implemented in each class, responsible for drawing the bitmap of the model in question
        for (Character character : characterList) {
            character.draw(canvas);
        }

        for (Character monster : this.dungeon.getCurrentRoom().monsterList) {
            monster.draw(canvas);
        }

        for (Item item : itemList) {
            item.draw(canvas);
        }

        for (Explosion explosion : explosionList) {
            explosion.draw(canvas);
        }
    }


    public void removeCharacter(Character other) {
        removalList.add(other);
    }

    // Implements method of SurfaceHolder.Callback

    @Override
    // The surfaceCreated method is called immediately after the surface is first created (in MainActivity)
    // The gameThread will be what calls the update method on the character
    public void surfaceCreated(SurfaceHolder holder) {

        StuffFactory stuffFactory = new StuffFactory(this);

        this.dungeon = new Dungeon(this);

        Character player = stuffFactory.newPlayer(this.characterList, 200, 200);

        Map[][] mapArr = new Map[3][3];

        Bitmap mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.blue_room);
        Map map = new Map(this, mapImage, "blue_room", stuffFactory, context);
        mapArr[0][0] = map;

        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.red_room);
        map = new Map(this, mapImage, "red_room", stuffFactory, context);
        mapArr[1][0] = map;

        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.box_room);
        map = new Map(this, mapImage, "box_room", stuffFactory, context);
        mapArr[0][1] = map;

        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.green_room);
        map = new Map(this, mapImage, "green_room", stuffFactory, context);
        mapArr[2][0] = map;

        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.sewers);
        map = new Map(this, mapImage, "sewers", stuffFactory, context);
        mapArr[1][1] = map;

        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.boss_room);
        map = new Map(this, mapImage, "boss_room", stuffFactory, context);
        mapArr[1][2] = map;

        this.dungeon.populateMapArray(mapArr);

        this.dungeon.getCurrentRoom().monsterList = new ArrayList<Character>(mapArr[0][0].monsterList);

        // Create a thread that will handle the running of the game (character movements and such) that can be easily paused without having to add excess logic to the main thread
        this.gameThread = new GameThread(this, holder);
        // Call the setRunning method to set the `running` variable of the game thread to true: this is what will control the pausing of the thread
        this.gameThread.setRunning(true);
        // Call the already implemented `start()` method of the `Thread` superclass which will call the overrided `run()` method of our `GameThread` subclass
        this.gameThread.start();
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                this.gameThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = true;
        }
    }
}
