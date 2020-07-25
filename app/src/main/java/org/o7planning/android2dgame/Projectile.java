package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

public class Projectile extends GameObject {

    private GameSurface gameSurface;
    private int movingVectorX = 0;
    private int movingVectorY = 0;
    private float velocity = 0;
    private int attackDamage = 0;
    private boolean isPlayerOwned = true;
    private long lastDrawNanoTime =-1;

    public Projectile(boolean isPlayerOwned, Bitmap image, int rowCount, int colCount, int x, int y, int movingVectorX, int movingVectorY, GameSurface gameSurface, float velocity, int attackDamage) {
        super(image, rowCount, colCount, x, y);

        this.gameSurface = gameSurface;
        this.movingVectorX = movingVectorX;
        this.movingVectorY = movingVectorY;
        this.velocity = velocity;
        this.attackDamage = attackDamage;
        this.isPlayerOwned = isPlayerOwned;
        this.hurtBox = hurtBox;

    }

    public void update(){
        //TODO: call move()
        move();
        //TODO: check for collision (either with PC or NPC), pass in respective list from gamesurface
        //TODO:check for collision with environment
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(image, this.x, this.y, null);
    }

    public void move(){
        //TODO: updates projectile X and Y coordinates
        double movingVectorLength = Math.sqrt(movingVectorX*movingVectorX + movingVectorY*movingVectorY);

        // Current time in nanoseconds
        long now = System.nanoTime();

        // If we have not drawn the sprite yet

        if(lastDrawNanoTime==-1) {
            lastDrawNanoTime= now;
        }
        // Change nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        int deltaTime = (int) ((now - lastDrawNanoTime)/ 1000000 );

        // Distance moved per time unit
        float distance = velocity * deltaTime;

        // Calculate the new position of the game character.
        int xToMoveTo = this.x + (int)(distance* movingVectorX / movingVectorLength);
        int yToMoveTo = this.y + (int)(distance* movingVectorY / movingVectorLength);
        this.x = xToMoveTo;
        this.y = yToMoveTo;
    }

    public void checkCharacterCollision(List<Character> targetList){
        //TODO:Check the hurtbox of the projectile against the hitbox(es) of its target(s)
    }

    public void checkEnvironmentCollision(){
        //TODO:Check if the projectile is out of bounds
    }

    public void onCollision(){
        //TODO: Handles the destruction of the projectile when it collides with a map feature
        //TODO: Destroy the projectile when needed (either on contact with player or collidable object)
    }


}
