package org.o7planning.android2dgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    public Dungeon dungeon;
    private Context context;
    public final List<Character> characterList = new ArrayList<Character>();
    public final List<Explosion> explosionList = new ArrayList<Explosion>();
    public final List<Character> monsterList = new ArrayList<Character>();
    public final List<Character> removalList = new ArrayList<Character>();
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
        Map currentMap = dungeon.getCurrentRoom();
        for (Character character : characterList) {
            character.update(currentMap);
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

        monsterList.removeAll(removalList);
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

        for (Explosion explosion : explosionList) {
            explosion.draw(canvas);
        }
    }

    private void createCreatures(CharacterFactory characterFactory) {
        Character player = characterFactory.newPlayer();
        Character monster = characterFactory.newMonster();
    }

    public void removeCharacter(Character other) {
        removalList.add(other);
    }

    // Implements method of SurfaceHolder.Callback
    @Override

    // The surfaceCreated method is called immediately after the surface is first created (in MainActivity)
    // The gameThread will be what calls the update method on the character
    public void surfaceCreated(SurfaceHolder holder) {

        CharacterFactory characterFactory = new CharacterFactory(this);
        createCreatures(characterFactory);

        Bitmap mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.testmap);

        Map map = new Map(this, mapImage, R.raw.main, context);
        Map[][] mapArr = new Map[2][2];
        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.red_room);
        Map map2 = new Map(this, mapImage, R.raw.red_room, context);
        mapImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.red_room);
        Map map3 = new Map(this, mapImage, R.raw.red_room, context);
        mapArr[1][0] = map2;
        mapArr[0][1] = map3;
        this.dungeon = new Dungeon(this, mapArr);

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
