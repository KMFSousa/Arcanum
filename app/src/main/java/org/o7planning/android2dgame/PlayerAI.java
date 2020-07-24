package org.o7planning.android2dgame;

import android.util.Log;

import java.util.Iterator;

public class PlayerAI implements CharacterAI {

    public static boolean isDead;
    public Character character;
    public String attackStyle = "Melee";
    private int attackVectorX = 0;
    private int attackVectorY = 0;
    private GameSurface gameSurface;
    private long lastAttackNanoTime = -1;
    private double damageDealt = 0;

    public PlayerAI(Character character, GameSurface gameSurface) {
        this.character = character;
        this.isDead = isDead;
        this.gameSurface = gameSurface;
    }

    public void setAttackVector(int x, int y) {
        this.attackVectorX = x;
        this.attackVectorY = y;
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
                    other.reduceHitPointsBy(this.damageDealt);
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
        if (deltaTime > 1000) {
            // Cap deltaTime at 1 second -- this means their first hit will do full damage
            // TODO: Can adjust as necessary
            deltaTime = 1000;
        }

        // Let's say we want a DPS equal to the character's attack damage, which for argument's sake is 10
        // We need to scale down the damage done based on how much time has passed such that only 10 damage can occur in 1 second
        // If, for example, a tick occurred in 14 milliseconds, we would need 1 second/14 milliseconds = ~71 ticks to make up 1 seconds
        // If we want 10 DPS, this means that we should be dividing our wanted DPS by the # of ticks to get how much damage we should be doing per tick
        if (now != this.lastAttackNanoTime) {
            double numTicksForOneSecond = 1000/deltaTime;
            this.damageDealt = this.character.attackDamage/numTicksForOneSecond;

            if (this.attackStyle.equals("Melee")) {
                this.meleeAttack();
            } else {
                this.rangedAttack();
            }
        }

        this.lastAttackNanoTime = now;
    }

    public boolean hasWeapon(){
        return true;
    }
    public boolean isPlayer(){
        return true;
    }


    //TODO: Think of PC specific actions
}
