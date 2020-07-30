package org.o7planning.android2dgame;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class BossAI implements CharacterAI {

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
    private ArrayList<Bitmap> currentAnimationBitmap = new ArrayList<Bitmap>();
    private int colUsing = 0;
    private Context context;

    private boolean enraged = false;

    public BossAI(Character character, GameSurface gameSurface) {
        this.gameSurface = gameSurface;
        this.factory = factory;
        this.player = gameSurface.characterList.get(0);
        this.character = character;
    }

    public void onUpdate(int movingVectorX, int movingVectoryY) {
        // TODO: Check health percent, determine if we are spawning enemies
        // TODO: Check if boss is enraged or not
        // TODO:
    }

    public void attack() {
        // TODO: Choose an attack
    }

    public Bitmap getCurrentBitmap() {
        return this.currentAnimationBitmap.get(this.colUsing);
    }


    public void animate(int movingVectorX, int movingVectorY) {


    }

    public void wander() {

    }

    public boolean hasWeapon() {
        return false;
    }

    public boolean isPlayer() {
        return false;
    }
}
