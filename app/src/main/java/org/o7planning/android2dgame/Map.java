package org.o7planning.android2dgame;

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
    protected int x;
    protected int y;

    private boolean needsRedraw = true;

    private GameSurface gameSurface;

    private void createTiles (Bitmap image, int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // TODO: IMPORTANT! May be able to use the GameObject's `createSubImageAt()` method, which returns a bitmap from a subset of the source bitmap
                // TODO: It sets the width and height of the object based on the width and height of the original image, divided by the rows/columns
                // TODO: We should probably call the `createSubImageAt()` here as the rowCount and colCount properties are unneeded for a Tile object, and then pass the sub image into the `Tile()` constructor
                // TODO: Would need to extend GameObject

                // TODO: Divide up map into rows and columns, then pass each sub-bitmap into the `Tile()` constructor along with the `x` and `y`
                // TODO: Then, place the created tile into its respective place in the `tileArray`
                // TODO: This method should do the dividing of the main image into tiles
            }
        }
    }

    public Map(GameSurface gameSurface, Bitmap image, int x, int y) {
        this.gameSurface = gameSurface;
        this.bitmap = image;
        this.x = x;
        this.y = y;

        // TODO: Get screen size, divide into tiles of some width/height (may be pre-determined), initialize the `tileArray`
        // TODO: Then, pass the # of rows and columns as well as the whole background image to the `createTiles()` method
    }

    public void update () {
        // Only update if the player interacts with something or we change rooms, we don't need to keep re-drawing the tiles
    }


    public void draw (Canvas canvas) {

        if (this.needsRedraw) {


            this.needsRedraw = false;
        }
        // TODO: Iterate over the 2D tile array, draw everything out (is it good to draw in a loop?)

        // canvas.drawBitmap(this.bitmap, this.x, this.y, null);
    }

}
