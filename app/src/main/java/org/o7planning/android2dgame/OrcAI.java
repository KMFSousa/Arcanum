package org.o7planning.android2dgame;

import java.util.List;

public class OrcAI extends CharacterAI {

    private GameSurface gameSurface;
    private List<Character> playerList;
    private final Character player;
    private StuffFactory factory;
    private int positionX;
    private int positionY;
    private int mobID;

    private int distanceToPlayerX;
    private int distanceToPlayerY;

    private int updateCounter;

    public OrcAI(Character character, Dungeon dungeon, GameSurface gameSurface) {
        super(character, dungeon);
        this.gameSurface = gameSurface;
        this.factory = factory;
        this.player = gameSurface.characterList.get(0);
        this.mobID = 1;
    }

    public void onUpdate() {

        if (closeToPlayer()) {

        }
            // TODO: Base pathing in accordance to line of sight
            // Draw a vector from monster to player
            // Sample along the vector and calculate if there is a tile that is collidable at each sample.
            // If so, line of sight is broken and go back to wandering.
            // Otherwise, move towards player
            // Probably sample every 1/2 tile width

            this.positionX = this.character.getX() + this.character.width/2;
            this.positionY = this.character.getY() + this.character.height/2;

            distanceToPlayerX = player.getX() + player.width/2 - this.positionX;
            distanceToPlayerY = player.getY() + player.height/2 - this.positionY;

            int distanceToPlayer = GameObject.getDistanceBetweenObjects(this.character, player);

            int tileWidth = this.dungeon.getCurrentRoom().tileWidth;

            int numTileChecks = (int) Math.ceil(distanceToPlayer/(tileWidth/4));

            int xValToCheck;
            int yValToCheck;

            boolean lineOfSight = true;

            for (int i = 1; i < numTileChecks; i++) {
                xValToCheck = this.positionX + i*((distanceToPlayerX)/numTileChecks);
                yValToCheck = this.positionY + i*((distanceToPlayerY)/numTileChecks);

                if (this.dungeon.getCurrentRoom().isPointCollidable(xValToCheck, yValToCheck)) {
                    lineOfSight = false;
                }
            }

            if (lineOfSight) {
                character.setMovingVector(distanceToPlayerX, distanceToPlayerY);
            } else if (updateCounter % 5 == 0) {
                this.updateCounter = 0;
                this.wander();
            }

        updateCounter++;


    }

    public boolean closeToPlayer() {
        int distanceToPlayer = GameObject.getDistanceBetweenObjects(this.character, player);


        if (distanceToPlayer <= 1200) { // TODO: Change back to 560
            return true;
        }

        return false;
    }

}
