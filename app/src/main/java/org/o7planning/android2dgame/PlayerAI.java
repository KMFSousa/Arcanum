package org.o7planning.android2dgame;

import java.util.Iterator;

public class PlayerAI extends CharacterAI {

    public static boolean isDead;
    private GameSurface gameSurface;
    public Character character;

    public PlayerAI(Character character, Dungeon dungeon) {
        super(character, dungeon);
        this.isDead = isDead;
        this.character = character;
    }

    public void onUpdate() {



    }

    public boolean hasWeapon(){
        return true;
    }
    public boolean isPlayer(){
        return true;
    }


    //TODO: Think of PC specific actions
}
