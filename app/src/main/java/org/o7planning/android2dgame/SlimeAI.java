package org.o7planning.android2dgame;

import java.util.Iterator;
import java.util.List;

public class SlimeAI extends CharacterAI {

    private GameSurface gameSurface;
    private List<Character> playerList;
    private final Character player;
    private StuffFactory factory;
    private int positionX;
    private int postitionY;

    private int distanceToPlayerX;
    private int distanceToPlayerY;

    private int updateCounter;

    private int spreadCount;

    public SlimeAI(Character character, GameSurface gameSurface, StuffFactory factory) {
        super(character);
        this.gameSurface = gameSurface;
        this.factory = factory;
        List<Character> playerList = gameSurface.characterList;
        Iterator<Character> iter = playerList.iterator();
        this.player = iter.next();

    }

    public void onUpdate() {

        if (updateCounter % 5 == 0) {
            updateCounter = 0;
            wander();
        }


        if(spreadCount < 1 && Math.random() < 0.1 && gameSurface.dungeon.currentMonsterList.size() < 10)
           spread();

            updateCounter++;
    }

    private void spread() {
        int x = character.x;
        int y = character.y;

        Character child = factory.newSlime(this.gameSurface.dungeon.currentMonsterList, x, y);
        child.x = x;
        child.y = y;
        child.hitBox.x = x;
        child.hitBox.y = y;

        spreadCount++;
    }

    public boolean hasWeapon(){
        return false;
    }
    public boolean isPlayer(){
        return false;
    }
}

