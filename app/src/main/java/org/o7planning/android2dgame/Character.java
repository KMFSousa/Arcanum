package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;
import android.util.Pair;


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

    private int attackAnimationIndex;

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;

    // Velocity of game character (pixel/millisecond)
    private float velocity;
    public   boolean isAttacking;
    boolean attackAnimationInProgress = false;

    public double hitPoints;
    public double MAXHITPOINTS;
    public int attackDamage;
    public double attackSpeed;

    private int movingVectorX = 0;
    private int movingVectorY = 0;

    private Pair<Boolean, Boolean> movePair;

    private long lastDrawNanoTime =-1;

    private boolean isPlayer;

    public CharacterAI ai;
    public void setCharacterAI(CharacterAI ai) {   this.ai = ai; }

    //TODO: IMPLEMENT A ROLL MECHANIC (INCREASED SPEED, ANIMATION)
    //TODO: IMPLEMENT CHARACTERS STATS (FOR COMBAT AND LEVELING(?))
    //TODO: BROAD PHASE HIT DETECTION (SO WE'RE NOT CONSTANTLY CHECKING EVERY MONSTER)
    //TODO: ATTACK ANIMATIONS
    //TODO: ON HIT EFFECTS (KNOCKBACK, BLOOD(?))



    // This method (called in GameSurface.java) will take the sprite sheet we provide it with and create arrays holding the bitmaps of each sprite

    public Character(GameSurface gameSurface, Bitmap image, int x, int y, boolean isPlayer, int spriteSheetRows, int spriteSheetColumns, float velocity, double hitPoints, int attackDamage, double attackSpeed, int attackAnimationIndex) {
        super(image, spriteSheetRows, spriteSheetColumns, x, y); // Calls

        this.isPlayer = isPlayer;
        this.gameSurface= gameSurface;
        this.velocity = velocity;
        this.hitPoints = hitPoints;
        this.MAXHITPOINTS = hitPoints;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.attackAnimationIndex = attackAnimationIndex;

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

    public void update(Map map)  {

        checkIfDead();

        //TODO: MOVE TO CHARACTER AI
//        if(!ai.getType()){
//            findItem();
//        }

        //update character moving vector and animate
        move(map);

        //update AI

        ai.onUpdate();
//        if(!this.isPlayer){
//            attack();
//        }

        //update Weapon
//        if(itemList.size() != 0){
//            itemList.get(0).update();
//        }

    }

    public void move(Map map) {
        // c = sqrt(a^2 + b^2) - i.e. we are getting the movement vector based on how far we moved horizontally and vertically
        double movingVectorLength = Math.sqrt(movingVectorX*movingVectorX + movingVectorY*movingVectorY);

        // Update which column we are using by 1 for each iteration
        // Used in getCurrentMoveBitmap() to grab the next sprite to render on the canvas
        if (movingVectorLength > 0 || isAttacking) {
            this.colUsing++;
        }
        // Once we have cycled through all the sprites for a certain direction, start back at the first one
        //TODO: CYCLE THROUGH STATES, NOT ATTACK STATE
        if(colUsing >= this.attackAnimationIndex -1 && !isAttacking )  {
            this.colUsing =0;
        }

        if(isAttacking) {
            if(!attackAnimationInProgress) {
                this.colUsing = this.attackAnimationIndex;
                attackAnimationInProgress = true;
                
            }
            if(this.colUsing == this.colCount - 1) {
                isAttacking = false;
                attackAnimationInProgress = false;
            }
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
        int xToMoveTo = this.x + (int)(distance* movingVectorX / movingVectorLength);
        int yToMoveTo = this.y + (int)(distance* movingVectorY / movingVectorLength);

        movePair = map.canMove(this.isPlayer, this.x, this.y, xToMoveTo, yToMoveTo, this.height, this.width);
        boolean canMoveX = movePair.first;
        boolean canMoveY = movePair.second;
        if (canMoveX && canMoveY) {
            this.x = xToMoveTo;
            this.y = yToMoveTo;
        } else if (canMoveX) {
            this.x = xToMoveTo;
        } else if (canMoveY) {
            this.y = yToMoveTo;
        }

        // When the game's character touches the edge of the screen, then change direction
        if (this.isPlayer) {
            if(this.x < 0 )  {
                this.x = this.gameSurface.getWidth()-width;
                this.gameSurface.dungeon.transitionHorizontal(-1);
            } else if(this.x > this.gameSurface.getWidth() -width)  {
                this.x = 0;
                this.gameSurface.dungeon.transitionHorizontal(1);
            }

            if(this.y < 0 )  {
                this.y = this.gameSurface.getHeight()-height;
                this.gameSurface.dungeon.transitionVertical(-1);
            } else if(this.y > this.gameSurface.getHeight() - height)  {
                this.y = 0;
                this.gameSurface.dungeon.transitionVertical(1);
            }
        }

        //Move the hitbox and hurtbox with the character
        hitBox.x = this.getX() + this.getWidth()/2 - 30;
        hitBox.y = this.getY();

        if(!this.isPlayer && this.hitPoints < this.MAXHITPOINTS){ // If it's a player it will never move
            healthBar.x = this.getX() + this.getWidth()/2 - 100;
            healthBar.y = this.getY() - this.getHeight() + 10;
        }

        if(ai.hasWeapon()) {
            switch (this.rowUsing) {
                case ROW_LEFT_TO_RIGHT:
                    hurtBox.x = hitBox.getX() + hitBox.getWidth(); //+ this.getWidth()/2 - 30 ;
                    hurtBox.y = this.getY();
                    break;
                case ROW_BOTTOM_TO_TOP:
                    hurtBox.x = hitBox.getX();
                    hurtBox.y = hitBox.getY() - hitBox.getHeight();
                    break;
                case ROW_RIGHT_TO_LEFT:
                    hurtBox.x = this.getX();
                    hurtBox.y = this.getY();
                    break;
                case ROW_TOP_TO_BOTTOM:
                    hurtBox.x = hitBox.getX();
                    hurtBox.y = hitBox.getY() + hitBox.getHeight();
                    break;
            }
        }

        if(!ai.hasWeapon()){
            hurtBox.x = hitBox.getX();
            hurtBox.y = hitBox.getY();
        }
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

        int vectorX = movingVectorX;
        int vectorY = movingVectorY;

        if (this.ai instanceof PlayerAI) {
            PlayerAI playerAI = (PlayerAI) this.ai;
            if (this.isAttacking) {
                Pair<Integer, Integer> attackVectors = playerAI.getAttackVector();
                vectorX = attackVectors.first;
                vectorY = attackVectors.second;
            }
        }

        int vectorXAbsolute = Math.abs(vectorX);
        int vectorYAbsolute = Math.abs(vectorY);

        if ( vectorX != 0 || vectorY != 0 ) {
            if (vectorX > 0) {
                if (vectorY > 0 && vectorXAbsolute < vectorYAbsolute) {
                    this.rowUsing = ROW_TOP_TO_BOTTOM;
                } else if (vectorY < 0 && vectorXAbsolute < vectorYAbsolute) {
                    this.rowUsing = ROW_BOTTOM_TO_TOP;
                } else {
                    this.rowUsing = ROW_LEFT_TO_RIGHT;
                }
            } else {
                if (vectorY > 0 && vectorXAbsolute < vectorYAbsolute) {
                    this.rowUsing = ROW_TOP_TO_BOTTOM;
                } else if (vectorY < 0 && vectorXAbsolute < vectorYAbsolute) {
                    this.rowUsing = ROW_BOTTOM_TO_TOP;
                } else {
                    this.rowUsing = ROW_RIGHT_TO_LEFT;
                }
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
        hurtBox.draw(canvas);

        // healthBar Logic = Always draw for player, draw if taken damage, and don't draw if dead //TODO the last part shouldn't be necessary but for the demo it looks nicer
        if((this.isPlayer || this.hitPoints < this.MAXHITPOINTS) && this.hitPoints != 0){
            healthBar.draw(canvas);
        }
    }

    public void reduceHitPointsBy(double damageDealt)  {
        this.hitPoints -= damageDealt;
        if(this.hitPoints < 0){
            this.hitPoints = 0;
        }

        this.healthBar.width = (int) (((float) this.hitPoints/ (float) this.MAXHITPOINTS)*this.healthBar.originalSpriteWidth); //Hardcoded initial width of healthBar
        if(this.healthBar.width <= 0){ // We cannot draw bitmaps of width = 0, so we draw a thin sliver of width 1.
            this.healthBar.width = 1;
        }

        this.healthBar.image = Bitmap.createScaledBitmap(this.healthBar.image,this.healthBar.width, this.healthBar.height,false);
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

//    public void attack() {
//
//      //  if(itemList.size() != 0){itemList.get(0).inCombat();}
//        if(ai.isPlayer()){
//            this.isAttacking = true;
//            this.attackAnimationInProgress = false;
//            Iterator<Character> iterator = gameSurface.dungeon.getCurrentRoom().monsterList.iterator();
//
//            while(iterator.hasNext()){
//                Character other = iterator.next();
//                if(this.hurtBox.x < other.hitBox.x + other.width &&
//                        this.hurtBox.x + this.hurtBox.width > other.x &&
//                        this.hurtBox.y < other.hitBox.y + other.height &&
//                        this.hurtBox.y + this.hurtBox.height > other.hitBox.y){
//                    other.reduceHitPointsBy(attackDamage);
//                    Log.d("Slime HP remaining", ": " + other.hitPoints);
//                }
//            }
//        }
//
//        if(!ai.isPlayer() && !gameSurface.characterList.isEmpty()){
//            Character other = gameSurface.characterList.get(0);
//            if(this.hurtBox.x < other.hitBox.x + other.hitBox.width &&
//                    this.hurtBox.x + this.hurtBox.width > other.hitBox.x &&
//                    this.hurtBox.y < other.hitBox.y + other.hitBox.height &&
//                    this.hurtBox.y + this.hurtBox.height > other.hitBox.y){
//                other.reduceHitPointsBy(attackDamage);
//                Log.d("Player HP remaining", ": " + other.hitPoints);
//
//                this.isAttacking = true;
//            }
//        }
//    }

    public void checkIfDead() {
        if (this.hitPoints <= 0) {
            if(!ai.isPlayer()){gameSurface.removalList.add(this);}
            else if(ai.isPlayer()){((PlayerAI) ai).isDead = true;}
        }
    }


}