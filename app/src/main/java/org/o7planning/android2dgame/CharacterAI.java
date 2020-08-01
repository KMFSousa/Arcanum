package org.o7planning.android2dgame;

import java.util.Iterator;

public interface CharacterAI {

    //TODO: Different 'types' of enemies will have different AI classes that will extend this class

    public void wander();

    public void onUpdate();

    public void attack();

    public boolean hasWeapon();

    public boolean isPlayer();
}
