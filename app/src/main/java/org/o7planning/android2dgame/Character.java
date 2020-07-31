package org.o7planning.android2dgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import android.util.Pair;
import java.util.Map;


import java.util.Iterator;
import java.util.logging.Logger;

public class Character extends GameObject {

    // The spritesheet is configured such that:
    // Row 0: When character moving down
    // Row 1: When character moving left
    // Row 2: When character moving right
    // Row 3: When character moving up

    private GameSurface gameSurface;

    private final List<Item> itemList = new ArrayList<Item>();

    private int attackAnimationIndex;

    public Map<String, ArrayList<Bitmap>> animationMap;

    // Velocity of game character (pixel/millisecond)
    private float velocity;

    public int hitPoints;
    public int MAXHITPOINTS;
    public int attackDamage;
    public int hitsPerSecond;

    private int movingVectorX = 0;
    private int movingVectorY = 0;

    private Pair<Boolean, Boolean> movePair;

    private long lastDrawNanoTime =-1;

    private boolean isPlayer;

    public CharacterAI ai;

    // This method (called in GameSurface.java) will take the sprite sheet we provide it with and create arrays holding the bitmaps of each sprite

    public Character(GameSurface gameSurface, Bitmap image, int x, int y, boolean isPlayer, int spriteSheetRows, int spriteSheetColumns, float velocity, int hitPoints, int attackDamage, int hitsPerSecond, int attackAnimationIndex, Context context, String mobType) {
        super(image, spriteSheetRows, spriteSheetColumns, x, y); // Calls

        this.isPlayer = isPlayer;
        this.gameSurface= gameSurface;
        this.velocity = velocity;
        this.hitPoints = hitPoints;
        this.MAXHITPOINTS = hitPoints;
        this.attackDamage = attackDamage;
        this.hitsPerSecond = hitsPerSecond;
        this.attackAnimationIndex = attackAnimationIndex;
        this.animationMap = new HashMap<String, ArrayList<Bitmap>>();

        buildAnimationMap(context, mobType);
    }

    public void setCharacterAI(CharacterAI ai) {   this.ai = ai; }

    //TODO: IMPLEMENT A ROLL MECHANIC (INCREASED SPEED, ANIMATION)
    //TODO: IMPLEMENT CHARACTERS STATS (FOR COMBAT AND LEVELING(?))
    //TODO: BROAD PHASE HIT DETECTION (SO WE'RE NOT CONSTANTLY CHECKING EVERY MONSTER)
    //TODO: ATTACK ANIMATIONS
    //TODO: ON HIT EFFECTS (KNOCKBACK, BLOOD(?))

    private ArrayList<Bitmap> populateBitmapArray(int startIndex, int endIndex) {
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        for (int col = startIndex; col < endIndex; col++) {
            bitmaps.add(this.createSubImageAt(0, col));
        }
        return bitmaps;
    }

    private void buildAnimationMap(Context context, String mobType) {
        try {
            boolean done = false;
            switch(mobType) {
                case "player":
                    if (this.gameSurface.stuffFactory.playerAnimationMap != null) {
                        this.animationMap = this.gameSurface.stuffFactory.playerAnimationMap;
                        done = true;
                    }
                    break;
                case "boss":
                    if (this.gameSurface.stuffFactory.bossAnimationMap != null) {
                        this.animationMap = this.gameSurface.stuffFactory.bossAnimationMap;
                        done = true;
                    }
                    break;
                case "orc":
                    if (this.gameSurface.stuffFactory.orcAnimationMap != null) {
                        this.animationMap = this.gameSurface.stuffFactory.orcAnimationMap;
                        done = true;
                    }
                    break;
                case "slime":
                    if (this.gameSurface.stuffFactory.slimeAnimationMap != null) {
                        this.animationMap = this.gameSurface.stuffFactory.slimeAnimationMap;
                        done = true;
                    }
                    break;
            }

            if (!done) {
                InputStream inputStream = context.getResources().openRawResource(R.raw.animations);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String firstLine = reader.readLine();
                String[] animationKeys = firstLine.split(",");

                String line;
                int lastFilledIndex = 0;
                while ((line = reader.readLine()) != null) {
                    // TODO: This is where we will parse the CSV, starting with the first line where we decide how to insert into the map

                    String[] lineArray = line.split(",",-1);
                    String characterName = lineArray[0];

                    if (characterName.equals(mobType)) {

                        for (int i = 1; i < lineArray.length; i++) {
                            if (!lineArray[i].equals("") && !lineArray[i].equals(null)) {
                                ArrayList<Bitmap> bitmaps = populateBitmapArray(lastFilledIndex, Integer.parseInt(lineArray[i]));

                                this.animationMap.put(animationKeys[i], bitmaps);
                                lastFilledIndex = Integer.parseInt(lineArray[i]);
                            }
                        }

                        switch(mobType) {
                            case "player":
                                this.gameSurface.stuffFactory.playerAnimationMap = this.animationMap;
                                break;
                            case "boss":
                                this.gameSurface.stuffFactory.bossAnimationMap = this.animationMap;
                                break;
                            case "orc":
                                this.gameSurface.stuffFactory.orcAnimationMap = this.animationMap;
                                break;
                            case "slime":
                                this.gameSurface.stuffFactory.slimeAnimationMap = this.animationMap;
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Bad
            System.out.println("Error reading from animations CSV in Character class:");
            System.out.println(e.getMessage());
        }
    }


    // This is the character update loop

    public void update(org.o7planning.android2dgame.Map map)  {

        checkIfDead();

        //TODO: MOVE TO CHARACTER AI
//        if(!ai.getType()){
//            findItem();
//        }

        //update character moving vector and animate
        move(map);

        //update AI

        ai.onUpdate(this.movingVectorX, this.movingVectorY);
//        if(!this.isPlayer){
//            attack();
//        }

        //update Weapon
//        if(itemList.size() != 0){
//            itemList.get(0).update();
//        }

    }

    public void move(org.o7planning.android2dgame.Map map) {
        // c = sqrt(a^2 + b^2) - i.e. we are getting the movement vector based on how far we moved horizontally and vertically
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

        // TODO: This probably needs to change
        if(ai.hasWeapon()) {
            int vectorX = movingVectorX;
            int vectorY = movingVectorY;

            if (ai instanceof PlayerAI) {
                PlayerAI playerAI = (PlayerAI) ai;
                if (playerAI.attackVectorX != 0 || playerAI.attackVectorY != 0) {
                    vectorX = playerAI.attackVectorX;
                    vectorY = playerAI.attackVectorY;
                }
            }

            int vectorXAbsolute = Math.abs(vectorX);
            int vectorYAbsolute = Math.abs(vectorY);

            if ( vectorX != 0 || vectorY != 0 ) {
                if (vectorX > 0) {
                    if (vectorY > 0 && vectorXAbsolute < vectorYAbsolute) {
                        // Moving Down
                        hurtBox.x = hitBox.getX();
                        hurtBox.y = hitBox.getY() + hitBox.getHeight();
                    } else if (vectorY < 0 && vectorXAbsolute < vectorYAbsolute) {
                        // Moving Up
                        hurtBox.x = hitBox.getX();
                        hurtBox.y = hitBox.getY() - hitBox.getHeight();
                    } else {
                        // Moving Right
                        hurtBox.x = hitBox.getX() + hitBox.getWidth(); //+ this.getWidth()/2 - 30 ;
                        hurtBox.y = this.getY();
                    }
                } else {
                    if (vectorY > 0 && vectorXAbsolute < vectorYAbsolute) {
                        // Moving Down
                        hurtBox.x = hitBox.getX();
                        hurtBox.y = hitBox.getY() + hitBox.getHeight();
                    } else if (vectorY < 0 && vectorXAbsolute < vectorYAbsolute) {
                        // Moving Up
                        hurtBox.x = hitBox.getX();
                        hurtBox.y = hitBox.getY() - hitBox.getHeight();
                    } else {
                        // Moving Left
                        hurtBox.x = this.hitBox.getX() - hurtBox.getWidth();
                        hurtBox.y = this.getY();
                    }
                }
            }
        }



        if(!ai.hasWeapon()){
            hurtBox.x = hitBox.getX();
            hurtBox.y = hitBox.getY();
        }
        if(itemList.size() != 0){
            itemList.get(0).x = this.x+50;
            itemList.get(0).y = this.y+20;
            itemList.get(0).hitBox.x = itemList.get(0).x;
            itemList.get(0).hitBox.y = itemList.get(0).y;
        }
    }

    // This function will get the bitmap of the exact sprite we are on and draw it on the canvas

    public void draw(Canvas canvas)  {
        Bitmap bitmap = this.ai.getCurrentBitmap();
        canvas.drawBitmap(bitmap, x, y, null);
        // Last draw time.
        this.lastDrawNanoTime= System.nanoTime();
        //hitBox.draw(canvas);
        //hurtBox.draw(canvas);

        // healthBar Logic = Always draw for player, draw if taken damage, and don't draw if dead //TODO the last part shouldn't be necessary but for the demo it looks nicer
        if((this.isPlayer || this.hitPoints < this.MAXHITPOINTS) && this.hitPoints != 0){
            healthBar.draw(canvas);
        }
    }

    public void reduceHitPointsBy(int damageDealt)  {
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