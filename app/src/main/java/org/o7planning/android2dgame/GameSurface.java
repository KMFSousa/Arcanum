package org.o7planning.android2dgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
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
    private boolean gameStarted = false;
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
        initDungeon();
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



    public void initDungeon(){
        StuffFactory stuffFactory = new StuffFactory(this);

        this.dungeon = new Dungeon(this);

        Character player = stuffFactory.newPlayer(this.characterList, 850, 500);

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

        this.dungeon.getCurrentRoom().monsterList = new ArrayList<Character>(mapArr[2][0].monsterList);
    }
    // Implements method of SurfaceHolder.Callback

    @Override
    // The surfaceCreated method is called immediately after the surface is first created (in MainActivity)
    // The gameThread will be what calls the update method on the character
    public void surfaceCreated(SurfaceHolder holder) {

        // Create a thread that will handle the running of the game (character movements and such) that can be easily paused without having to add excess logic to the main thread
        if(gameThread == null) {
            gameThread = new GameThread(this, holder);
        }

        if (!gameStarted) {
            gameThread.start();
            gameStarted = true;
        }
        gameThread.setRunning(true);


    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameThread.setRunning(false);
    }

    public void setRunning(boolean running){
        gameThread.setRunning(running);
    }

    public boolean isRunning(){
        return gameThread.getRunning();
    }

    public long getLastPauseTime(){
        return gameThread.getLastPauseTime();
    }

    public void setLastPauseTime(long newPauseTime) {
        gameThread.setLastPauseTime(newPauseTime);
    }

}
