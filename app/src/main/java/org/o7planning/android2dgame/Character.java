package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Iterator;

public class Character extends GameObject {

    // The spritesheet is configured such that:
    // Row 0: When character moving down
    // Row 1: When character moving left
    // Row 2: When character moving right
    // Row 3: When character moving up

    private GameSurface gameSurface;

    private static final int ROW_TOP_TO_BOTTOM = 0;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private static final int ROW_LEFT_TO_RIGHT = 2;
    private static final int ROW_BOTTOM_TO_TOP = 3;

    // Row index of Image are being used.
    private int rowUsing = ROW_LEFT_TO_RIGHT;

    // `colUsing` will tell us which stage of the animation we are on
    private int colUsing;

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;

    // Velocity of game character (pixel/millisecond)
    public static final float VELOCITY = 0.1f;

    private int movingVectorX = 0;
    private int movingVectorY = 0;

    private long lastDrawNanoTime =-1;


    private CharacterAI ai;
    public void setCharacterAI(CharacterAI ai) {   this.ai = ai; }

    // This method (called in GameSurface.java) will take the spritesheet we provide it with and create arrays holding the bitmaps of each sprite

    public Character(GameSurface gameSurface, Bitmap image, int x, int y, int spriteSheetRows, int spriteSheetColumns) {
        super(image, spriteSheetRows, spriteSheetColumns, x, y); // Calls

        this.gameSurface= gameSurface;

        this.topToBottoms = new Bitmap[colCount]; // 3
        this.rightToLefts = new Bitmap[colCount]; // 3
        this.leftToRights = new Bitmap[colCount]; // 3
        this.bottomToTops = new Bitmap[colCount]; // 3

        // For each column in the spritesheet, generate arrays that hold the bitmaps created from dividing each row in createSubImageAt

        for(int col = 0; col< this.colCount; col++ ) {
            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            this.rightToLefts[col]  = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            this.bottomToTops[col]  = this.createSubImageAt(ROW_BOTTOM_TO_TOP, col);
        }
    }

    // Depending on what direction the character is moving we want to be able to grab the corresponding set of sprites

    public Bitmap[] getMoveBitmaps()  {
        switch (rowUsing)  {
            case ROW_BOTTOM_TO_TOP:
                return  this.bottomToTops;
            case ROW_LEFT_TO_RIGHT:
                return this.leftToRights;
            case ROW_RIGHT_TO_LEFT:
                return this.rightToLefts;
            case ROW_TOP_TO_BOTTOM:
                return this.topToBottoms;
            default:
                return null;
        }
    }

    // We will be cycling through each set of sprites depending on which direction we are moving
    // This is determined by the switch for `rowUsing` in getMoveBitmaps, called here
    // This function will return which stage of the animation (which specific sprite) is in use, denoted by `colUsing`

    public Bitmap getCurrentMoveBitmap()  {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }

    // This is the character update loop

    public void update()  {

        //check if in combat
        inCombat();

        //update character moving vector and animate
        move();

        //update AI
         ai.onUpdate();

    }

    public void move() {
        // c = sqrt(a^2 + b^2) - i.e. we are getting the movement vector based on how far we moved horizontally and vertically
        double movingVectorLength = Math.sqrt(movingVectorX*movingVectorX + movingVectorY*movingVectorY);

        // Update which column we are using by 1 for each iteration
        // Used in getCurrentMoveBitmap() to grab the next sprite to render on the canvas
        if (movingVectorLength > 0) {
            this.colUsing++;
        }
        // Once we have cycled through all the sprites for a certain direction, start back at the first one
        if(colUsing >= this.colCount)  {
            this.colUsing =0;
        }
        // Current time in nanoseconds
        long now = System.nanoTime();

        // If we have not drawn the sprite yet

        if(lastDrawNanoTime==-1) {
            lastDrawNanoTime= now;
        }
        // Change nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        int deltaTime = (int) ((now - lastDrawNanoTime)/ 1000000 );

        // Distance moved per time unit
        float distance = VELOCITY * deltaTime;

        // Calculate the new position of the game character.
        this.x = x +  (int)(distance* movingVectorX / movingVectorLength);
        this.y = y +  (int)(distance* movingVectorY / movingVectorLength);

        // When the game's character touches the edge of the screen, then change direction
        if(this.x < 0 )  {
            this.x = 0;
            this.movingVectorX = - this.movingVectorX;
        } else if(this.x > this.gameSurface.getWidth() -width)  {
            this.x= this.gameSurface.getWidth()-width;
            this.movingVectorX = - this.movingVectorX;
        }

        if(this.y < 0 )  {
            this.y = 0;
            this.movingVectorY = - this.movingVectorY;
        } else if(this.y > this.gameSurface.getHeight()- height)  {
            this.y= this.gameSurface.getHeight()- height;
            this.movingVectorY = - this.movingVectorY ;
        }

        animate();
    }

    public void animate() {

        // movingVectorX and movingVectorY are +/- values that will determine what direction we are moving in
        // based on the values of these vectors, we can decide which row of sprites we are using
        if( movingVectorX > 0 )  {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_LEFT_TO_RIGHT;
            }
        } else {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_RIGHT_TO_LEFT;
            }
        }
    }

    // This function will get the bitmap of the exact sprite we are on and draw it on the canvas

    public void draw(Canvas canvas)  {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap, x, y, null);
        // Last draw time.
        this.lastDrawNanoTime= System.nanoTime();
    }

    public void setMovingVector(int movingVectorX, int movingVectorY)  {
        this.movingVectorX = movingVectorX;
        this.movingVectorY = movingVectorY;
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
        if(!ai.getType())
            gameSurface.removeCharacter(other);
    }

}