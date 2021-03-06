package org.o7planning.android2dgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class BossAI implements CharacterAI {

    private GameSurface gameSurface;
    private List<Character> playerList;
    private Character player;
    public Character character;
    private StuffFactory factory;
    private int updateCounter;
    private ArrayList<Bitmap> currentAnimationBitmap = new ArrayList<Bitmap>();
    private int colUsing = 0;
    private Context context;
    private int spawnCount = 0;
    private boolean enraged = false;
    private State currentState = State.IDLE;
    private int attackCounter = 0;

    enum State {
        IDLE,
        SPREAD,
        RAILGUN
    }

    public BossAI(Character character, GameSurface gameSurface, StuffFactory factory, Context context) {
        this.gameSurface = gameSurface;
        this.factory = factory;
        this.player = this.gameSurface.characterList.get(0);
        this.character = character;
        this.context = context;
        this.currentAnimationBitmap = this.character.animationMap.get("idleleft");
    }

    public void onUpdate(int movingVectorX, int movingVectoryY) {
        if (this.character.hitPoints < 0.25*this.character.MAXHITPOINTS) {
            if (spawnCount < 3) {
                this.spawnEnemies();
                this.spawnCount++;
            }
        } else if (this.character.hitPoints < 0.5*this.character.MAXHITPOINTS) {
            this.enraged = true;
            if (spawnCount < 2) {
                this.spawnEnemies();
                this.spawnCount++;
            }
        } else if (this.character.hitPoints < 0.75*this.character.MAXHITPOINTS) {
            if (spawnCount < 1) {
                this.spawnEnemies();
                this.spawnCount++;
            }
        }

        this.attack();
    }

    private void spawnEnemies() {
        this.factory.newOrc(this.gameSurface.dungeon.getCurrentRoom().monsterList, 500, 300, this.context, true);
        this.factory.newOrc(this.gameSurface.dungeon.getCurrentRoom().monsterList, 1200, 300, this.context, true);
        if (this.enraged) {
            this.factory.newOrc(this.gameSurface.dungeon.getCurrentRoom().monsterList, 500, 800, this.context, true);
            this.factory.newOrc(this.gameSurface.dungeon.getCurrentRoom().monsterList, 1200, 800, this.context, true);
        }
    }

    public void attack() {
        switch (this.currentState) {
            case IDLE:
                if (this.player.getX() < this.character.getX()) {
                    this.currentAnimationBitmap = this.character.animationMap.get("idleleft");
                } else {
                    this.currentAnimationBitmap = this.character.animationMap.get("idleright");
                }
                break;
            case RAILGUN:
                if (this.player.getX() < this.character.getX()) {
                    this.currentAnimationBitmap = this.character.animationMap.get("attack1left");
                } else {
                    this.currentAnimationBitmap = this.character.animationMap.get("attack1right");
                }
                if (this.enraged) {
                    // Launch one fireball every frame when enraged
                    this.railgunAttack();
                } else {
                    if (this.attackCounter % 2 == 0) {
                        // Launch one fireball every two frames
                        this.railgunAttack();
                    }
                }
                break;
            case SPREAD:
                if (this.player.getX() < this.character.getX()) {
                    this.currentAnimationBitmap = this.character.animationMap.get("attack2left");
                } else {
                    this.currentAnimationBitmap = this.character.animationMap.get("attack2right");
                }
                if (this.enraged) {
                    if (this.attackCounter % 2 == 0) {
                        // Launch one barrage of projectiles every two frames when enraged
                        this.spreadAttack();
                    }
                } else {
                    if (this.attackCounter % 4 == 0) {
                        // Launch one barrage of projectiles every four frames
                        this.spreadAttack();
                    }
                }

                break;
        }

        this.attackCounter++;
        if (this.attackCounter >= 20) {
            this.attackCounter = 0;

            if (this.currentState == State.IDLE) {
                this.currentState = State.RAILGUN;
            } else if (this.currentState == State.RAILGUN) {
                this.currentState = State.SPREAD;
            } else {
                this.currentState = State.IDLE;
            }
        }

        this.colUsing++;
        if (this.colUsing >= this.currentAnimationBitmap.size()) {
            this.colUsing = 0;
        }
    }

    private void railgunAttack() {
        Bitmap projectileBitmap = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.fireball);
        int positionX = this.character.getX() + this.character.width/2;
        int positionY = this.character.getY() + this.character.height/2;
        int distanceToPlayerX = this.player.getX() + this.player.width/2 - positionX;
        int distanceToPlayerY = this.player.getY() + this.player.height/2 - positionY;
        if (!enraged) {
            factory.projectile(projectileBitmap, distanceToPlayerX, distanceToPlayerY, false, character.getX() + Math.round(character.width/2) - Math.round(projectileBitmap.getWidth()/2), character.getY() + Math.round(character.height/2) - Math.round(projectileBitmap.getHeight()/2), 0.4f, this.character.attackDamage);
        } else {
            factory.projectile(projectileBitmap, distanceToPlayerX, distanceToPlayerY, false, character.getX() + Math.round(character.width/2) - Math.round(projectileBitmap.getWidth()/2), character.getY() + Math.round(character.height/2) - Math.round(projectileBitmap.getHeight()/2), 0.6f, this.character.attackDamage);
        }
    }

    private void spreadAttack() {
        int[] pointXArray = {911, 913, 911, 913, 913, 911};
        int[] pointYArray = {588, 588, 586, 586, 587, 587};
        int[] enragedPointXArray = {911, 913, 911, 913, 913, 911, 912, 912};
        int[] enragedPointYArray = {588, 588, 586, 586, 587, 587, 588, 586};
        int[] xArrayToUse;
        int[] yArrayToUse;

        Bitmap projectileBitmap = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.fireball);
        int positionX = this.character.getX() + this.character.width/2;
        int positionY = this.character.getY() + this.character.height/2;
        int pointX;
        int pointY;
        int distanceToPointX;
        int distanceToPointY;

        if (!enraged) {
            xArrayToUse = pointXArray;
            yArrayToUse = pointYArray;
        } else {
            xArrayToUse = enragedPointXArray;
            yArrayToUse = enragedPointYArray;
        }

        for (int i = 0; i < xArrayToUse.length; i++) {
            pointX = xArrayToUse[i];
            pointY = yArrayToUse[i];
            distanceToPointX = pointX - positionX;
            distanceToPointY = pointY - positionY;
            if (!enraged) {
                factory.projectile(projectileBitmap, distanceToPointX, distanceToPointY, false, character.getX() + Math.round(character.width/2) - Math.round(projectileBitmap.getWidth()/2), character.getY() + Math.round(character.height/2) - Math.round(projectileBitmap.getHeight()/2), 0.2f, this.character.attackDamage);
            } else {
                factory.projectile(projectileBitmap, distanceToPointX, distanceToPointY, false, character.getX() + Math.round(character.width/2) - Math.round(projectileBitmap.getWidth()/2), character.getY() + Math.round(character.height/2) - Math.round(projectileBitmap.getHeight()/2), 0.4f, this.character.attackDamage);
            }
        }
    }

    public Bitmap getCurrentBitmap() {
        return this.currentAnimationBitmap.get(this.colUsing);
    }


    public void animate(int movingVectorX, int movingVectorY) { }

    public void wander() { }

    public boolean hasWeapon() {
        return false;
    }

    public boolean isPlayer() {
        return false;
    }
}
