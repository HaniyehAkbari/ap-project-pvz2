package com.pvz2.models.shop;

public class ShopItem {
    private String id;
    private String name;
    private int coinCost;
    private int diamondCost;

    public ShopItem(String id, String name, int coinCost, int diamondCost, int maxCapacity, boolean isPermanent) {
        this.id = id;
        this.name = name;
        this.coinCost = coinCost;
        this.diamondCost = diamondCost;
    }

    public boolean isAffordable(int coins, int diamonds) {
        return coins >= coinCost && diamonds >= diamondCost;
    }

    public String getName() {
        return name;
    }

    public int getCoinCost() {
        return coinCost;
    }

    public int getDiamondCost() {
        return diamondCost;
    }

    public String getId() {
        return id;
    }
}
