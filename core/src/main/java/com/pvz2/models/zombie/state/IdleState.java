package com.pvz2.models.zombie.state;

import com.pvz2.models.zombie.Zombie;

public class IdleState implements ZombieState {
    @Override
    public void handleAction(Zombie zombie, float delta) {
    }

    @Override
    public String getAnimationClip() {
        return "idle";
    }
}
