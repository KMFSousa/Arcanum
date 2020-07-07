package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.logging.Logger;

public class Character extends GameObject {

    // The spritesheet is configured such that:
    // Row 0: When character moving down
    // Row 1: When character moving left
    // Row 2: When character moving right
    // Row 3: When character moving up

    private GameSurface gameSurface;

    private static final int ROW_TOP_TO_BOTTOM = 0;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private static final int ROW_LEFT_TO_RIGHT = 2;
    private static final int ROW_BOTTOM_TO_TOP = 3;

    private final List<Item> itemList = new ArrayList<Item>();

    // Row index of Image are being used.
    private int rowUsing = ROW_LEFT_TO_RIGHT;

    // `colUsing` will tell us which stage of the animation we are on
    private int colUsing;

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;

    // Velocity of game character (pixel/millisecond)
    private float velocity;
    public int hitPoints;


    private int movingVectorX = 0;
    private int movingVectorY = 0;

    private long lastDrawNanoTime =-1;


    private CharacterAI ai;
    public void setCharacterAI(CharacterAI ai) {   this.ai = ai; }

    //TODO: IMPLEMENT A ROLL MECHANIC (INCREASED SPEED, ANIMATION)
    //TODO: IMPLEMENT CHARACTERS STATS (FOR COMBAT AND LEVELING(?))
    //TODO: BROAD PHASE HIT DETECTION (SO WE'RE NOT CONSTANTLY CHECKING EVERY MONSTER)
    //TODO: ATTACK ANIMATIONS
    //TODO: ON HIT EFFECTS (KNOCKBACK, BLOOD(?))



    // This method (called in GameSurface.java) will take the spritesheet we provide it with and create arrays holding the bitmaps of each sprite

    public Character(GameSurface gameSurface, Bitmap image, int x, int y, int spriteSheetRows, int spriteSheetColumns, float velocity, int hitPoints) {
        super(image, spriteSheetRows, spriteSheetColumns, x, y); // Calls


        this.gameSurface= gameSurface;
        this.velocity = velocity;
        this.hitPoints = hitPoints;


        this.topToBottoms = new Bitmap[colCount]; // 3
        this.rightToLefts = new Bitmap[colCount]; // 3
        this.leftToRights = new Bitmap[colCount]; // 3
        this.bottomToTops = new Bitmap[colCount]; // 3

        // For each column in the spritesheet, generate arrays that hold the bitmaps created from dividing each row in createSubImageAt

        for(int col = 0; col< this.colCount; col++ ) {
            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            this.rightToLefts[col]  = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            this.bottomToTops[col]  = this.createSubImageAt(ROW_BOTTOM_TO_TOP, col);
        }
    }

    // Depending on what direction the character is moving we want to be able to grab the corresponding set of sprites

    public Bitmap[] getMoveBitmaps()  {
        switch (rowUsing)  {
            case ROW_BOTTOM_TO_TOP:
                return  this.bottomToTops;
            case ROW_LEFT_TO_RIGHT:
                return this.leftToRights;
            case ROW_RIGHT_TO_LEFT:
                return this.rightToLefts;
            case ROW_TOP_TO_BOTTOM:
                return this.topToBottoms;
            default:
                return null;
        }
    }

    // We will be cycling through each set of sprites depending on which direction we are moving
    // This is determined by the switch for `rowUsing` in getMoveBitmaps, called here
    // This function will return which stage of the animation (which specific sprite) is in use, denoted by `colUsing`

    public Bitmap getCurrentMoveBitmap()  {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }

    // This is the character update loop

    public void update()  {

        //check if in combat
       // if(itemList.size() != 0){itemList.get(0).inCombat();}

        checkIfDead();

        //TODO: MOVE TO CHARACTER AI
        if(!ai.getType()){
            findItem();
        }


        //update character moving vector and animate
        move();
        //TODO: IS THIS THE BEST PLACE FOR THIS?
        hitBox.x = this.getX() + this.getWidth()/2-16;
        hitBox.y = this.getY(); //+ this.getHeight()/2-16;

        //update AI
         ai.onUpdate();

        //update Weapon
        if(itemList.size() != 0){
            itemList.get(0).update();
        }

    }

    public void move() {
        // c = sqrt(a^2 + b^2) - i.e. we are getting the movement vector based on how far we moved horizontally and vertically
        double movingVectorLength = Math.sqrt(movingVectorX*movingVectorX + movingVectorY*movingVectorY);

        // Update which column we are using by 1 for each iteration
        // Used in getCurrentMoveBitmap() to grab the next sprite to render on the canvas
        if (movingVectorLength > 0) {
            this.colUsing++;
        }
        // Once we have cycled through all the sprites for a certain direction, start back at the first one
        //TODO: CYCLE THROUGH STATES, NOT ATTACK STATE
        if(colUsing >= this.colCount)  {
            this.colUsing =0;
        }
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
        this.x = x +  (int)(distance* movingVectorX / movingVectorLength);
        this.y = y +  (int)(distance* movingVectorY / movingVectorLength);

        // When the game's character touches the edge of the screen, then change direction
        if(this.x < 0 )  {
            this.x = 0;
            this.movingVectorX = - this.movingVectorX;
        } else if(this.x > this.gameSurface.getWidth() -width)  {
            this.x= this.gameSurface.getWidth()-width;
            this.movingVectorX = - this.movingVectorX;
        }

        if(this.y < 0 )  {
            this.y = 0;
            this.movingVectorY = - this.movingVectorY;
        } else if(this.y > this.gameSurface.getHeight()- height)  {
            this.y= this.gameSurface.getHeight()- height;
            this.movingVectorY = - this.movingVectorY ;
        }

        //TODO: CLEAN UP THIS CODE. PROBABLY MOVE IT INTO THE SWORD/ITEM CLASS
        animate();
        if(itemList.size() != 0){
            itemList.get(0).x = this.x+50;
            itemList.get(0).y = this.y+20;
            itemList.get(0).hitBox.x = itemList.get(0).x;
            itemList.get(0).hitBox.y = itemList.get(0).y;
        }


    }

    public void animate() {

        // movingVectorX and movingVectorY are +/- values that will determine what direction we are moving in
        // based on the values of these vectors, we can decide which row of sprites we are using
        if( movingVectorX > 0 )  {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_LEFT_TO_RIGHT;
            }
        } else {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_RIGHT_TO_LEFT;
            }
        }
    }

    // This function will get the bitmap of the exact sprite we are on and draw it on the canvas

    public void draw(Canvas canvas)  {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap, x, y, null);
        // Last draw time.
        this.lastDrawNanoTime= System.nanoTime();
        hitBox.draw(canvas);
    }

    public void setMovingVector(int movingVectorX, int movingVectorY)  {
        this.movingVectorX = movingVectorX;
        this.movingVectorY = movingVectorY;
    }

    //TODO: COLLISION DETECTION CAN BE MOVED WITHIN THE HITBOX CLASS AS A SEPARATE METHOD
    public void findItem() {
        Iterator<Item> iterator = gameSurface.itemList.iterator();

        while(iterator.hasNext()) {
            Item other = iterator.next();
            if(this.getX() < other.x + other.getWidth() && other.x < this.getX() + this.getWidth()
                    && this.getY() < other.y && other.y < this.getY() + this.getHeight()) {
                pickupItem(other);
            }
        }
    }

    public void pickupItem(Item other) {
        itemList.add(other);
        //gameSurface.itemList.remove(other);
    }

    public void attack() {

      //  if(itemList.size() != 0){itemList.get(0).inCombat();}
        if(itemList.size() != 0){itemList.get(0).combatAnimationFinished = false;}
//        Iterator<Character> iterator = gameSurface.monsterList.iterator();

        if(ai.getType() && !gameSurface.characterList.isEmpty()){
            //Iterator<Character> iterator = gameSurface.characterList.iterator();
            Character other = gameSurface.characterList.get(0);
            if(this.hitBox.x < other.hitBox.x + other.hitBox.width &&
                    this.hitBox.x + this.hitBox.width > other.hitBox.x &&
                    this.hitBox.y < other.hitBox.y + other.hitBox.height &&
                    this.hitBox.y + this.hitBox.height > other.hitBox.y){
                other.hitPoints -= 1;
                Log.d("playerHitpoints", "" + other.hitPoints );
            }
        }
//
//        while(iterator.hasNext()){
//            Character other = iterator.next();
//            if(this.getX() < other.x + other.getWidth() && other.x < this.getX() + this.getWidth()
//                    && this.getY() < other.y && other.y < this.getY() + this.getHeight()){
//                attack(other);
//            }
//
//        }
    }

    public void checkIfDead() {
        if (this.hitPoints <= 0) {
            if(ai.getType()){gameSurface.removalList.add(this);}
            else if(!ai.getType()){((PlayerAI) ai).isDead = true;}
        }
    }
//
//    public void attack(Character other) {
//        if(!ai.getType())
//            gameSurface.removeCharacter(other);
//    }

}