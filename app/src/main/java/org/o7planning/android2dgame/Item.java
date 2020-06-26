package org.o7planning.android2dgame;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.Iterator;

public class Item extends GameObject {

    private GameSurface gameSurface;

    private Bitmap[] sword_nuetral;
    private Bitmap[] sword_attack;
    private int colUsing = 0;
    private boolean combatAnimationFinished = true;


    private String name;
    public String name() { return name; }

 //TODO: FURTHER FLESH OUT THE ITEM CLASS. ALLOW PLAYERS TO PICKUP ITEMS AND PUT THEM IN AN INVENTORY (ALSO IMPLEMENT AN INVENTORY)
 //TODO: IMPLEMENT ITEM STATS
    
    public Item(GameSurface gameSurface, Bitmap image, String name, int x, int y, int spriteSheetRows, int spriteSheetColumns) {
        super(image, spriteSheetRows, spriteSheetColumns, x, y);

        this.name = name;

        this.gameSurface = gameSurface;

        this.sword_nuetral = new Bitmap[spriteSheetColumns];
        //this.sword_attack = new Bitmap[spriteSheetColumns];

        for(int col = 0; col< this.colCount; col++ ) {
            this.sword_nuetral[col] = this.createSubImageAt(0, col);
        }
    }

    public void update() {
        if(combatAnimationFinished == false && colUsing <= 5) {
            colUsing++;
            if (colUsing == 4) {
                colUsing = 0;
                combatAnimationFinished = true;
            }
        }
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = this.sword_nuetral[colUsing];
        canvas.drawBitmap(bitmap, x, y, null);
        //hitBox.draw(canvas);
        Log.d("Location" ,"Sword hitbox x: " + hitBox.x);

    }

    //TODO: COLLISION DETECTION CAN BE MOVED WITHIN THE HITBOX CLASS AS A SEPARATE METHOD
    public void inCombat() {
        Iterator<Character> iterator = gameSurface.monsterList.iterator();
        combatAnimationFinished = false;
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





