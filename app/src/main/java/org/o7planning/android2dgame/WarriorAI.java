package org.o7planning.android2dgame;

public class WarriorAI extends CharacterAI {

    int updateCounter = 0;

    public WarriorAI(Character character) {
        super(character);
    }

    public void onUpdate() {

        if(updateCounter % 10 == 0) {
            wander();
        }

        if(updateCounter > 100) {
            updateCounter = 0;
        }

        updateCounter++;
    }
}
