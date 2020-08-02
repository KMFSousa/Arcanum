package org.o7planning.android2dgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.Integer.parseInt;

public class LootTables {
    protected String[][] dropTable;
    protected String[][] mobTable;
    protected int dropRows;
    protected int dropCol;
    protected int mobRow;
    protected int mobCol;
    public Character player;
    private Context context;
    protected TextView itemText;

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
        this.itemText = (TextView) ((Activity)context).findViewById(R.id.itemsText);
    }

    public void  populateCsvArray() {
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
                        this.mobTable[row-1][col] = value;
                    }
                }
                row++;
            }

        } catch (IOException e) {
            System.out.println("Exception in populateCSVArray in LootTable Class:");
            System.out.println(e.getMessage());
        }
    }

    public List<Integer> roulette (int mobID) {
        List<Integer> output = new ArrayList<Integer>();
        int itemLB = parseInt(this.mobTable[mobID][3]);
        int itemUB = parseInt(this.mobTable[mobID][4]);
        int num_items = 0;
        Random ran = new Random();
        int roulette[] = new int[] {
                -1, -1, -1, -1, -1, -1
        };


        for(int i = 0; i < this.dropTable.length; i ++) {
            int dropVal = parseInt(this.dropTable[i][2]);
            if (dropVal <= itemUB && dropVal >= itemLB) {
                roulette[num_items] = parseInt(this.dropTable[i][0]);
                num_items++;
            }
        }

        final int randomInt = ran.nextInt(roulette.length);
        Activity mainActivity = (MainActivity) context;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemText.setText(dropTable[randomInt][1]);
            }
        });

        output.add(roulette[randomInt]);

        return output;
    }

    public void applyItems (List<Integer> items, Character player) {

        for (int item : items) {
            if(item == 0){
                player.hitPoints = player.MAXHITPOINTS;
                player.replenishHitpoints();
            } else if(item == 1) {
                player.attackDamage +=10;
            } else if(item == 2) {
                player.defense += 1;
            } else if(item == 3) {
                // Implement Speed change
                player.hitsPerSecond += 0.5;
            } else if(item == 4) {
                player.MAXHITPOINTS += 10;
            } else if(item == 5) {
                player.numRocks ++;
                if (player.numRocks == 2) {
                    player.attackDamage +=5;
                    player.numRocks = 0;
                }
            }
        }
    }

}


