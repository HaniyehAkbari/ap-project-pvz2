package com.pvz2.models.lawnMower;

import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.zombie.Zombie;

import java.util.List;

public class LawnMower {
    public enum MowerState {
        IDLE,
        MOVING,
        ATTACKING
    }

    private final int row;
    private boolean isActive;
    private boolean isSpent;
    private double positionX;
    private final double maxX;
    private double speed = 150.0;
    private boolean isAlive = true;

    private MowerState state;
    private float attackTimer = 0f;
    private float stateTime = 0f;

    public LawnMower(int row, double startX, double maxX) {
        this.row = row;
        this.isActive = false;
        this.isSpent = false;
        this.positionX = startX;
        this.maxX = maxX;
        this.state = MowerState.IDLE;
    }

    private void changeState(MowerState newState) {
        if (this.state != newState) {
            this.state = newState;
            this.stateTime = 0f;
        }
    }

    public void activate() {
        if (!isActive && !isSpent) {
            this.isActive = true;
            this.isAlive = true;
            changeState(MowerState.MOVING);
        }
    }

    public void mowZombies(List<Zombie> zombiesInRow) {
        if (!isActive) return;

        boolean hitZombie = false;
        for (Zombie z : zombiesInRow) {
            if (!z.isDead() && z.getX() <= this.positionX) {
                if (!z.isBoss()) {
                    z.setKiller(null);
                    z.takeDamage(99999, "MOWER");
                    hitZombie = true;

                    User user = App.getCurrentUser();
                    if (user != null) {
                        user.getQuestStats().incrementLawnmowerKills();
                        user.getQuestManager().checkAllQuests(user);
                    }
                }
            }
        }

        if (hitZombie) {
            changeState(MowerState.ATTACKING);
            this.attackTimer = 0.5f;
        }
    }

    public void move(float delta) {
        stateTime += delta;

        if (isActive) {
            positionX += speed * delta;

            if (state == MowerState.ATTACKING) {
                attackTimer -= delta;
                if (attackTimer <= 0) {
                    changeState(MowerState.MOVING);
                }
            }

            if (isOutOfBounds()) {
                isAlive = false;
                isActive = false;
                isSpent = true;
            }
        }
    }

    public boolean isOutOfBounds() {
        return positionX > maxX;
    }

    public boolean isAlive() { return isAlive; }
    public boolean isActive() { return isActive; }
    public int getRow() { return row; }
    public boolean isSpent() { return isSpent; }
    public double getPositionX() { return positionX; }
    public MowerState getState() { return state; }
    public float getStateTime() { return stateTime; }
}
