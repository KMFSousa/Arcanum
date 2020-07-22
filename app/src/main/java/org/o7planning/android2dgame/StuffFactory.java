package org.o7planning.android2dgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.List;


public class StuffFactory {
    private GameSurface gameSurface;

    public StuffFactory(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
    }

    //TODO: UPDATE CHARACTER AND ITEM CONSTRUCTORS WITH ATTRIBUTES

    public Character newPlayer(List<Character> characterList, int x, int y) {
        Bitmap characterBitmap1 = BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.spritesheet);

        Character player = new Character(gameSurface, characterBitmap1, x, y, true, 4, 4, 0.3f, 100, 10, 3 );
        characterList.add(player);
        PlayerAI playerAI = new PlayerAI(player, gameSurface);
        playerAI.character = player;
        player.setCharacterAI(playerAI);

        Bitmap playerHitbox1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.characterhitbox);
        HitBox hitBox1 = new HitBox(gameSurface, playerHitbox1, x, y, player);
        hitBox1.object = player;
        player.setObjectHitbox(hitBox1);

        Bitmap playerHurtbox1 = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.characterhitbox);
        HitBox hurtBox1 = new HitBox(gameSurface, playerHurtbox1, x, y, player);
        hurtBox1.object = player;
        player.setObjectHurtbox(hurtBox1);

        Bitmap playerHealthBar = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.healthbar);
        playerHealthBar = Bitmap.createScaledBitmap(playerHealthBar, 400, 40,false);
        GameObject healthBar = new GameObject(playerHealthBar, 1, 1, 15, 15);
        player.setHealthBar(healthBar);

        return player;
    }

    public Character newSlime(List<Character> characterList, int x, int y) {
        Bitmap slimeBitmap = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.slimes1);
        Character slime = new Character(gameSurface, slimeBitmap, x, y, false, 4, 5, 0.1f, 30, 1, 4);
        characterList.add(slime);
        SlimeAI slimeAI = new SlimeAI(slime, gameSurface, this);
        slimeAI.character = slime;
        slime.setCharacterAI(slimeAI);

        Bitmap slimeHitbox = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.characterhitbox);
        HitBox hitBox1 = new HitBox(gameSurface, slimeHitbox, x, y, slime);
        hitBox1.object = slime;
        slime.setObjectHitbox(hitBox1);

        Bitmap slimeHurtbox = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.characterhitbox);
        HitBox hurtBox1 = new HitBox(gameSurface, slimeHurtbox, x, y, slime);
        hurtBox1.object = slime;
        slime.setObjectHurtbox(hurtBox1);

        Bitmap slimeHealthBar = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.healthbar);
        slimeHealthBar = Bitmap.createScaledBitmap(slimeHealthBar, 200, 20,false);
        GameObject healthBar = new GameObject(slimeHealthBar, 1, 1, slime.x, slime.y);
        slime.setHealthBar(healthBar);

        return slime;
    }

    public Character newOrc(List<Character> characterList, int x, int y) {
        Bitmap orcBitmap = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.orc);
        orcBitmap = Bitmap.createScaledBitmap(orcBitmap, 1000, 500, false);
        Character orc = new Character(gameSurface, orcBitmap, x, y, false, 4, 8, 0.1f, 30, 1, 4);
        characterList.add(orc);
        OrcAI orcAI = new OrcAI(orc, gameSurface);
        orcAI.character = orc;
        orc.setCharacterAI(orcAI);

        Bitmap orcHitbox = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.orc_body_hitbox);
        HitBox hitBox2 = new HitBox(gameSurface, orcHitbox, x, y, orc);
        hitBox2.object = orc;
        orc.setObjectHitbox(hitBox2);

        Bitmap orcHurtbox = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.orc_spear_extended_hurtbox);
        HitBox hurtBox1 = new HitBox(gameSurface, orcHurtbox, x, y, orc);
        hurtBox1.object = orc;
        orc.setObjectHurtbox(hurtBox1);

        Bitmap orcHealthBar = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.healthbar);
        orcHealthBar = Bitmap.createScaledBitmap(orcHealthBar, 200, 20, false);
        GameObject healthBar = new GameObject(orcHealthBar, 1, 1, orc.x, orc.y);
        orc.setHealthBar(healthBar);

        return orc;
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
