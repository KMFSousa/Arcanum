package org.o7planning.android2dgame;

import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {
    private Map[][] rooms;
    private int roomX, roomY;
    private GameSurface gameSurface;
    public List<Character> currentMonsterList;

    public Dungeon(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
        this.currentMonsterList = new ArrayList<Character>();
    }

    public void populateMapArray(Map[][] rooms) {
        this.rooms = rooms;
        roomX = 0;
        roomY = 0;
    }


    public Map getCurrentRoom(){
        return rooms[roomX][roomY];
    }

    public void draw(Canvas canvas) {
        Map currRoom = getCurrentRoom();
        if(currRoom != null) {
            currRoom.draw(canvas);
        }
    }

    public void transitionVertical(int direction){ //Direction should be +1 for upwards room change, -1 for downwards
        if(roomY + direction >= 0  && roomY + direction < rooms[0].length) {
            this.getCurrentRoom().monsterList = this.currentMonsterList;
            roomY += direction;
            this.currentMonsterList = this.getCurrentRoom().monsterList;
            Log.d("Dungeon","Vertical Transition: " + direction);
        }
    }

    public void transitionHorizontal(int direction){ //Direction should be +1 for right room change, -1 for left
        if(roomX + direction >= 0  && roomX + direction < rooms[0].length) {
            this.getCurrentRoom().monsterList = this.currentMonsterList;
            roomX += direction;
            this.currentMonsterList = this.getCurrentRoom().monsterList;
            Log.d("Dungeon","Horizontal Transition: " + direction);
        }
    }




}
