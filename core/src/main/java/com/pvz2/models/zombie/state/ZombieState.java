package com.pvz2.models.zombie.state;

import com.pvz2.models.zombie.Zombie;

public interface ZombieState {
    void handleAction(Zombie zombie, float delta);
    String getAnimationClip();
}
