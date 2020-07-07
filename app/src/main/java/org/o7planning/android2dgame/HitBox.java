package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class HitBox extends GameObject {
    protected GameObject object;
    private GameSurface gameSurface;
    private Bitmap[] hitbox;

    public HitBox(GameSurface gameSurface, Bitmap image, int x, int y, GameObject gameObject) {
        super(image, 1, 1, x, y);

        this.gameSurface = gameSurface;

        this.hitbox = new Bitmap[1];

        this.hitbox[0] = this.createSubImageAt(0, 0);
    }

    public void update() {

    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = this.hitbox[0];
        canvas.drawBitmap(bitmap, this.x, this.y, null);

    }
}
