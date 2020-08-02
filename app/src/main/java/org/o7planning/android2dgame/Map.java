package org.o7planning.android2dgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Build;
import android.util.Log;
import android.util.Pair;

public class Map {
    protected Bitmap currentRoomBitmap;
    protected Tile[][] tileArray;
    protected List<Character> monsterList = new ArrayList<Character>();
    protected String csvName;
    private int[][] csvValues;
    protected int rows;
    protected int columns;
    protected int tileWidth;
    protected int tileHeight;

    private Context context;

    private boolean needsRedraw = true;

    private GameSurface gameSurface;

    private int screenWidth;
    private int screenHeight;

    private int spawnCount;
    private String mapType;
    private String[] monsterTypes;
    private ArrayList<int[]> spawnPoints;

    public Map(GameSurface gameSurface, Bitmap startingImage, String csvName, StuffFactory stuffFactory, Context context) {
        this.gameSurface = gameSurface;
        this.context = context;
        this.csvName = csvName;

        if(Build.FINGERPRINT.contains("generic")) { //Emulator
            this.screenWidth = Resources.getSystem().getDisplayMetrics().heightPixels;
            this.screenHeight = Resources.getSystem().getDisplayMetrics().widthPixels;
        }
        else { //Hardware Phone
            this.screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            this.screenHeight =  Resources.getSystem().getDisplayMetrics().heightPixels;
        }

        this.currentRoomBitmap = Bitmap.createScaledBitmap(startingImage, screenWidth, screenHeight, true);
        int bitmapWidth = currentRoomBitmap.getWidth();
        int bitmapHeight = currentRoomBitmap.getHeight();
        this.rows = 9; // 16:9 map aspect ratio to function with most resolutions
        this.columns = 16;
        this.tileWidth = bitmapWidth/columns;
        this.tileHeight = bitmapHeight/rows;

        this.csvValues = new int[rows][columns];
        this.populateCsvArray();

        this.tileArray = new Tile[rows][columns];
        this.createTiles(currentRoomBitmap, rows, columns);

        this.setMonsterSpawnAndTypes(this.mapType);

        this.determinePossibleSpawnPoints(this.csvValues);

        this.populateMonsterList(stuffFactory);
    }

    // Sets the monster spawn number and types (this can be changed according to what we
    // feel is needed or appropriate)
    private void setMonsterSpawnAndTypes(String mapType) {
        if (mapType == "SPAWN") {
            this.spawnCount = 0;
            this.monsterTypes = new String[0];
        } else if (mapType == "BOSS"){
            this.spawnCount = 1;
            this.monsterTypes = new String[]{"boss"};
        } else if (mapType == "REGULAR"){
            if (this.gameSurface.difficulty == 1) {
                this.spawnCount = this.gameSurface.difficulty + (int)(Math.random() * 2);
                this.monsterTypes = new String[]{"orc"};
            } else {
                this.spawnCount = this.gameSurface.difficulty + (int)(Math.random() * 3);
                this.monsterTypes = new String[]{"orc", "slime"};
            }
        } else {
            // Unknown Map Type
            this.spawnCount = 0;
            this.monsterTypes = new String[0];
        }
    }

    // Sets spawnPoints, a list of coordinates which are
    private void determinePossibleSpawnPoints(int[][] csvValues) {
        int border = 2;
        this.spawnPoints = new ArrayList<int[]>();
        for (int x = 0 + border; x <= (this.rows - 2); x++) {
            for (int y = 0 + border; y <= (this.columns - 2); y++) {
                if (csvValues[x][y] == 0) {
                    this.spawnPoints.add(new int[]{x, y});
                }
            }
        }
    }

    private void populateMonsterList(StuffFactory stuffFactory) {
        Random rand = new Random();

        try {
            for (int i = 0; i < spawnCount; i++) {
                int randomIndex = rand.nextInt(this.spawnPoints.size());
                int [] spawnPoint = this.spawnPoints.get(randomIndex);
                this.spawnPoints.remove(randomIndex);

                int xSpawnLocation = spawnPoint[1] * tileWidth;
                int ySpawnLocation = spawnPoint[0] * tileHeight;

                randomIndex = rand.nextInt(this.monsterTypes.length);
                String monsterType = this.monsterTypes[randomIndex];

                this.callCorrectStuffFactoryMethod(monsterType, xSpawnLocation, ySpawnLocation, stuffFactory);
            }
        } catch (Exception e) {
            // Bad
            System.out.println("Error reading from monster spawns CSV in Game Surface:");
            System.out.println(e.getMessage());
        }
    }

    private void callCorrectStuffFactoryMethod(String name, int xSpawnLocation, int ySpawnLocation, StuffFactory stuffFactory) throws Exception {
        switch(name) {
            case "orc":
                stuffFactory.newOrc(this.monsterList, xSpawnLocation, ySpawnLocation, this.context, false);
                break;
            case "slime":
                stuffFactory.newSlime(this.monsterList, xSpawnLocation, ySpawnLocation, this.context, false);
                break;
            case "boss":
                stuffFactory.newBoss(this.monsterList, 850, 500, this.context);
            default:
                throw new Exception("Error: No Monster Found for Given Type.");
        }
    }

    private int getResourceFromCSVName(String name) throws Exception {
        switch(name) {
            case "blue_room":
                mapType = "REGULAR";
                return R.raw.blue_room;
            case "red_room":
                mapType = "REGULAR";
                return R.raw.red_room;
            case "green_room":
                mapType = "SPAWN";
                return R.raw.green_room;
            case "box_room":
                mapType = "REGULAR";
                return R.raw.box_room;
            case "sewers":
                mapType = "REGULAR";
                return R.raw.sewers;
            case "boss_room":
                mapType = "BOSS";
                return R.raw.boss_room;
            default:
                throw new Exception("Error: No Resource Found for CSV.");
        }
    }

    private void createTiles (Bitmap image, int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                tileArray[i][j] = new Tile(createSubImageAt(image, i, j), j*tileWidth, i*tileHeight, this.csvValues[i][j] == 1);
            }
        }
    }

    private Bitmap createSubImageAt(Bitmap image, int row, int col) {
        Bitmap subImage = Bitmap.createBitmap(image, col * this.tileWidth, row * this.tileHeight, this.tileWidth, this.tileHeight);
        return subImage;
    }

    public void update () {
        // Only update if the player interacts with something or we change rooms, we don't need to keep re-drawing the tiles
    }

    private void populateCsvArray() {
        try {
            InputStream inputStream = this.context.getResources().openRawResource(this.getResourceFromCSVName(this.csvName));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                String[] lineArray = line.split(",");
                for (int col = 0; col < lineArray.length; col++) {
                    int valueAsInt = Integer.parseInt(lineArray[col]);
                    this.csvValues[row][col] = valueAsInt;
                }
                row++;
            }
        } catch (Exception e) {
            System.out.println("Exception when populating map CSV array:");
            System.out.println(e.getMessage());
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

    public Pair<Boolean, Boolean> canMove(boolean isPlayer, int xSrc, int ySrc, int xDest, int yDest, int height, int width) {
        // Character calls this function to determine if it can move to particular x and y
        // We break it down into if it can move in the x direction or the y direction

        // Step 1: Figure out what tiles the new xDestination and yDestination belong to, as well as the current x and y tiles
        int xDestWithWidth = xDest + Math.round(width / 2);
        int xSrcWithWidth = xSrc + Math.round(width / 2);
        int yDestWithHeight = yDest + Math.round(height / 2);
        int ySrcWithHeight = ySrc + Math.round(height / 2);
        // This if check prevents the AI from moving "off the map" and triggering out-of-bounds errors
        // However, the player needs to be able to move "off the map" to trigger the transition, which is what the "isPlayer" check is for
        if (isPlayer || (xDestWithWidth > 0 && xDestWithWidth < this.screenWidth && yDestWithHeight > 0 && yDestWithHeight < this.screenHeight)) {
            int destRow = this.getRowFromY(yDestWithHeight);
            int destCol = this.getColFromX(xDestWithWidth);
            int srcRow = this.getRowFromY(ySrcWithHeight);
            int srcCol = this.getColFromX(xSrcWithWidth);

            // Step 2: Index into tile array to get the tiles associated with the destination for both x and y
            try {
                Tile newColTile = this.tileArray[srcRow][destCol];
                Tile newRowTile = this.tileArray[destRow][srcCol];
                return new Pair<Boolean, Boolean>(!newColTile.isCollidable(), !newRowTile.isCollidable());
            } catch (Exception e) {
                return new Pair<Boolean, Boolean>(false, false);
            }

            // Step 3: Return a pair that defines whether you can move in the x direction or y direction
        } else {
            return new Pair<Boolean, Boolean>(false, false);
        }
    }

    public boolean canMoveProjectile (int xDest, int yDest, int width, int height) {
        int xDestWithWidth = xDest + Math.round(width);
        int yDestWithHeight = yDest + Math.round(height);

        if (xDestWithWidth > 0 && xDestWithWidth < this.screenWidth && yDestWithHeight > 0 && yDestWithHeight < this.screenHeight) {
            int destRow = this.getRowFromY(yDestWithHeight);
            int destCol = this.getColFromX(xDestWithWidth);

            try {
                return !this.tileArray[destRow][destCol].isCollidable();
            } catch (Exception e){
                return false;
            }

        }
        return false;
    }

    public Boolean isPointCollidable(int x, int y) {
        int col = this.getColFromX(x);
        int row = this.getRowFromY(y);

        try {
            return this.tileArray[row][col].isCollidable();
        } catch (Exception e) {
            return true;
        }
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
