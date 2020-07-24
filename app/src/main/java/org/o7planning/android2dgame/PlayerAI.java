package org.o7planning.android2dgame;

import android.util.Log;
import android.util.Pair;

import java.util.Iterator;

public class PlayerAI implements CharacterAI {

    public static boolean isDead;
    public Character character;
    public String attackStyle = "Melee";
    private int attackVectorX = 0;
    private int attackVectorY = 0;
    private GameSurface gameSurface;
    private long lastAttackNanoTime = -1;

    public PlayerAI(Character character, GameSurface gameSurface) {
        this.character = character;
        this.isDead = isDead;
        this.gameSurface = gameSurface;
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

    public void onUpdate() {
    }

    // TODO: Implement attack speed so things don't die instantly
    private void meleeAttack() {
        if(this.isPlayer()){
            character.isAttacking = true;
            character.attackAnimationInProgress = false;
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
    }

    private void rangedAttack() {
        // Create projectile in correct direction
    }


    public void attack() {
        long now = System.nanoTime();

        if (this.lastAttackNanoTime == -1)
            this.lastAttackNanoTime = now;

        // Convert nanoseconds to milliseconds
        double deltaTime = (double) ((now - this.lastAttackNanoTime)/1000000);

        // If time between attacks was greater than 1 second, for example if they stop attacking
        if (deltaTime > (((double) 1)/((double) this.character.hitsPerSecond))*1000) {
            Log.i("tag", "Deltatime: " + deltaTime + ", blah: " + (((double) 1)/((double) this.character.hitsPerSecond))*1000);
            if (this.attackStyle.equals("Melee")) {
                this.meleeAttack();
            } else {
                this.rangedAttack();
            }
            this.lastAttackNanoTime = now;
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
