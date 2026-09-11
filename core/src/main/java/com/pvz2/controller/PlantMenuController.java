package com.pvz2.controller;

import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantFamily;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.plant.card.PlantCardFactory;
import com.pvz2.models.world.levelSetup.PlantWhatYouGetLevelSetup;
import com.pvz2.view.screen.MenuScreen;

import java.util.*;

public class PlantMenuController implements MenuController {
    private Set<PlantType> selectedPlants = new HashSet<>();
    private int maxSlots = 8;
    private PlantType imitatorTarget = null;

    @Override
    public void changeMenu() {
    }

    public void reset() {
        selectedPlants.clear();
        maxSlots = 8 - App.getCurrentGame().getPlantLists().size();
        this.imitatorTarget = null;
    }

    public boolean addPlant(String typeName, MenuScreen screen) {
        User user = App.getCurrentUser();
        if (user == null) {
            screen.addToast("Error", "No user logged in.");
            return false;
        }
        PlantType type;
        try {
            type = PlantType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            screen.addToast("Error", "Invalid plant type.");
            return false;
        }
        if (type == PlantType.IMITATER) {
            screen.addToast("Error", "Please specify target plant for Imitator" +
                " (e.g., add plant -t imitator peashooter).");
            return false;
        }
        if (!user.getUnlockedPlantsLevels().containsKey(type)) {
            screen.addToast("Error", "Plant is locked.");
            return false;
        }
        if (selectedPlants.size() >= maxSlots) {
            screen.addToast("Error", "Selection is full (max " + maxSlots + " plants).");
            return false;
        }
        boolean gameHasThisCard = false;
        for (PlantCard card : App.getCurrentGame().getPlantLists()) {
            if (card.getType() == type) {
                gameHasThisCard = true;
                break;
            }
        }
        if (gameHasThisCard) {
            screen.addToast("Error", "This plant is locked for this level!");
            return false;
        }
        if (selectedPlants.contains(type)) {
            screen.addToast("Error", "Plant already selected.");
            return false;
        }
        if (type.family == PlantFamily.SUN_PRODUCER &&
            App.getCurrentGame().getLevelSetup() instanceof PlantWhatYouGetLevelSetup) {
            screen.addToast("Error", "You cant choose sun producer plant in this level.");
            return false;
        }
        selectedPlants.add(type);
        screen.addToast("Added!", "Plant " + type.name() + " added to selection.");
        return true;
    }
    public boolean addPlant(String typeName, String targetTypeName, MenuScreen screen) {
        User user = App.getCurrentUser();
        if (user == null) {
            screen.addToast("Error", "No user logged in.");
            return false;
        }

        PlantType type;
        PlantType targetType;
        try {
            type = PlantType.valueOf(typeName.toUpperCase());
            targetType = PlantType.valueOf(targetTypeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            screen.addToast("Error", "Invalid plant type.");
            return false;
        }

        if (type != PlantType.IMITATER) {
            screen.addToast("Error", "Target plant can only be specified for IMITATER.");
            return false;
        }

        if (targetType == PlantType.IMITATER) {
            screen.addToast("Error", "Imitator cannot imitate itself!");
            return false;
        }

        if (!user.getUnlockedPlantsLevels().containsKey(targetType)) {
            screen.addToast("Error", "Target plant is locked.");
            return false;
        }

        if (selectedPlants.size() >= maxSlots) {
            screen.addToast("Error", "Selection is full (max " + maxSlots + " plants).");
            return false;
        }

        if (selectedPlants.contains(type)) {
            screen.addToast("Error", "Imitator is already selected.");
            return false;
        }

        selectedPlants.add(type);
        this.imitatorTarget = targetType;

        screen.addToast("Added!", "Plant IMITATER added to selection as " + targetType.name() + ".");
        return true;
    }

    public boolean removePlant(String typeName, MenuScreen screen) {
        PlantType type;
        try {
            type = PlantType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            screen.addToast("Error", "Invalid plant type.");
            return false;
        }

        boolean gameHasThisCard = false;
        for (PlantCard card : App.getCurrentGame().getPlantLists()) {
            if (card.getType() == type) {
                gameHasThisCard = true;
                break;
            }
        }

        if (!selectedPlants.contains(type)) {
            if (gameHasThisCard) {
                screen.addToast("Error", "This plant can't be removed!");
            } else {
                screen.addToast("Error", "Plant is not selected.");
            }
            return false;
        }

        selectedPlants.remove(type);

        if (type == PlantType.IMITATER) {
            this.imitatorTarget = null;
        }

        screen.addToast("Removed!", "Plant " + type.name() + " removed from selection.");
        return true;
    }

    public boolean boostPlant(String typeName, MenuScreen screen) {
        User user = App.getCurrentUser();

        PlantType type;
        try {
            type = PlantType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            screen.addToast("Error", "Invalid plant type.");
            return false;
        }

        if (!user.getUnlockedPlantsLevels().containsKey(type)) {
            screen.addToast("Error", "Plant is locked.");
            return false;
        }

        if (user.hasBoost(type)) {
            screen.addToast("Error", "Plant is already boosted.");
            return false;
        }

        if (!user.spendGems(15)) {
            screen.addToast("Error", "Not enough gems (need 15).");
            return false;
        }

        user.addBoost(type);
        screen.addToast("Boosted!", "Plant " + type.name() + " boosted for this level (15 gems spent).");
        return true;
    }

    public boolean startGame(MenuScreen screen) {
        User user = App.getCurrentUser();
        if (user == null) {
            screen.addToast("Error", "No user logged in.");
            return false;
        }

        if (selectedPlants.size() < maxSlots) {
            screen.addToast("Error", "Please select " + (maxSlots - selectedPlants.size()) + " more plants.");
            return false;
        }

        for (PlantType type : selectedPlants) {
            if (type == PlantType.IMITATER) {
                int targetLevel = user.getUnlockedPlantsLevels().getOrDefault(imitatorTarget, 1);
                int imitatorLevel = user.getUnlockedPlantsLevels().getOrDefault(PlantType.IMITATER, 1);

                App.getCurrentGame().getPlantLists().add(
                    PlantCardFactory.createImitatorCard(imitatorTarget, targetLevel, imitatorLevel)
                );
            } else {
                int plantLevel = user.getUnlockedPlantsLevels().getOrDefault(type, 1);
                App.getCurrentGame().getPlantLists().add(
                    PlantCardFactory.createCard(type, plantLevel)
                );
            }
        }

        screen.addToast("Starting!", "Starting game with selected plants...");
        return true;
    }

    @Override
    public void exitMenu() {
    }

}
