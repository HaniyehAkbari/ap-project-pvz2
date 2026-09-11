package com.pvz2.controller;

import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.greenhouse.GreenHouse;

public class GreenhouseMenuController implements MenuController {

    @Override
    public void changeMenu() {
    }

    @Override
    public void exitMenu() {
    }

    private GreenHouse getGreenHouse() {
        User user = App.getCurrentUser();
        return (user != null) ? user.getGreenhouse() : null;
    }

    public String plantPot(int x, int y) {
        GreenHouse greenHouse = getGreenHouse();
        return (greenHouse != null) ? greenHouse.plantPot(x, y) : "Error: No user logged in.";
    }

    public String collect(int x, int y) {
        GreenHouse greenHouse = getGreenHouse();
        return (greenHouse != null) ? greenHouse.collect(x, y) : "Error: No user logged in.";
    }

    public String grow(int x, int y) {
        GreenHouse greenHouse = getGreenHouse();
        return (greenHouse != null) ? greenHouse.grow(x, y) : "Error: No user logged in.";
    }
}
