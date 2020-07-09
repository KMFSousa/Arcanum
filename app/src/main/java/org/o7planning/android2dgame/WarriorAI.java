package org.o7planning.android2dgame;

import java.util.Iterator;
import java.util.List;

public class WarriorAI extends CharacterAI {

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

    public WarriorAI(Character character, GameSurface gameSurface, StuffFactory factory) {
        super(character);
        this.gameSurface = gameSurface;
        this.factory = factory;
        List<Character> playerList = gameSurface.characterList;
        Iterator<Character> iter = playerList.iterator();
        this.player = iter.next();

    }

    public void onUpdate() {



        if (closeToPlayer()) {
            character.setMovingVector(distanceToPlayerX, distanceToPlayerY);
            character.attack();
        } else if (updateCounter % 5 == 0) {
            updateCounter = 0;
            wander();
        }

        if(spreadCount < 1 && Math.random() < 0.1 )
            spread();

        updateCounter++;
    }

    private void spread() {
        int x = character.x +(int)(Math.random() * 200 - 100);
        int y = character.y +(int)(Math.random() * 200 - 100);

        Character child = factory.newMonster();
        child.x = x;
        child.y = y;
        child.hitBox.x = x;
        child.hitBox.y = y;

        spreadCount++;
    }

    public boolean closeToPlayer() {
        this.positionX = this.character.getX();
        this.postitionY = this.character.getY();

        distanceToPlayerX = player.getX() - positionX;
        distanceToPlayerY = player.getY() - postitionY;

        int distanceToPlayer = GameObject.getDistanceBetweenObjects(this.character, player);

       // Log.d("Distance" ,"Movement" + distanceToPlayer);

        if (distanceToPlayer <= 560) {
            return true;
        }

        return false;
    }

    public boolean getType(){
        return true;
    }
}

