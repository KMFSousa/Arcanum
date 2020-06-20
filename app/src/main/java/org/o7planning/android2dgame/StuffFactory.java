package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class StuffFactory {
    private GameSurface gameSurface;

    public StuffFactory(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
    }

    public Character newPlayer() {
        Bitmap characterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.chibi1);
        Character player = new Character(gameSurface, characterBitmap1, 100, 50, 4, 3 );
        gameSurface.characterList.add(player);
        PlayerAI playerAI = new PlayerAI(player);
        playerAI.character = player;
        player.setCharacterAI(playerAI);
//        new PlayerAI(player);
        return player;
    }

    public Character newMonster() {
        Bitmap monsterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.slimes1);
        Character monster1 = new Character(gameSurface, monsterBitmap1, 500, 500, 4, 5);
        gameSurface.monsterList.add(monster1);
        WarriorAI warriorAI = new WarriorAI(monster1, gameSurface, this);
        warriorAI.character = monster1;
        monster1.setCharacterAI(warriorAI);
//        new CharacterAI(monster1);
        return monster1;
    }

    public Item newSword() {
        Bitmap swordBitmap = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.sword);
        Item sword = new Item(gameSurface, swordBitmap, "sword", 100, 100);
        return sword;
    }
}
