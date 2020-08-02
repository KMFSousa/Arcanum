package org.o7planning.android2dgame;

import android.graphics.Bitmap;

import java.util.Iterator;

public interface CharacterAI {

    //TODO: Different 'types' of enemies will have different AI classes that will extend this class

    public void animate(int movingVectorX, int movingVectorY);

    public boolean hasWeapon();

    public boolean isPlayer();

    public Bitmap getCurrentBitmap();

    public void onUpdate(int movingVectorX, int movingVectorY);

    public void wander();

}
