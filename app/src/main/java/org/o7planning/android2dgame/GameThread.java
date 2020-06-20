package org.o7planning.android2dgame;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

// A thread is a thread of execution in a program.
// The JVM allows an application to have multiple threads of execution running concurrently.
// Every thread has a priority, and threads with higher priority are executed in preference to those with lower priority.
// There are two ways to create a new thread of execution.
// 1) Declare a subclass of the `Thread` class, which should override the `run()` method of class Thread.
//    - An instance of the subclass can then be allocated and started.
// 2) Declare a class that implements the `Runnable` interface, which then implements the `run()` method.
//    - An instance of the class can then be allocated, passed as an argument when creating Thread, and started.

// When a Java app is started, its main() method is executed by the main thread.
// From inside your app, you can create and start more threads which can execute parts of your app in parallel with the main thread.
// Why would this be wanted here?
// In our case, we have a thread that specifically controls the running of the game - the characters' movements, for example.
// If someone pauses the game, they can just pause that thread, and the whole application doesn't have to stop running and we don't have to implement a ton of if-elses.

// The way to stop a thread from running is NOT via calling some `stop()` function - instead, we have a variable `running` within the class that will be regularly checked within the `run()` method to determine whether it should keep executing.
// In this way, we can easily pause the running of the thread by setting `gameThread.running = False`.

public class GameThread extends Thread {

    private boolean running;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;

    public GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder)  {
        this.gameSurface= gameSurface;
        this.surfaceHolder= surfaceHolder;
    }

    @Override

    // This function will be what runs all the different aspects of the game as they currently stand, such as updating the character sprite and redrawing the canvas.
    public void run()  {
        long startTime = System.nanoTime();

        while(running)  {
            Canvas canvas= null;
            try {
                // Get Canvas from Holder and lock it.
                canvas = this.surfaceHolder.lockCanvas();

                // The synchronized method allows only one thread at a time to access the contained code
                // In our case we've only instantiated one thread, but if we were to instantiate more this would become very important.
                synchronized (canvas)  {
                    // the gameSurface update() method calls the character update() method
                    // IMPORTANT NOTE: The `update()` method is responsible for adjusting the parameters regarding the character objects, IT DOES NOT RENDER ANYTHING TO THE SCREEN
                    this.gameSurface.update();
                    // the gameSurface draw() method calls the character draw() method
                    // IMPORTANT NOTE: The `draw()` method is what will actually draw the sprites to the canvas
                    this.gameSurface.draw(canvas);
                }
            }catch(Exception e)  {
                Log.d("ERROR", "ERROR HAS OCCURED!");
            } finally {
                if(canvas!= null)  {
                    // Unlock Canvas.
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            long now = System.nanoTime() ;
            // Interval to redraw game
            long waitTime = 100; // Millisecond.
            System.out.print("Wait Time = " + waitTime);

            try {
                // Sleep.
                this.sleep(waitTime);
            } catch(InterruptedException e)  {
                System.out.print("Error when trying to sleep in the game thread.");
            }
            startTime = System.nanoTime();
            System.out.print(".");
        }
    }

    public void setRunning(boolean running)  {
        this.running = running;
    }
}