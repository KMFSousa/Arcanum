package org.o7planning.android2dgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class Map {
    protected Bitmap currentRoomBitmap;
    protected Tile[][] tileArray;
    private int[][] csvValues;
    protected int rows;
    protected int columns;
    protected int tileWidth;
    protected int tileHeight;

    private Context context;

    private boolean needsRedraw = true;

    private GameSurface gameSurface;

    public Map(GameSurface gameSurface, Bitmap startingImage, Context context) {
        this.gameSurface = gameSurface;
        this.context = context;

        // TODO: Use the below screenWidth and screenHeight properties if you are testing on an actual phone
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight =  Resources.getSystem().getDisplayMetrics().heightPixels;

        // TODO: Use the below screenWidth and screenHeight properties if you are testing on the emulator
        //int screenWidth = Resources.getSystem().getDisplayMetrics().heightPixels;
        //int screenHeight =  Resources.getSystem().getDisplayMetrics().widthPixels;

        this.currentRoomBitmap = Bitmap.createScaledBitmap(startingImage, screenWidth, screenHeight, true);
        int bitmapWidth = currentRoomBitmap.getWidth();
        int bitmapHeight = currentRoomBitmap.getHeight();
        this.rows = 9; // 16:9 map aspect ratio to function with most resolutions
        this.columns = 16;
        this.tileWidth = bitmapWidth/columns;
        this.tileHeight = bitmapHeight/rows;

        this.csvValues = new int[rows][columns];
        this.populateCsvArray("main.csv");

        this.tileArray = new Tile[rows][columns];
        this.createTiles(currentRoomBitmap, rows, columns);
    }

    private void createTiles (Bitmap image, int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                tileArray[i][j] = new Tile(createSubImageAt(image, i, j), j*tileWidth, i*tileHeight, this.csvValues[i][j] == 1 ? true : false);
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

    private void populateCsvArray(String fileName) {
        try {
            InputStream inputStream = this.context.getResources().openRawResource(R.raw.main);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                String[] lineArray = line.split(",");
                for (int col = 0; col < lineArray.length; col++) {
                    int valueAsInt = Integer.parseInt(lineArray[col]);
                    this.csvValues[row][col] = valueAsInt;
                    // Log.i("Map", "Row: " + row + ", Col: " + col + ", Val: " + lineArray[col]);
                }
                row++;
            }
        } catch (IOException e) {
            // Bad
        }
    }

    private int getColFromX(int x) {
        for (int i = 0; i < this.columns; i++) {
            if (i*this.tileWidth <= x && x < i*this.tileWidth + this.tileWidth) {
                return i;
            }
        }
        return -1;
    }

    private int getRowFromY(int y) {
        for (int i = 0; i < this.rows; i++) {
            if (i*this.tileHeight <= y && y < i*this.tileHeight + this.tileHeight) {
                return i;
            }
        }
        return -1;
    }

    public boolean canMove(int x, int y, int height, int width) {
        // Character calls this function to determine if it can move to particular x and y

        // Step 1: Figure out what tiles the incoming x and y belong to
        int row = this.getRowFromY(y + Math.round(height/2));
        int col = this.getColFromX(x + Math.round(width/2));

        // Step 2: Index into tile array to get the right tiles
        Tile tile = this.tileArray[row][col];

        // Step 3: Check if tile is collidable
        if (tile.isCollidable()) {
            return false;
        }
        return true;
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
