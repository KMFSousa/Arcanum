package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Tile extends GameObject{
    protected Bitmap bitmap;
    protected int x;
    protected int y;

    public Tile(Bitmap image, int x, int y) {
        super(image, 1, 1, x, y);
        this.bitmap = image;
        this.x = x;
        this.y = y;
    }

    public void update () {

    }


    public void draw (Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }



}
