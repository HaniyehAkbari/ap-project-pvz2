package com.pvz2.controller;

import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.shop.ShopList;

public class ShopMenuController implements MenuController {
    private ShopList shopList = new ShopList();

    @Override
    public void changeMenu() {
    }

    @Override
    public void exitMenu() {
    }

    public String buyItem(String itemId, int count, String plantTypeName) {
        User user = App.getCurrentUser();
        if (user == null) {
            return "Error: No user logged in.";
        }

        PlantType type = null;
        if (plantTypeName != null && !plantTypeName.isEmpty()) {
            try {
                type = PlantType.valueOf(plantTypeName.toUpperCase());
            } catch (IllegalArgumentException e) {
                return "Error: Invalid plant type.";
            }
        }

        return shopList.buy(itemId, type, count);
    }
}
