package org.o7planning.android2dgame;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.Integer.parseInt;

public class LootTables {
//    protected final int mobID;
    protected String[][] dropTable;
    protected String[][] mobTable;
    protected int dropRows;
    protected int dropCol;
    protected int mobRow;
    protected int mobCol;
    public Character player;
    private Context context;
    public LootTables(Context context ) {
        this.context = context;
        this.mobRow = 3;
        this.mobCol = 7;
        this.dropRows = 6;
        this.dropCol = 3;
        this.dropTable = new String[dropRows][dropCol];
        this.mobTable = new String[mobRow][mobCol];
        this.populateCsvArray();
        this.player = player;
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
                        this.dropTable[row-1][col] = value;
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
                        Log.i("row and col", lineArray.length + "") ;

                        this.mobTable[row-1][col] = value;
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

    public List<Integer> roulette (int mobID) {
        int itemQuantity = parseInt(this.mobTable[mobID][5]);
        List<Integer> output = new ArrayList<Integer>();
        int itemLB = parseInt(this.mobTable[mobID][3]);
        int itemUB = parseInt(this.mobTable[mobID][4]);
        int num_items = 0;
        Random ran = new Random();
        int roulette[] = new int[] {
                -1, -1, -1, -1, -1, -1
        };

        for(int i = 0; i < this.dropTable.length; i ++) {
//
            int dropVal = parseInt(this.dropTable[i][2]);
            if (dropVal <= itemUB && dropVal >= itemLB) {
                roulette[num_items] = parseInt(this.dropTable[i][0]);
                num_items++;
            }
        }
//        Log.i("Test:", itemQuantity + " Wtff");
//        for (int j =0; j < roulette.length; j++) {
//            Log.i("Roulette Items", roulette[j]);
//        }

        int index = 0;
        for (int i = 0; i < itemQuantity; i++) {
            int randomInt = ran.nextInt(roulette.length);
            output.add(roulette[randomInt]);
//            Log.i("Test Output:", output[index]);
            index ++;
        }

        return output;
    }

    public void applyItems (List<Integer> items, Character player) {

        for (int item : items) {
            Log.i("ApplyItems", item+"");
            if(item == 0){
                Log.i("Here: ", "IT SHOULD WORK " + player.hitPoints);
                player.hitPoints = player.MAXHITPOINTS;
                Log.i("Hit points", player.hitPoints + "");
                player.replenishHitpoints();
            } else if(item == 1) {
                Log.i("Attack Before:", player.attackDamage + "");
                player.attackDamage +=10;
                Log.i("Attack after:", player.attackDamage + "");
            } else if(item == 2) {
                player.defence += 1;
            } else if(item == 3) {
                // Implement Speed change
            } else if(item == 4) {
                Log.i("Attack Before:", player.attackDamage + "");
                player.MAXHITPOINTS += 10;
                Log.i("Attack after:", player.attackDamage + "");
            } else if(item == 5) {
                player.numRocks ++;
                if (player.numRocks == 5) {
                    player.attackDamage +=10;
                    player.numRocks = 0;
                }
            }
        }
    }

}

