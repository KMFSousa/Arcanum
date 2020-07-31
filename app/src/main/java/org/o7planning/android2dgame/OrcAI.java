package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class OrcAI implements CharacterAI {

    private GameSurface gameSurface;
    private List<Character> playerList;
    private Character player;
    public Character character;
    private int positionX;
    private int positionY;
    private int distanceToPlayerX;
    private int distanceToPlayerY;
    private int updateCounter;
    private ArrayList<Bitmap> currentAnimationBitmap = new ArrayList<Bitmap>();
    private int colUsing = 0;
    private boolean isAttacking = false;
    private boolean attackAnimationInProgress = false;

    public OrcAI(Character character, GameSurface gameSurface) {
        this.gameSurface = gameSurface;
        this.player = gameSurface.characterList.get(0);
        this.character = character;
        this.currentAnimationBitmap = this.character.animationMap.get("walkleft");
    }

    public void onUpdate(int movingVectorX, int movingVectorY) {
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

        this.animate(movingVectorX, movingVectorY);

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
                this.isAttacking = true;
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
        }

        double movingVectorLength = Math.sqrt(movingVectorX*movingVectorX + movingVectorY*movingVectorY);

        if (movingVectorLength > 0 || this.isAttacking) {
            this.colUsing++;

            if (this.isAttacking) {
                if(!attackAnimationInProgress) {
                    attackAnimationInProgress = true;
                }
                if(this.colUsing == this.currentAnimationBitmap.size()) {
                    isAttacking = false;
                    attackAnimationInProgress = false;
                }
            }

            if (this.colUsing >= this.currentAnimationBitmap.size()) {
                this.colUsing = 0;
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
