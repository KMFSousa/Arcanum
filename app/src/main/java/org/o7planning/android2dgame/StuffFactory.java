package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class StuffFactory {
    private GameSurface gameSurface;

    public StuffFactory(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
    }

    //TODO: UPDATE CHARACTER AND ITEM CONSTRUCTORS WITH ATTRIBUTES

    public Character newPlayer(Dungeon dungeon) {
        Bitmap characterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.spritesheet);


        Character player = new Character(gameSurface, characterBitmap1, 100, 50, 4, 4, 0.3f, 100, 10 );
        gameSurface.characterList.add(player);
        PlayerAI playerAI = new PlayerAI(player, dungeon);
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

    public Character newMonster(Dungeon dungeon) {
        Bitmap monsterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.slimes1);
        Character monster1 = new Character(gameSurface, monsterBitmap1, 500, 500, 4, 5, 0.1f, 30, 1);
        gameSurface.monsterList.add(monster1);
        SlimeAI warriorAI = new SlimeAI(monster1, gameSurface, this, dungeon);
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

    public Character newOrc(Dungeon dungeon) {
        Bitmap monsterBitmap2 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.orc);
        monsterBitmap2 = Bitmap.createScaledBitmap(monsterBitmap2, 1000, 500, false);
        Character monster2 = new Character(gameSurface, monsterBitmap2, 500, 500, 4, 8, 0.1f, 30, 1);
        gameSurface.monsterList.add(monster2);
        OrcAI orcAI = new OrcAI(monster2, dungeon, gameSurface);
        orcAI.character = monster2;
        monster2.setCharacterAI(orcAI);

        Bitmap monsterHitbox2 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.orc_body_hitbox);
        HitBox hitBox2 = new HitBox(gameSurface, monsterHitbox2, 500, 500, monster2);
        hitBox2.object = monster2;
        monster2.setObjectHitbox(hitBox2);

        Bitmap monsterHurtbox1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.orc_spear_extended_hurtbox);
        HitBox hurtBox1 = new HitBox(gameSurface, monsterHurtbox1, 500, 500, monster2);
        hurtBox1.object = monster2;
        monster2.setObjectHurtbox(hurtBox1);

        return monster2;
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
