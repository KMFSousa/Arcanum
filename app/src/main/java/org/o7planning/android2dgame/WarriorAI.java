package org.o7planning.android2dgame;

public class WarriorAI extends CharacterAI {

    public WarriorAI(Character character) {
        super(character);
    }

    public void onUpdate() {
        wander();
    }
}
