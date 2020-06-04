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
        return player;
    }

    public Monster newMonster() {
        Bitmap monsterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.chibi2);
        Monster monster1 = new Monster(gameSurface, monsterBitmap1, 1000, 1000);
        gameSurface.monsterList.add(monster1);
        return monster1;
    }
}
