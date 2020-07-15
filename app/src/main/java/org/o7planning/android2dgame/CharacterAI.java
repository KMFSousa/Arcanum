package org.o7planning.android2dgame;

import java.util.Iterator;

public class CharacterAI {
    protected Character character;
    protected Dungeon dungeon;

    public CharacterAI(Character character, Dungeon dungeon) {
//        this.character = character;
//        this.character.setCharacterAI(this);
        this.dungeon = dungeon;
    }

    //TODO: Different 'types' of enemies will have different AI classes that will extend this class

    public void wander() {
        int x = (int)(Math.random() * 200 - 100);
        int y = (int)(Math.random() * 200 - 100);
        character.setMovingVector(x, y);
    }

    public void onUpdate() {

    }



    public boolean hasWeapon(){ return false;}
    public boolean isPlayer(){
        return false;
    }
}
