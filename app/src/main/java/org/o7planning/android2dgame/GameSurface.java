package org.o7planning.android2dgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private Map map;
    private final List<Character> characterList = new ArrayList<Character>();
    private final List<Explosion> explosionList = new ArrayList<Explosion>();
    public GameSurface(Context context)  {
        super(context);

        // Make Game Surface focusable so it can handle events.
        this.setFocusable(true);

        // Set callback.
        this.getHolder().addCallback(this);
    }

    // MASTER UPDATE CONTROL
    // CALL ALL UPDATE METHODS FOR OBJECTS HERE
    public void update()  {
        for (Character character: characterList) {
            character.update();
        }

        for (Explosion explosion: this.explosionList) {
            explosion.update();
        }

        Iterator<Explosion> iterator = this.explosionList.iterator();
        while (iterator.hasNext()) {
            Explosion explosion = iterator.next();

            if (explosion.isFinish()) {
                iterator.remove();
            }
        }
    }

    @Override
    // MASTER DRAW CONTROL
    // CALL ALL DRAW METHODS FOR OBJECTS HERE
    public void draw(Canvas canvas)  {
        super.draw(canvas);

        this.map.draw(canvas);

        // Call the draw method implemented in each class, responsible for drawing the bitmap of the model in question
        for (Character character: characterList) {
            character.draw(canvas);
        }

        for (Explosion explosion: explosionList) {
            explosion.draw(canvas);
        }
    }

    // Implements method of SurfaceHolder.Callback
    @Override

    // The surfaceCreated method is called immediately after the surface is first created (in MainActivity)
    // The gameThread will be what calls the update method on the character
    public void surfaceCreated(SurfaceHolder holder) {
        Bitmap characterBitmap1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.chibi1);
        Character character1 = new Character(this, characterBitmap1,100,50);

        Bitmap characterBitmap2 = BitmapFactory.decodeResource(this.getResources(),R.drawable.chibi2);
        Character character2 = new Character(this, characterBitmap2,300,150);
        Bitmap backgroundBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.testmap);

        Map map = new Map(this, backgroundBitmap);
        this.map = map;

        this.characterList.add(character1);
        this.characterList.add(character2);

        // Create a thread that will handle the running of the game (character movements and such) that can be easily paused without having to add excess logic to the main thread
        this.gameThread = new GameThread(this,holder);
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
        boolean retry= true;
        while(retry) {
            try {
                this.gameThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
            }catch(InterruptedException e)  {
                e.printStackTrace();
            }
            retry= true;
        }
    }

    @Override
    // This method will be called whenever the surface receives a touch event
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            // An iterator loops over a list, and will continue to do so until there are no items left in the list
            // Rather than use a while or for loop, which can be problematic if you have to remove items from them while they are executing,
            // an iterator is designed to allow the programmer to remove items from the list mid-iteration without affecting the program negatively.
            Iterator<Character> iterator = this.characterList.iterator();

            for (Character character: characterList) {
                int movingVectorX = x - character.getX();
                int movingVectorY = y - character.getY();

                character.setMovingVector(movingVectorX, movingVectorY);
            }

            while (iterator.hasNext()) {
                Character character = iterator.next();
                // Is the click within the bounds of the character?
                if (character.isWithinBounds(x,y)) {
                    //Stop moving the character
                    character.setMovingVector(0,0);
                }
            }

            return true;
        }
        return false;
    }

}