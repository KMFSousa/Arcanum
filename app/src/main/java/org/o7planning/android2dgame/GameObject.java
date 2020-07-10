package org.o7planning.android2dgame;

import android.graphics.Bitmap;

public class GameObject {
    protected Bitmap image;
    protected final int rowCount;
    protected final int colCount;

    protected final int WIDTH;
    protected final int HEIGHT;

    protected final int width;
    protected final int height;

    protected int x;
    protected int y;

    protected HitBox hitBox;
    public void setObjectHitbox(HitBox hitBox) {   this.hitBox = hitBox; }

    protected HitBox hurtBox;
    public void setObjectHurtbox(HitBox hurtBox) {   this.hurtBox = hurtBox; }

    public GameObject(Bitmap image, int rowCount, int colCount, int x, int y) {
        this.image = image;
        this.rowCount = rowCount;
        this.colCount = colCount;

        this.x = x;
        this.y = y;

        this.WIDTH = image.getWidth();
        this.HEIGHT = image.getHeight();

        this.width = this.WIDTH/colCount;
        this.height = this.HEIGHT/rowCount;
    }

    public static int getDistanceBetweenObjects(GameObject obj1, GameObject obj2) {
        return  (int) Math.sqrt(
                Math.pow( (double) obj2.getX() - (double) obj1.getX(), 2) +
                        Math.pow((double) obj2.getY() - (double) obj1.getY(), 2)
        );

    }

    protected Bitmap createSubImageAt(int row, int col) {
        //createBitmap(bitmap, x, y, width, height);
        Bitmap subImage = Bitmap.createBitmap(image, col * this.width, row * this.height, this.width, this.height);
        return subImage;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean isWithinBounds(int targetX, int targetY) {
        return this.x < targetX && targetX < this.x + this.width && this.y < targetY && targetY < this.y + this.height;
    }
}

