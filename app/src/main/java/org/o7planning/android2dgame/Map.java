package org.o7planning.android2dgame;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.security.AccessController.getContext;

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
        int screenHeight =  Resources.getSystem().getDisplayMetrics().heightPixels;
        int screenWidth =  Resources.getSystem().getDisplayMetrics().widthPixels;
        this.bitmap = Bitmap.createScaledBitmap(image, screenWidth,screenHeight, true);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        this.rows = 16; // 16:9 map aspect ratio to function with most resolutions
        this.columns = 9;
        this.tileWidth = bitmapWidth/rows;
        this.tileHeight = bitmapHeight/columns;
        this.tileArray = new Tile[rows][columns];
        this.createTiles(bitmap, rows, columns);

        // TODO: Get screen size, divide into tiles of some width/height (may be pre-determined), initialize the `tileArray`
        // TODO: Then, pass the # of rows and columns as well as the whole background image to the `createTiles()` method
    }

    private void createTiles (Bitmap image, int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                tileArray[i][j] = new Tile(createSubImageAt(image, j, i), i*tileWidth, j*tileHeight);
                // TODO: Divide up map into rows and columns, then pass each sub-bitmap into the `Tile()` constructor along with the `x` and `y`
                // TODO: Then, place the created tile into its respective place in the `tileArray`
                // TODO: This method should do the dividing of the main image into tiles
            }
        }
    }

    private Bitmap createSubImageAt(Bitmap image, int row, int col) {
        //createBitmap(bitmap, x, y, width, height);
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
