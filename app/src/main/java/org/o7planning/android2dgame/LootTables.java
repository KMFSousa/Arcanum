package org.o7planning.android2dgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
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
    protected ListView itemListView;
    public String[][] playerItems;

    public LootTables(Context context ) {
        this.context = context;
        this.mobRow = 3;
        this.mobCol = 7;
        this.dropRows = 10;
        this.dropCol = 5;
        this.dropTable = new String[dropRows][dropCol];
        this.mobTable = new String[mobRow][mobCol];
        this.populateCsvArray();
        this.player = player;
        this.itemText = (TextView) ((Activity)context).findViewById(R.id.itemsText);
        this.itemListView = (ListView) ((Activity)context).findViewById(R.id.total_items);
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
        int dropQuant = parseInt(this.mobTable[mobID][5]);
        int num_items = 0;
        Random ran = new Random();
        int roulette[] = new int[] {
                -1, -1, -1, -1, -1, -1,-1,-1,-1,-1,-1
        };


        for(int i = 0; i < this.dropTable.length; i ++) {
            int dropVal = parseInt(this.dropTable[i][2]);
            if (dropVal <= itemUB && dropVal >= itemLB) {
                roulette[num_items] = parseInt(this.dropTable[i][0]);
                num_items++;
            }
        }

        String dropString = "Recent Drop: ";
        int itemQuan;
        for(int j = 0; j < dropQuant; j++ ) {
            final int randomInt = ran.nextInt(roulette.length);
            output.add(roulette[randomInt]);
            if (roulette[randomInt] != -1) {
                if (j == dropQuant - 1) {
                    dropString = dropString + dropTable[randomInt][3];
                } else {
                    dropString = dropString + dropTable[randomInt][3] + ", ";
                }
            }
        }
        final String display = dropString;

        Activity mainActivity = (MainActivity) context;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (display != "Recent Drops: ") {
                    itemText.setText(display);
                }
            }
        });
        return output;
    }

    public ArrayList<List<String>> applyItems (List<Integer> items, Character player, ArrayList<List<String>> playerItems) {
        int Quantity;
        for (int item : items) {
            if(item == 0){
                //Potion
                player.hitPoints = player.MAXHITPOINTS;
                player.replenishHitpoints();
            } else if (item == 1) {
                //Sword
                player.attackDamage += parseInt(dropTable[1][4]);
                Quantity = parseInt(playerItems.get(1).get(1));
                Quantity ++;
                playerItems.get(1).set(1, Integer.toString(Quantity)) ;
            } else if(item == 2) {
                //Shield
                player.defense += parseInt(dropTable[2][4]);
                Quantity = parseInt(playerItems.get(2).get(1));
                Quantity ++;
                playerItems.get(2).set(1, Integer.toString(Quantity));
            } else if(item == 3) {
                //Gloves
                // Implement Speed change
                player.hitsPerSecond += Float.parseFloat(dropTable[3][4]);
                Quantity = parseInt(playerItems.get(3).get(1));
                Quantity ++;
                playerItems.get(3).set(1, Integer.toString(Quantity));
            } else if(item == 4) {
                //Armor
                player.MAXHITPOINTS += parseInt(dropTable[4][4]);
                Quantity = parseInt(playerItems.get(4).get(1));
                Quantity ++;
                playerItems.get(4).set(1,Integer.toString(Quantity));
            } else if(item == 5) {
                //Rocks
                player.numRocks++;
                if (player.numRocks == 2) {
                    player.attackDamage += 1;
                    player.numRocks = 0;
                }
                Quantity = parseInt(playerItems.get(5).get(1));
                Quantity ++;
                playerItems.get(5).set(1, Integer.toString(Quantity));
            } else if(item == 6) {
                //Holy Sword
                player.attackDamage += parseInt(dropTable[6][4]);
                Quantity = parseInt(playerItems.get(6).get(1));
                Quantity ++;
                playerItems.get(6).set(1,Integer.toString(Quantity));

            } else if(item == 7) {
                //Holy Shield
                player.defense += parseInt(dropTable[7][4]);
                Quantity = parseInt(playerItems.get(7).get(1));
                Quantity ++;
                playerItems.get(7).set(1, Integer.toString(Quantity));
            } else if(item == 8) {
                //Holy Gloves
                player.hitsPerSecond += parseInt(dropTable[8][4]);
                Quantity = parseInt(playerItems.get(8).get(1));
                Quantity ++;
                playerItems.get(8).set(1, Integer.toString(Quantity));
            } else if(item == 9) {
                // Holy Armor
                player.MAXHITPOINTS += parseInt(dropTable[9][4]);
                Quantity = parseInt(playerItems.get(9).get(1));
                Quantity ++;
                playerItems.get(9).set(1, Integer.toString(Quantity));
            } else if (item == -1) {

            }
        }


        final ArrayList<String> array = convert2d(playerItems);
        final ArrayList<List<String>> showList = playerItems;

        Activity mainActivity = (MainActivity) context;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> aa = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, array) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView tv = (TextView) super.getView(position, convertView, parent);
                        tv.setTextColor(Color.parseColor("#986333"));
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
                        tv.setMaxHeight(50);

//                        tv.setLayoutParams();
                        return tv;
                    }
                };

                itemListView.setAdapter(aa);
            }
        });

        return playerItems;
    }

    public ArrayList<String> convert2d (ArrayList<List<String>> playeritems) {
        ArrayList<String> array = new ArrayList<String>();
        for (List<String> row: playeritems) {
            String converted ;
            converted = row.get(0) + ":" + row.get(1);
            array.add(converted);
        }

        return array;
    }
}


