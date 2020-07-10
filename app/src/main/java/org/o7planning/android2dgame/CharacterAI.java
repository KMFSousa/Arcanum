package org.o7planning.android2dgame;

import java.util.Iterator;

public class CharacterAI {
    protected Character character;

    public CharacterAI(Character character) {
//        this.character = character;
//        this.character.setCharacterAI(this);
    }

    //TODO: Different 'types' of enemies will have different AI classes that will extend this class

    public void wander() {

        int x = (int)(Math.random() * 200 - 100);
        int y = (int)(Math.random() * 200 - 100);
        character.setMovingVector(x, y);
    }

    public void onUpdate() {

    }

    public boolean getType() {
        return false;
    }

    public void attack() {

    }
}
