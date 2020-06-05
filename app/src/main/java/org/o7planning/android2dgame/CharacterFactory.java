package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class CharacterFactory {
    private GameSurface gameSurface;

    public CharacterFactory(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
    }

    public Character newPlayer() {
        Bitmap characterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.chibi1);
        Character player = new Character(gameSurface, characterBitmap1, 100, 50 );
        gameSurface.characterList.add(player);
        new PlayerAI(player);
        return player;
    }

    public Character newMonster() {
        Bitmap monsterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.chibi2);
        Character monster1 = new Character(gameSurface, monsterBitmap1, 500, 500);
        gameSurface.monsterList.add(monster1);
        new CharacterAI(monster1);
        return monster1;
    }
}
