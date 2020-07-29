package org.o7planning.android2dgame;

import android.util.Log;

import java.util.List;

public class OrcAI implements CharacterAI {

    private GameSurface gameSurface;
    private List<Character> playerList;
    private Character player;
    public Character character;
    private StuffFactory factory;
    private int positionX;
    private int positionY;

    private int distanceToPlayerX;
    private int distanceToPlayerY;

    private int updateCounter;

    public OrcAI(Character character, GameSurface gameSurface) {
        this.gameSurface = gameSurface;
        this.factory = factory;
        this.player = gameSurface.characterList.get(0);
        this.character = character;
    }

    public void onUpdate() {
        if(closeToPlayer(50)) {
            character.setMovingVector(0, 0);
            attack();
        } else if (closeToPlayer(1200)) {

            // TODO: Base pathing in accordance to line of sight
            // Draw a vector from monster to player
            // Sample along the vector and calculate if there is a tile that is collidable at each sample.
            // If so, line of sight is broken and go back to wandering.
            // Otherwise, move towards player
            // Probably sample every 1/2 tile width

            this.positionX = this.character.getX() + this.character.width/2;
            this.positionY = this.character.getY() + this.character.height/2;

            distanceToPlayerX = player.getX() + player.width/2 - this.positionX;
            distanceToPlayerY = player.getY() + player.height/2 - this.positionY;

            int distanceToPlayer = GameObject.getDistanceBetweenObjects(this.character, player);

            int tileWidth = this.gameSurface.dungeon.getCurrentRoom().tileWidth;

            int numTileChecks = (int) Math.ceil(distanceToPlayer/(tileWidth/4));

            int xValToCheck;
            int yValToCheck;

            boolean lineOfSight = true;

            for (int i = 1; i < numTileChecks; i++) {
                xValToCheck = this.positionX + i*((distanceToPlayerX)/numTileChecks);
                yValToCheck = this.positionY + i*((distanceToPlayerY)/numTileChecks);

                if (this.gameSurface.dungeon.getCurrentRoom().isPointCollidable(xValToCheck, yValToCheck)) {
                    lineOfSight = false;
                }
            }

            if (lineOfSight) {
                character.setMovingVector(distanceToPlayerX, distanceToPlayerY);
            } else if (updateCounter % 20 == 0) {
                this.updateCounter = 0;
                this.wander();
            }
        }
        updateCounter++;
    }


    public void attack() {
        if(!gameSurface.characterList.isEmpty()){
            Character other = gameSurface.characterList.get(0);
            if(character.hurtBox.x < other.hitBox.x + other.hitBox.width &&
                    character.hurtBox.x + character.hurtBox.width > other.hitBox.x &&
                    character.hurtBox.y < other.hitBox.y + other.hitBox.height &&
                    character.hurtBox.y + character.hurtBox.height > other.hitBox.y){
                other.reduceHitPointsBy(character.attackDamage);
                character.isAttacking = true;
            }
        }
    }


    public boolean hasWeapon() {
        return true;
    }


    public boolean isPlayer() {
        return false;
    }

    public boolean closeToPlayer(int requiredDistance) {
        int distanceToPlayer = GameObject.getDistanceBetweenObjects(this.character, player);

        if (distanceToPlayer <= requiredDistance) {
            return true;
        }

        return false;
    }

    public void wander() {
        int x = (int)(Math.random() * 200 - 100);
        int y = (int)(Math.random() * 200 - 100);
        character.setMovingVector(x, y);
    }

}
