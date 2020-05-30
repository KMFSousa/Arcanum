package org.o7planning.android2dgame;

import android.content.Context;
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
    private final List<ChibiCharacter> chibiList = new ArrayList<ChibiCharacter>();
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
        for (ChibiCharacter chibi: chibiList) {
            chibi.update();
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
        for (ChibiCharacter chibi: chibiList) {
            chibi.draw(canvas);
        }

        for (Explosion explosion: explosionList) {
            explosion.draw(canvas);
        }
    }

    // Implements method of SurfaceHolder.Callback
    @Override

    // The surfaceCreated method is called immediately after the surface is first created (in MainActivity)
    // The gameThread will be what calls the update method on the chibi character
    public void surfaceCreated(SurfaceHolder holder) {
        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.chibi1);
        ChibiCharacter chibi1 = new ChibiCharacter(this, chibiBitmap1,100,50);

        Bitmap chibiBitmap2 = BitmapFactory.decodeResource(this.getResources(),R.drawable.chibi2);
        ChibiCharacter chibi2 = new ChibiCharacter(this, chibiBitmap2,300,150);

        Bitmap mapBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.spritesheet);
        Map map = new Map(this, mapBitmap, 500, 500);

        this.map = map;

        this.chibiList.add(chibi1);
        this.chibiList.add(chibi2);

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
            Iterator<ChibiCharacter> iterator = this.chibiList.iterator();

            while (iterator.hasNext()) {
                ChibiCharacter chibi = iterator.next();
                // Is the click within the bounds of the character?
                if (chibi.getX() < x && x < chibi.getX() + chibi.getWidth() && chibi.getY() < y && y < chibi.getY() + chibi.getHeight()) {
                    iterator.remove();

                    // Create Explosion object
                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion);
                    Explosion explosion = new Explosion(this, bitmap, chibi.getX(), chibi.getY());

                    this.explosionList.add(explosion);
                }
            }

            for (ChibiCharacter chibi: chibiList) {
                int movingVectorX = x - chibi.getX();
                int movingVectorY = y - chibi.getY();

                chibi.setMovingVector(movingVectorX, movingVectorY);
            }

            return true;
        }
        return false;
    }

}