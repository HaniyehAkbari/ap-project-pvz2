package com.pvz2.models.miniGame.IZombie;

public class Brain {
    private int row;
    private float x;
    private float y;
    private boolean eaten;

    public Brain(int row, float x, float y) {
        this.row = row;
        this.x = x;
        this.y = y;
        this.eaten = false;
    }

    public int getRow() {
        return row;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isEaten() {
        return eaten;
    }

    public void eat() {
        this.eaten = true;
    }
}
