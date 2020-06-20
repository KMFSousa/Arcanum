package org.o7planning.android2dgame;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.Iterator;

public class Item extends GameObject {

    private GameSurface gameSurface;

    private Bitmap[] sword;


    private String name;
    public String name() { return name; }

 //TODO: FURTHER FLESH OUT THE ITEM CLASS. ALLOW PLAYERS TO PICKUP ITEMS AND PUT THEM IN AN INVENTORY (ALSO IMPLEMENT AN INVENTORY)
 //TODO: IMPLEMENT ITEM STATS
    
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
        hitBox.draw(canvas);
        Log.d("Location" ,"Sword hitbox x: " + hitBox.x);

    }

    //TODO: COLLISION DETECTION CAN BE MOVED WITHIN THE HITBOX CLASS AS A SEPARATE METHOD
    public void inCombat() {
        Iterator<Character> iterator = gameSurface.monsterList.iterator();

        while(iterator.hasNext()){
            Character other = iterator.next();
            if(this.hitBox.x < other.hitBox.x + other.width &&
                    this.hitBox.x + this.hitBox.width > other.x &&
                    this.hitBox.y < other.hitBox.y + other.height &&
                    this.hitBox.y + this.hitBox.height > other.hitBox.y){
                attack(other);
            }

        }
    }

    public void attack(Character other) {
            gameSurface.removeCharacter(other);
    }
}





