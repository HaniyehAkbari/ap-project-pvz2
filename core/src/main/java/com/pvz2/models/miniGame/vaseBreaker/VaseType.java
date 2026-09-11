package com.pvz2.models.miniGame.vaseBreaker;

public enum VaseType {
    NORMAL("🏺"),
    PLANT("🪴"),
    GIANT("🏺✨");

    private final String symbol;

    VaseType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

}
