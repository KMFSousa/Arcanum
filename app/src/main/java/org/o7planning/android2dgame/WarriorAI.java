package org.o7planning.android2dgame;

import android.util.Log;

import java.util.Iterator;
import java.util.List;

public class WarriorAI extends CharacterAI {

    private GameSurface gameSurface;
    private List<Character> playerList;
    private final Character player;
    private int positionX;
    private int postitionY;
    public WarriorAI(Character character, GameSurface gameSurface) {
        super(character);
        this.gameSurface = gameSurface;
        List<Character> playerList = gameSurface.characterList;
        Iterator<Character> iter = playerList.iterator()
;       this.player = iter.next();
    }

    public void onUpdate() {
//        wander();
        this.positionX = this.character.getX();
        this.postitionY = this.character.getY();

        int distanceToPlayerX = player.getX() - positionX;
        int distanceToPlayerY = player.getY() - postitionY;

        int distanceToPlayer = GameObject.getDistanceBetweenObjects(this.character, player);

        Log.d("Distance" ,"Movement" + distanceToPlayer);
        if (distanceToPlayer <= 560) {
            // Velocity of monster set
            character.setMovingVector(distanceToPlayerX, distanceToPlayerY);
        } else {
            wander();
        }





        // Calculate vector vector between enemy to player (x and y)
        // Calculate (absolute) distance between enemy and player
        // Set velocity to direction of player
        // Update position
    }
}
