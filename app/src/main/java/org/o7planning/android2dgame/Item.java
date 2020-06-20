package org.o7planning.android2dgame;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Item extends GameObject {

    private GameSurface gameSurface;
    private Bitmap[] sword;


    private String name;
    public String name() { return name; }


    public Item(GameSurface gameSurface, Bitmap image, String name, int x, int y) {
        super(image, 1, 1, x, y);
        this.name = name;
    }
}


//
//    public Character(GameSurface gameSurface, Bitmap image, int x, int y, int spriteSheetRows, int spriteSheetColumns) {
//        super(image, spriteSheetRows, spriteSheetColumns, x, y); // Calls
//
//        this.gameSurface= gameSurface;
