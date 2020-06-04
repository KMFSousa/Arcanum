package org.o7planning.android2dgame;

public class CharacterAI {
    protected Character character;

    public CharacterAI(Character character) {
        this.character = character;
        this.character.setCharacterAI(this);
    }

    //TODO: Maybe add movement logic in here. It will be the same for both AI and Player characters. More thought required
    //TODO: Different 'types' of enemies will have different AI classes that will extend this class

    public void wander() {

    }

    public void onUpdate() {

    }
}
