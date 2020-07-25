package org.o7planning.android2dgame;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LootTables {
//    protected final int mobID;
    private String[][] dropTable;
    private String[][] mobTable;
    protected int dropRows;
    protected int dropCol;
    protected int mobRow;
    protected int mobCol;
    private Context context;
    public LootTables(Context context) {
        this.context = context;
        this.mobRow = 4;
        this.mobCol = 6;
        this.dropRows = 7;
        this.dropCol = 3;
        this.dropTable = new String[dropRows][dropCol];
        this.mobTable = new String[mobRow][mobCol];
        this.populateCsvArray();

    }

    public void  populateCsvArray() {
//        String dropsFile = "";
//        String mobsFile = "";
        try {
            InputStream inputStream_drops = this.context.getResources().openRawResource(R.raw.drops_array);
            InputStream inputStream_mobs = this.context.getResources().openRawResource(R.raw.mob_array);
            BufferedReader drops_reader = new BufferedReader(new InputStreamReader(inputStream_drops));
            BufferedReader mobs_reader = new BufferedReader(new InputStreamReader(inputStream_mobs));

            String dropLine;
            int row = 0;
            while((dropLine = drops_reader.readLine()) != null) {
                String[] lineArray = dropLine.split(",");
                for( int col = 0; col <lineArray.length; col ++) {
                    if (row != 0) {
                        String value = lineArray[col];
                        Log.i("Col Val:", value);
                        this.dropTable[row][col] = value;
                    }
                }
                row++;
            }
            String mobLine;
            row = 0;
            while((mobLine = mobs_reader.readLine()) != null) {
                String[] lineArray = mobLine.split(",");
                for( int col = 0; col <lineArray.length; col ++) {
                    if (row != 0) {
                        String value = lineArray[col];
                        Log.i("Col Val:", value);
                        this.mobTable[row][col] = value;
                    }
                }
                row++;
            }

//            for (int i = 0; i < dropTable.length; i ++){
//                String output = "";
//                for (int j = 0; j < dropTable[i].length; j++){
//                    output = output + dropTable[i][j];
//                }
//                Log.i("DropTable Row:" + i, output);
//            }
//
//            for (int i = 0; i < mobTable.length; i ++){
//                String output = "";
//                for (int j = 0; j < mobTable[i].length; j++){
//                    output = output + mobTable[i][j];
//                }
//                Log.i("MobTable Row: " + i, output);
//            }
        } catch (IOException e) {
            Log.i("ERROR", "ERRRRRROOOOORRR");
        }
    }

    public String[] roulette (int mobID) {
        int itemQuantity =
        String[] output = new String[];
    }
}
