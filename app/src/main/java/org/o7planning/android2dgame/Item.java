package org.o7planning.android2dgame;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.Iterator;

public class Item extends GameObject {

    private GameSurface gameSurface;

    private Bitmap[] sword_neutral;
    private Bitmap[] sword_attack;
    private int colUsing = 0;
    public boolean combatAnimationFinished = true;
    public int attackDMG;


    private String name;
    public String name() { return name; }

    //TODO: FURTHER FLESH OUT THE ITEM CLASS. ALLOW PLAYERS TO PICKUP ITEMS AND PUT THEM IN AN INVENTORY (ALSO IMPLEMENT AN INVENTORY)
    //TODO: IMPLEMENT ITEM STATS

    public Item(GameSurface gameSurface, Bitmap image, String name, int x, int y, int spriteSheetRows, int spriteSheetColumns, int attackDMG) {
        super(image, spriteSheetRows, spriteSheetColumns, x, y);

        this.name = name;

        this.gameSurface = gameSurface;

        this.attackDMG = attackDMG;

        this.sword_neutral = new Bitmap[spriteSheetColumns];
        //this.sword_attack = new Bitmap[spriteSheetColumns];

        for(int col = 0; col< this.colCount; col++ ) {
            this.sword_neutral[col] = this.createSubImageAt(0, col);
        }
    }

    //TODO: SPEED UP ANIMATIONS
    //TODO: SCALE UP THE SWORD
    //TODO: ENABLE ANIMATION INTERRUPT FOR INSTANT ATTACKS
    public void update() {
        if(combatAnimationFinished == false && colUsing <= 5) {
            inCombat();
            colUsing++;
            if (colUsing == 4) {
                colUsing = 0;
                combatAnimationFinished = true;
            }
        }
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = this.sword_neutral[colUsing];
        canvas.drawBitmap(bitmap, x, y, null);
        //hitBox.draw(canvas);
        Log.d("Location" ,"Sword hitbox x: " + hitBox.x);

    }

    //TODO: COLLISION DETECTION CAN BE MOVED WITHIN THE HITBOX CLASS AS A SEPARATE METHOD
    public void inCombat() {
        Iterator<Character> iterator = gameSurface.dungeon.getCurrentRoom().monsterList.iterator();
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
        //gameSurface.removeCharacter(other);
        other.hitPoints =- 10;
    }
}





