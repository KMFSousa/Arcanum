package org.o7planning.android2dgame;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Iterator;

public class Item extends GameObject {

    private GameSurface gameSurface;

    private Bitmap[] sword;


    private String name;
    public String name() { return name; }


    public Item(GameSurface gameSurface, Bitmap image, String name, int x, int y) {
        super(image, 1, 1, x, y);

        this.name = name;

        this.gameSurface = gameSurface;

        this.sword = new Bitmap[1];

        this.sword[0] = this.createSubImageAt(0, 0);
    }

    public void update() {

    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = this.sword[0];
        canvas.drawBitmap(bitmap, x, y, null);

    }

    public void inCombat() {
        Iterator<Character> iterator = gameSurface.monsterList.iterator();

        while(iterator.hasNext()){
            Character other = iterator.next();
            if(this.getX() < other.x + other.getWidth() && other.x < this.getX() + this.getWidth()
                    && this.getY() < other.y && other.y < this.getY() + this.getHeight()){
                attack(other);
            }

        }
    }

    public void attack(Character other) {
            gameSurface.removeCharacter(other);
    }
}





