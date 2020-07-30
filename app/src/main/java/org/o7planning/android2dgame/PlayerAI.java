package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayerAI implements CharacterAI {

    public static boolean isDead;
    public Character character;
    public String attackStyle = "Melee";
    public int attackVectorX = 0;
    public int attackVectorY = 0;
    private GameSurface gameSurface;
    private long lastAttackNanoTime = -1;
    private double damageDealt = 0;
    private StuffFactory factory;
    private ArrayList<Bitmap> currentAnimationBitmap = new ArrayList<Bitmap>();
    private int colUsing = 0;
    private boolean isAttacking = false;
    private boolean attackAnimationInProgress = false;


    public PlayerAI(Character character, GameSurface gameSurface, StuffFactory factory) {
        this.character = character;
        this.isDead = isDead;
        this.gameSurface = gameSurface;
        this.factory = factory;
        this.currentAnimationBitmap = this.character.animationMap.get("walkleft");
    }

    public void setAttackVector(int x, int y) {
        this.attackVectorX = x;
        this.attackVectorY = y;
    }

    public Pair<Integer, Integer> getAttackVector() {
        return new Pair<Integer, Integer>(this.attackVectorX, this.attackVectorY);
    }

    public void wander() {
    }

    public void onUpdate(int movingVectorX, int movingVectorY) {

        this.animate(movingVectorX, movingVectorY);

    }

    // TODO: Implement attack speed so things don't die instantly
    private void meleeAttack() {
        this.isAttacking = true;
        this.attackAnimationInProgress = false;
        Iterator<Character> iterator = gameSurface.dungeon.getCurrentRoom().monsterList.iterator();

        while(iterator.hasNext()){
            Character other = iterator.next();
            if(character.hurtBox.x < other.hitBox.x + other.width &&
                    character.hurtBox.x + character.hurtBox.width > other.x &&
                    character.hurtBox.y < other.hitBox.y + other.height &&
                    character.hurtBox.y + character.hurtBox.height > other.hitBox.y){
                other.reduceHitPointsBy(this.character.attackDamage);
            }
        }
    }

    private void rangedAttack() {
        // Create projectile in correct direction
        if(attackVectorX != 0 || attackVectorY != 0) {
            Bitmap projectileBitmap = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.fireball2);
            Projectile projectile = factory.projectile(projectileBitmap, attackVectorX, attackVectorY, true, character.getX() + Math.round(character.width/2) - Math.round(projectileBitmap.getWidth()/2), character.getY() + Math.round(character.height/2) - Math.round(projectileBitmap.getHeight()/2));
        }
    }


    public void attack() {
        long now = System.nanoTime();

        if (this.lastAttackNanoTime == -1)
            this.lastAttackNanoTime = now;

        // Convert nanoseconds to milliseconds
        double deltaTime = (double) ((now - this.lastAttackNanoTime)/1000000);

        // If time between attacks was greater than 1 second, for example if they stop attacking
        if (deltaTime > (((double) 1)/((double) this.character.hitsPerSecond))*1000) {
            if (this.attackStyle.equals("Melee")) {
                this.meleeAttack();
            } else {
                this.rangedAttack();
            }
            this.lastAttackNanoTime = now;
        }
    }

    public Bitmap getCurrentBitmap() {
        return this.currentAnimationBitmap.get(this.colUsing);
    }

    public void animate(int movingVectorX, int movingVectorY) {
        int vectorX = movingVectorX;
        int vectorY = movingVectorY;

        if (this.attackVectorX != 0 || this.attackVectorY != 0) {
            vectorX = this.attackVectorX;
            vectorY = this.attackVectorY;
        }

        int vectorXAbsolute = Math.abs(vectorX);
        int vectorYAbsolute = Math.abs(vectorY);

        // TODO: If I move up, I should not be set to the right/left, it should be based on if I'm moving up/left or up/right and same for bottom
        // This should be fixed in the orcAI and slimeAI as well

        if ( vectorX != 0 || vectorY != 0 ) {
            if (vectorX > 0) {
                if (vectorY > 0 && vectorXAbsolute < vectorYAbsolute) {
                    // Moving down and to the right
                    if (!this.isAttacking) {
                        this.currentAnimationBitmap = this.character.animationMap.get("walkright");
                    } else {
                        this.currentAnimationBitmap = this.character.animationMap.get("attack1right");
                    }
                } else if (vectorY < 0 && vectorXAbsolute < vectorYAbsolute) {
                    // Moving up and to the right
                    if (!this.isAttacking) {
                        this.currentAnimationBitmap = this.character.animationMap.get("walkright");
                    } else {
                        this.currentAnimationBitmap = this.character.animationMap.get("attack1right");
                    }
                } else {
                    // Moving right
                    if (!this.isAttacking) {
                        this.currentAnimationBitmap = this.character.animationMap.get("walkright");
                    } else {
                        this.currentAnimationBitmap = this.character.animationMap.get("attack1right");
                    }
                }
            } else {
                if (vectorY > 0 && vectorXAbsolute < vectorYAbsolute) {
                    // Moving down and to the left
                    if (!this.isAttacking) {
                        this.currentAnimationBitmap = this.character.animationMap.get("walkleft");
                    } else {
                        this.currentAnimationBitmap = this.character.animationMap.get("attack1left");
                    }
                } else if (vectorY < 0 && vectorXAbsolute < vectorYAbsolute) {
                    // Moving up and to the left
                    if (!this.isAttacking) {
                        this.currentAnimationBitmap = this.character.animationMap.get("walkleft");
                    } else {
                        this.currentAnimationBitmap = this.character.animationMap.get("attack1left");
                    }
                } else {
                    // Moving left
                    if (!this.isAttacking) {
                        this.currentAnimationBitmap = this.character.animationMap.get("walkleft");
                    } else {
                        this.currentAnimationBitmap = this.character.animationMap.get("attack1left");
                    }
                }
            }
        } else {
            // TODO: Need idle animation
            // This should be fixed in the orc AI and slime AI (?) as well
            this.currentAnimationBitmap = this.character.animationMap.get("walkleft");
        }

        double movingVectorLength = Math.sqrt(movingVectorX*movingVectorX + movingVectorY*movingVectorY);

        if (movingVectorLength > 0 || this.isAttacking) {
            this.colUsing++;

            if (this.isAttacking) {
                if(!attackAnimationInProgress) {
                    attackAnimationInProgress = true;
                }
                if(this.colUsing >= this.currentAnimationBitmap.size()) {
                    this.isAttacking = false;
                    attackAnimationInProgress = false;
                }
            }

            if (this.colUsing >= this.currentAnimationBitmap.size()) {
                this.colUsing = 0;
            }
        }
    }


    public boolean hasWeapon(){
        return true;
    }
    public boolean isPlayer(){
        return true;
    }


    //TODO: Think of PC specific actions
}
