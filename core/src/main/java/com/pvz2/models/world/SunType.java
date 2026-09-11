package com.pvz2.models.world;

public enum SunType {
    NORMAL(25),
    SPECIAL(100),
    RADIOACTIVE(0);

    public final int amount;

    SunType(int amount) {
        this.amount = amount;
    }
}
