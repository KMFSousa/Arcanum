package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Iterator;
import java.util.List;

public class Projectile extends GameObject {

    private GameSurface gameSurface;
    private int movingVectorX = 0;
    private int movingVectorY = 0;
    private float velocity = 0;
    private int attackDamage = 0;
    private boolean isPlayerOwned = true;
    private long lastDrawNanoTime =-1;
    private int xToMoveTo;
    private int yToMoveTo;

    public Projectile(boolean isPlayerOwned, Bitmap image, int rowCount, int colCount, int x, int y, int movingVectorX, int movingVectorY, GameSurface gameSurface, float velocity, int attackDamage) {
        super(image, rowCount, colCount, x, y);

        this.gameSurface = gameSurface;
        this.movingVectorX = movingVectorX;
        this.movingVectorY = movingVectorY;
        this.velocity = velocity;
        this.attackDamage = attackDamage;
        this.isPlayerOwned = isPlayerOwned;
        this.yToMoveTo = yToMoveTo;
        this.xToMoveTo = xToMoveTo;
    }

    //TODO: If attack vector is 0 do not spawn a projectile
    //TODO: Fine tune the projectile speed


    public void update(){
        //TODO: call move()
        move();
        //TODO: check for collision (either with PC or NPC), pass in respective list from gamesurface
        checkCharacterCollision(gameSurface.dungeon.getCurrentRoom().monsterList);
        //TODO:check for collision with environment
        checkEnvironmentCollision();
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
        xToMoveTo = this.x + (int)(distance* movingVectorX / movingVectorLength);
        yToMoveTo = this.y + (int)(distance* movingVectorY / movingVectorLength);
        this.x = xToMoveTo;
        this.y = yToMoveTo;

        hurtBox.x = this.getX();
        hurtBox.y = this.getY();

        lastDrawNanoTime = now;

    }

    public void checkCharacterCollision(List<Character> targetList) {
        //TODO:Check the hurtbox of the projectile against the hitbox(es) of its target(s)
        if (!targetList.isEmpty()) {
            Iterator<Character> iterator = targetList.iterator();

            while (iterator.hasNext()) {
                Character other = iterator.next();
                if (this.hurtBox.x < other.hitBox.x + other.width &&
                        this.hurtBox.x + this.hurtBox.width > other.x &&
                        this.hurtBox.y < other.hitBox.y + other.height &&
                        this.hurtBox.y + this.hurtBox.height > other.hitBox.y) {
                    other.reduceHitPointsBy(this.attackDamage);
                }
            }
        }
    }

    public void checkEnvironmentCollision(){
        //TODO:Check if the projectile is out of bounds
        if(!gameSurface.dungeon.getCurrentRoom().canMoveProjectile(xToMoveTo, yToMoveTo, this.getWidth(), this.getHeight())) {
            gameSurface.projectileRemovalList.add(this);
        }
    }

    public void onCollision(){
        //TODO: Handles the destruction of the projectile when it collides with a map feature
        //TODO: Destroy the projectile when needed (either on contact with player or collidable object)
    }


}
