package org.o7planning.android2dgame;

import android.util.Log;

import java.util.Iterator;

public class PlayerAI implements CharacterAI {

    public static boolean isDead;
    public Character character;
    private GameSurface gameSurface;

    public PlayerAI(Character character, GameSurface gameSurface) {
        this.character = character;
        this.isDead = isDead;
        this.gameSurface = gameSurface;
    }


    public void wander() {
    }

    public void onUpdate() {
    }

    public void attack() {
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
                    other.reduceHitPointsBy(character.attackDamage);
                }
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
