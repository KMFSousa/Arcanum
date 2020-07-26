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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    public Dungeon dungeon;
    private Context context;
    public final List<Character> characterList = new ArrayList<Character>();
    public final List<Explosion> explosionList = new ArrayList<Explosion>();
    public final List<Character> monsterList = new ArrayList<Character>();
    public final List<Character> removalList = new ArrayList<Character>();
    public final List<Integer> upgradeList = new ArrayList<Integer>();
    public final List<Item> itemList = new ArrayList<Item>();
    public LootTables lootTables;
    public Character player;
    public GameSurface(Context context)  {
        super(context);

        this.context = context;

        // Make Game Surface focusable so it can handle events.
        this.setFocusable(true);

        // Set callback.
        this.getHolder().addCallback(this);
        this.lootTables = new LootTables(this.context);
    }

    // MASTER UPDATE CONTROL
    // CALL ALL UPDATE METHODS FOR OBJECTS HERE
    public void update() {
        Map currentMap = dungeon.getCurrentRoom();
        for (Character character : characterList) {
            if(!characterList.isEmpty()){
                character.update(currentMap);

            }
        }

        for (Character monster : monsterList) {
            monster.update(currentMap);
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
        if(!upgradeList.isEmpty()) {
            lootTables.applyItems(upgradeList, this.player);
            upgradeList.clear();
        }
        monsterList.removeAll(removalList);
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

        for (Character monster : monsterList) {
            monster.draw(canvas);
        }

        for (Item item : itemList) {
            item.draw(canvas);
        }

        for (Explosion explosion : explosionList) {
            explosion.draw(canvas);
        }
    }


    private void createCreatures(StuffFactory stuffFactory, Dungeon dungeon) {
        Character player = stuffFactory.newPlayer(dungeon);
        this.player = player;
        Character monster = stuffFactory.newMonster(dungeon);
        Character orc = stuffFactory.newOrc(dungeon);
        //Item sword = stuffFactory.newSword();

    }

    public void removeCharacter(Character other) {
        removalList.add(other);
    }

    // Implements method of SurfaceHolder.Callback

    @Override
    // The surfaceCreated method is called immediately after the surface is first created (in MainActivity)
    // The gameThread will be what calls the update method on the character
    public void surfaceCreated(SurfaceHolder holder) {

        Map[][] mapArr = new Map[3][3];
        Bitmap mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.blue_room);
        Map map = new Map(this, mapImage, R.raw.blue_room, context);
        mapArr[0][0] = map;

        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.red_room);
        map = new Map(this, mapImage, R.raw.red_room, context);
        mapArr[1][0] = map;

        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.box_room);
        map = new Map(this, mapImage, R.raw.box_room, context);
        mapArr[0][1] = map;

        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.green_room);
        map = new Map(this, mapImage, R.raw.green_room, context);
        mapArr[2][0] = map;

        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.sewers);
        map = new Map(this, mapImage, R.raw.sewers, context);
        mapArr[1][1] = map;

        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.boss_room);
        map = new Map(this, mapImage, R.raw.boss_room, context);
        mapArr[1][2] = map;

        this.dungeon = new Dungeon(this, mapArr);

        StuffFactory stuffFactory = new StuffFactory(this, context);
        createCreatures(stuffFactory, dungeon);

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
