package org.o7planning.android2dgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SlimeAI implements CharacterAI {

    private GameSurface gameSurface;
    private List<Character> playerList;
    private final Character player;
    public Character character;
    private StuffFactory factory;
    private int positionX;
    private int postitionY;
    private int distanceToPlayerX;
    private int distanceToPlayerY;
    private int updateCounter;
    private int spreadCount;
    private ArrayList<Bitmap> currentAnimationBitmap = new ArrayList<Bitmap>();
    private int colUsing = 0;
    private Context context;
    private boolean isAttacking = false;

    public SlimeAI(Character character, GameSurface gameSurface, StuffFactory factory, Context context) {
        this.gameSurface = gameSurface;
        this.factory = factory;
        this.player = gameSurface.characterList.get(0);
        this.character = character;
        this.context = context;
    }

    public void onUpdate (int movingVectorX, int movingVectorY) {
        if(closeToPlayer(100)) {
            attack();
        }

        if (updateCounter % 5 == 0) {
            updateCounter = 0;
            wander();
        }

        if(spreadCount < 1 && Math.random() < 0.1 && gameSurface.dungeon.getCurrentRoom().monsterList.size() < 10)
           spread();

        updateCounter++;

        this.animate(movingVectorX, movingVectorY);
    }

    public void attack() {
        if(!gameSurface.characterList.isEmpty()){
            Character other = gameSurface.characterList.get(0);
            if(character.hurtBox.x < other.hitBox.x + other.hitBox.width &&
                    character.hurtBox.x + character.hurtBox.width > other.hitBox.x &&
                    character.hurtBox.y < other.hitBox.y + other.hitBox.height &&
                    character.hurtBox.y + character.hurtBox.height > other.hitBox.y){
                other.reduceHitPointsBy(character.attackDamage);
                Log.d("Player HP remaining", ": " + other.hitPoints);
            }
        }
    }

    public Bitmap getCurrentBitmap() {
        return this.currentAnimationBitmap.get(this.colUsing);
    }

    public void animate(int movingVectorX, int movingVectorY) {
        int vectorX = movingVectorX;
        int vectorY = movingVectorY;
        int vectorXAbsolute = Math.abs(vectorX);
        int vectorYAbsolute = Math.abs(vectorY);

        if ( vectorX != 0 || vectorY != 0 ) {
            if (vectorX > 0) {
                if (vectorY > 0 && vectorXAbsolute < vectorYAbsolute) {
                    // Moving down and to the right
                    this.currentAnimationBitmap = this.character.animationMap.get("walkright");
                } else if (vectorY < 0 && vectorXAbsolute < vectorYAbsolute) {
                    // Moving up and to the right
                    this.currentAnimationBitmap = this.character.animationMap.get("walkright");
                } else {
                    // Moving right
                    this.currentAnimationBitmap = this.character.animationMap.get("walkright");
                }
            } else {
                if (vectorY > 0 && vectorXAbsolute < vectorYAbsolute) {
                    // Moving down and to the left
                    this.currentAnimationBitmap = this.character.animationMap.get("walkleft");
                } else if (vectorY < 0 && vectorXAbsolute < vectorYAbsolute) {
                    // Moving up and to the left
                    this.currentAnimationBitmap = this.character.animationMap.get("walkleft");
                } else {
                    // Moving left
                    this.currentAnimationBitmap = this.character.animationMap.get("walkleft");
                }
            }
        } else {
            // Idle animation
        }

        double movingVectorLength = Math.sqrt(movingVectorX*movingVectorX + movingVectorY*movingVectorY);

        if (movingVectorLength > 0) {
            this.colUsing++;

            if (this.colUsing >= this.currentAnimationBitmap.size()) {
                this.colUsing = 0;
            }
        }
    }

    private void spread() {
        int x = character.x;
        int y = character.y;

        Character child = factory.newSlime(this.gameSurface.dungeon.getCurrentRoom().monsterList, x, y, this.context, true);
        child.x = x;
        child.y = y;
        child.hitBox.x = x;
        child.hitBox.y = y;

        spreadCount++;
    }

    public void wander() {
        int x = (int)(Math.random() * 200 - 100);
        int y = (int)(Math.random() * 200 - 100);
        character.setMovingVector(x, y);
    }

    public boolean closeToPlayer(int requiredDistance) {
        int distanceToPlayer = GameObject.getDistanceBetweenObjects(this.character, player);


        if (distanceToPlayer <= requiredDistance) { // TODO: Change back to 560
            return true;
        }

        return false;
    }

    public boolean hasWeapon(){
        return false;
    }
    public boolean isPlayer(){
        return false;
    }
}

