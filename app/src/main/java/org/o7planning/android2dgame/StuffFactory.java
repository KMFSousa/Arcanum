package org.o7planning.android2dgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StuffFactory {
    private GameSurface gameSurface;
    public Map<String, ArrayList<Bitmap>> playerAnimationMap = null;
    public Map<String, ArrayList<Bitmap>> orcAnimationMap = null;
    public Map<String, ArrayList<Bitmap>> slimeAnimationMap = null;
    public Map<String, ArrayList<Bitmap>> bossAnimationMap = null;
    public Character player;

    public StuffFactory(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
        this.player = this.gameSurface.player;
    }

    //TODO: UPDATE CHARACTER AND ITEM CONSTRUCTORS WITH ATTRIBUTES

    public Character newPlayer(List<Character> characterList, int x, int y, Context context) {
        Bitmap characterBitmap = BitmapFactory.decodeResource(gameSurface.getResources(),R.drawable.barbarian);
        characterBitmap = Bitmap.createScaledBitmap(characterBitmap, 1980, 90, false);
        Character player = new Character(gameSurface, characterBitmap, x, y, true, 1, 11, 0.3f, 120 - (this.gameSurface.difficulty * 20), 11 - this.gameSurface.difficulty, 3, 3, context, "player", 3);
        characterList.add(player);
        this.gameSurface.player = player;
        PlayerAI playerAI = new PlayerAI(player, gameSurface, this);
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

    public Character newSlime(List<Character> characterList, int x, int y, Context context, Boolean addedMidGame) {
        Bitmap slimeBitmap = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.slimes1);
        slimeBitmap = Bitmap.createScaledBitmap(slimeBitmap, 480, 96, false);
        Character slime = new Character(gameSurface, slimeBitmap, x, y, false, 1, 5, 0.1f, 30 + 10*this.gameSurface.difficulty, 1, 3, 4, context, "slime", 0);
        if (addedMidGame) {
            this.gameSurface.charactersToAddList.add(slime);
        } else {
            characterList.add(slime);
        }
        SlimeAI slimeAI = new SlimeAI(slime, gameSurface, this, context);
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

    public Character newOrc(List<Character> characterList, int x, int y, Context context, Boolean addedMidGame) {
        Bitmap orcBitmap = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.orc);
        orcBitmap = Bitmap.createScaledBitmap(orcBitmap, 1800, 128, false);
        Character orc = new Character(gameSurface, orcBitmap, x, y, false, 1, 16, 0.2f, 40 + 10*this.gameSurface.difficulty, 1, 2, 4, context, "orc", 1);
        if (addedMidGame) {
            this.gameSurface.charactersToAddList.add(orc);
        } else {
            characterList.add(orc);
        }

        OrcAI orcAI = new OrcAI(orc, gameSurface);
        orcAI.character = orc;
        orc.setCharacterAI(orcAI);

        Bitmap orcHitbox = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.orc_body_hitbox);
        orcHitbox = Bitmap.createScaledBitmap(orcHitbox, 90, 90, false);
        HitBox hitBox2 = new HitBox(gameSurface, orcHitbox, x, y, orc);
        hitBox2.object = orc;
        orc.setObjectHitbox(hitBox2);

        Bitmap orcHurtbox = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.orc_spear_extended_hurtbox);
        orcHurtbox = Bitmap.createScaledBitmap(orcHurtbox, 90, 90, false);
        HitBox hurtBox1 = new HitBox(gameSurface, orcHurtbox, x, y, orc);
        hurtBox1.object = orc;
        orc.setObjectHurtbox(hurtBox1);

        Bitmap orcHealthBar = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.healthbar);
        orcHealthBar = Bitmap.createScaledBitmap(orcHealthBar, 200, 20, false);
        GameObject healthBar = new GameObject(orcHealthBar, 1, 1, orc.x, orc.y);
        orc.setHealthBar(healthBar);

        return orc;
    }

    public Character newBoss(List<Character> characterList, int x, int y, Context context) {
        Bitmap bossBitmap = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.boss);
        bossBitmap = Bitmap.createScaledBitmap(bossBitmap, 2250, 175, false);
        Character boss = new Character(gameSurface, bossBitmap, x, y, false, 1, 18, 0.1f, 500 + 500*this.gameSurface.difficulty, 3 + this.gameSurface.difficulty, 3, 4, context, "boss", 2);
        characterList.add(boss);
        BossAI bossAI = new BossAI(boss, gameSurface, this, context);
        bossAI.character = boss;
        boss.setCharacterAI(bossAI);

        Bitmap bossHitbox = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.orc_body_hitbox);
        bossHitbox = Bitmap.createScaledBitmap(bossHitbox, 90, 180, false);
        HitBox hitBox2 = new HitBox(gameSurface, bossHitbox, x, y, boss);
        hitBox2.object = boss;
        boss.setObjectHitbox(hitBox2);

        Bitmap bossHurtbox = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.orc_spear_extended_hurtbox);
        bossHurtbox = Bitmap.createScaledBitmap(bossHurtbox, 90, 180, false);
        HitBox hurtBox1 = new HitBox(gameSurface, bossHurtbox, x, y, boss);
        hurtBox1.object = boss;
        boss.setObjectHurtbox(hurtBox1);

        Bitmap bossHealthBar = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.healthbar);
        bossHealthBar = Bitmap.createScaledBitmap(bossHealthBar, 200, 20, false);
        GameObject healthBar = new GameObject(bossHealthBar, 1, 1, boss.x, boss.y);
        boss.setHealthBar(healthBar);

        return boss;
    }

    public Projectile projectile(Bitmap projectileBitmap, int movingVectorX, int movingVectorY, boolean isPlayerOwned, int originX, int originY, float velocity, int attackdamage){
        Projectile projectile = new Projectile(isPlayerOwned, projectileBitmap, 1, 1, originX, originY, movingVectorX, movingVectorY, gameSurface, velocity, attackdamage);
        gameSurface.projectilesToAddList.add(projectile);

        Bitmap projectileHurtbox = BitmapFactory.decodeResource(gameSurface.getResources(), R.drawable.characterhitbox);
        projectileHurtbox = Bitmap.createScaledBitmap(projectileHurtbox, 36, 36, false);
        HitBox hurtBox3 = new HitBox(gameSurface, projectileHurtbox, originX, originY, projectile);
        hurtBox3.object = projectile;
        projectile.setObjectHurtbox(hurtBox3);
        return projectile;
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
