package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class StuffFactory {
    private GameSurface gameSurface;

    public StuffFactory(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
    }

    //TODO: UPDATE CHARACTER AND ITEM CONSTRUCTORS WITH ATTRIBUTES

    public Character newPlayer() {
        Bitmap characterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.spritesheet);

        Character player = new Character(gameSurface, characterBitmap1, 100, 50, 4, 4, 0.4f, 100, 10 );
        gameSurface.characterList.add(player);
        PlayerAI playerAI = new PlayerAI(player);
        playerAI.character = player;
        player.setCharacterAI(playerAI);

        Bitmap playerHitbox1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.characterhitbox);
        HitBox hitBox1 = new HitBox(gameSurface, playerHitbox1, 200, 200, player);
        hitBox1.object = player;
        player.setObjectHitbox(hitBox1);

        Bitmap playerHurtbox1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.characterhitbox);
        HitBox hurtBox1 = new HitBox(gameSurface, playerHurtbox1, 200, 200, player);
        hurtBox1.object = player;
        player.setObjectHurtbox(hurtBox1);


        return player;
    }

    public Character newMonster() {
        Bitmap monsterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.slimes1);
        Character monster1 = new Character(gameSurface, monsterBitmap1, 500, 500, 4, 5, 0.1f, 30, 1);
        gameSurface.monsterList.add(monster1);
        SlimeAI warriorAI = new SlimeAI(monster1, gameSurface, this);
        warriorAI.character = monster1;
        monster1.setCharacterAI(warriorAI);

        Bitmap monsterHitbox1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.characterhitbox);
        HitBox hitBox1 = new HitBox(gameSurface, monsterHitbox1, 500, 500, monster1);
        hitBox1.object = monster1;
        monster1.setObjectHitbox(hitBox1);

        Bitmap monsterHurtbox1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.characterhitbox);
        HitBox hurtBox1 = new HitBox(gameSurface, monsterHurtbox1, 500, 500, monster1);
        hurtBox1.object = monster1;
        monster1.setObjectHurtbox(hurtBox1);

        return monster1;
    }

//    public Item newSword() {
//        Bitmap swordBitmap = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.sword_attack_animation);
//        Item sword = new Item(gameSurface, swordBitmap, "sword", 100, 100, 1, 5, 10);
//        gameSurface.itemList.add(sword);
//        Bitmap swordHitbox1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.sword_hitbox);
//        HitBox hitBox1 = new HitBox(gameSurface, swordHitbox1, 100, 100, sword);
//        hitBox1.object = sword;
//        sword.setObjectHitbox(hitBox1);
//        return sword;
//    }
}
