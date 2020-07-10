package org.o7planning.android2dgame;

import java.util.List;

public class OrcAI extends CharacterAI {

    private GameSurface gameSurface;
    private List<Character> playerList;
    private final Character player;
    private StuffFactory factory;
    private int positionX;
    private int postitionY;

    private int distanceToPlayerX;
    private int distanceToPlayerY;

    private int updateCounter;
    public OrcAI(Character character, GameSurface gameSurface) {
        super(character);
        this.player = gameSurface.characterList.get(0);
    }

    public void onUpdate() {

        if (closeToPlayer()) {
            character.setMovingVector(distanceToPlayerX, distanceToPlayerY);
            character.attack();
        } else if (updateCounter % 5 == 0) {
            updateCounter = 0;
            wander();
        }
        updateCounter++;

    }

    public boolean closeToPlayer() {
        this.positionX = this.character.getX();
        this.postitionY = this.character.getY();

        distanceToPlayerX = player.getX() - positionX;
        distanceToPlayerY = player.getY() - postitionY;

        int distanceToPlayer = GameObject.getDistanceBetweenObjects(this.character, player);


        if (distanceToPlayer <= 560) {
            return true;
        }

        return false;
    }

}
