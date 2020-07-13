package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class CharacterFactory {
    private GameSurface gameSurface;

    public CharacterFactory(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
    }

    public Character newPlayer(Dungeon dungeon) {
        Bitmap characterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.chibi1);
        Character player = new Character(gameSurface, characterBitmap1, 200, 200 );
        gameSurface.characterList.add(player);
        PlayerAI playerAI = new PlayerAI(player, dungeon);
        playerAI.character = player;
        player.setCharacterAI(playerAI);
        return player;
    }

    public Character newMonster(Dungeon dungeon) {
        Bitmap monsterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.chibi2);
        Character monster1 = new Character(gameSurface, monsterBitmap1, 500, 500);
        gameSurface.monsterList.add(monster1);
        WarriorAI warriorAI = new WarriorAI(monster1, dungeon, gameSurface, this);
        warriorAI.character = monster1;
        monster1.setCharacterAI(warriorAI);
        return monster1;
    }
}
