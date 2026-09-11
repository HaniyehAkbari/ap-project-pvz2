package com.pvz2.models.zombie.data;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ZombieData {
    @JsonProperty("Hitpoints")
    @JsonAlias("hitpoints")
    private int hitpoints;

    @JsonProperty("EatDPS")
    @JsonAlias("eatDPS")
    private int eatDPS;

    @JsonProperty("Speed")
    @JsonAlias("speed")
    private double speed;

    @JsonProperty("ZombieArmorProps")
    @JsonAlias("zombieArmorProps")
    private List<String> zombieArmorProps = new ArrayList<>();

    public int getHitpoints() {
        return hitpoints;
    }

    public int getEatDPS() {
        return eatDPS;
    }

    public double getSpeed() {
        return speed;
    }

    public List<String> getZombieArmorProps() {
        return zombieArmorProps;
    }

}
