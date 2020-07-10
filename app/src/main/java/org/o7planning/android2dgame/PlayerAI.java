package org.o7planning.android2dgame;

import java.util.Iterator;

public class PlayerAI extends CharacterAI {

    public static boolean isDead;
    private GameSurface gameSurface;


    public PlayerAI(Character character) {
        super(character);


        this.isDead = isDead;
    }

    public void onUpdate() {



    }

    public boolean getType(){
        return false;
    }


    //TODO: Think of PC specific actions
}
