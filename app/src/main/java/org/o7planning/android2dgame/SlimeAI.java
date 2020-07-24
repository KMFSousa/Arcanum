package org.o7planning.android2dgame;

import android.util.Log;

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

    public SlimeAI(Character character, GameSurface gameSurface, StuffFactory factory) {
        this.gameSurface = gameSurface;
        this.factory = factory;
        this.player = gameSurface.characterList.get(0);
        this.character = character;

    }

    public void onUpdate() {
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
    }

    public void attack() {
        if(!gameSurface.characterList.isEmpty()){
            Character other = gameSurface.characterList.get(0);
            if(character.hurtBox.x < other.hitBox.x + other.hitBox.width &&
                    character.hurtBox.x + character.hurtBox.width > other.hitBox.x &&
                    character.hurtBox.y < other.hitBox.y + other.hitBox.height &&
                    character.hurtBox.y + character.hurtBox.height > other.hitBox.y){
                other.reduceHitPointsBy((double) character.attackDamage);
                Log.d("Player HP remaining", ": " + other.hitPoints);

                character.isAttacking = true;
            }
        }
    }

    private void spread() {
        int x = character.x;
        int y = character.y;

        Character child = factory.newSlime(this.gameSurface.dungeon.getCurrentRoom().monsterList, x, y);
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

