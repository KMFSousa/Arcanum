package org.o7planning.android2dgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;


public class Map {

    // Have another class for each 'tile' of the grid
    // Store all tiles within a 2D array
    // We'll have a camera class which will be able to see a subset of all the tiles in the 2D array
    // When we render tiles, we identify what the camera can see, and then extract that subset of tiles
    // https://jellepelgrims.com/articles/roguelike_java

    protected Bitmap bitmap;
    protected Tile[][] tileArray;
    protected int rows;
    protected int columns;
    protected int tileWidth;
    protected int tileHeight;

    private boolean needsRedraw = true;

    private GameSurface gameSurface;

    public Map(GameSurface gameSurface, Bitmap image) {
        this.gameSurface = gameSurface;
        // TODO: For some reason screen still returns display metrics as if it is in portrait, even though set to landscape
        // TODO: Thus, swapping width and height for now
        int screenWidth = Resources.getSystem().getDisplayMetrics().heightPixels; // For landscape mode, width = height of portrait
        int screenHeight =  Resources.getSystem().getDisplayMetrics().widthPixels; // For landscape mode, height = width of portrait
        this.bitmap = Bitmap.createScaledBitmap(image, screenWidth, screenHeight, true);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        this.rows = 9; // 16:9 map aspect ratio to function with most resolutions
        this.columns = 16;
        this.tileWidth = bitmapWidth/columns;
        this.tileHeight = bitmapHeight/rows;
        this.tileArray = new Tile[rows][columns];
        this.createTiles(bitmap, rows, columns);
    }

    private void createTiles (Bitmap image, int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // TODO: For some reason,
                tileArray[i][j] = new Tile(createSubImageAt(image, i, j), j*tileWidth, i*tileHeight);
            }
        }
    }

    private Bitmap createSubImageAt(Bitmap image, int row, int col) {
        // createBitmap(bitmap, x, y, width, height);
        // Log.i("Map", "x: " + Integer.toString(col*this.tileWidth) + ", y: " + Integer.toString((row*this.tileHeight)));
        Bitmap subImage = Bitmap.createBitmap(image, col * this.tileWidth, row * this.tileHeight, this.tileWidth, this.tileHeight);
        return subImage;
    }

    public void update () {
        // Only update if the player interacts with something or we change rooms, we don't need to keep re-drawing the tiles
    }


    public void draw (Canvas canvas) {

        if (this.needsRedraw) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    tileArray[i][j].draw(canvas);
                }
            }

            // TODO: add this back
            //  this.needsRedraw = false;
        }
        // TODO: Iterate over the 2D tile array, draw everything out (is it good to draw in a loop?)
        // canvas.drawBitmap(this.bitmap, this.x, this.y, null);
    }

}
