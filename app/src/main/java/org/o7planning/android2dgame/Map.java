package org.o7planning.android2dgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;


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
                // TODO: IMPORTANT! May be able to use the GameObject's `createSubImageAt()` method, which returns a bitmap from a subset of the source bitmap
                // TODO: It sets the width and height of the object based on the width and height of the original image, divided by the rows/columns
                // TODO: We should probably call the `createSubImageAt()` here as the rowCount and colCount properties are unneeded for a Tile object, and then pass the sub image into the `Tile()` constructor
                // TODO: Would need to extend GameObject
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
