package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Tile extends GameObject{
    protected Bitmap bitmap;
    protected int x;
    protected int y;
    private boolean isCollidable;

    public Tile(Bitmap image, int x, int y, boolean isCollidable) {
        super(image, 1, 1, x, y);
        this.bitmap = image;
        this.x = x;
        this.y = y;
        this.isCollidable = isCollidable;
    }

    public void update () {

    }

    public boolean isCollidable() {
        return this.isCollidable;
    }

    public void draw (Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }



}
