package com.pvz2.models.zombie.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ArmorData {
    @JsonProperty("ArmorType")
    private String armorType;

    @JsonProperty("BaseHealth")
    private int baseHealth;

    @JsonProperty("ArmorFlags")
    private List<String> armorFlags = new ArrayList<>();

    public String getArmorType() {
        return armorType;
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public List<String> getArmorFlags() {
        return armorFlags;
    }
}
