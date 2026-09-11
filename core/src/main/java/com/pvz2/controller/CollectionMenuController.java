package com.pvz2.controller;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.plant.card.PlantCardFactory;
import com.pvz2.models.plant.factory.PlantFactory;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.ZombieFactory;
import com.pvz2.models.zombie.zombiesType.ArmoredZombie;
import com.pvz2.view.screen.CollectionMenuScreen;
import com.pvz2.view.screen.MenuScreen;
import com.pvz2.view.screen.PlantsCollectionMenuScreen;


public class CollectionMenuController implements MenuController {
    MenuScreen lastScreen;
    CollectionMenuScreen screen;
    private PlantsCollectionMenuScreen plantsCollectionMenuScreen;

    public CollectionMenuController(MenuScreen lastScreen, CollectionMenuScreen screen) {
        this.lastScreen = lastScreen;
        this.screen = screen;
    }

    @Override
    public void changeMenu() {

    }

    @Override
    public void exitMenu() {
        screen.fadeAndSwitchScreen(lastScreen);
    }



    public static int[] showPlant(PlantType type) {
        if (type == PlantType.IMITATER){
            return new int[]{0,0,0,0};
        }
        Plant plant = PlantFactory.createPlant(type, 0, 0, null);
        PlantCard card = PlantCardFactory.createCard(type,
            App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(type,1));
        int[] result = new int[4];
        result[0] = card.getSunCost();
        result[1] = (int) card.getMaxCooldownTicks();
        result[2] = plant.getHealth();
        result[3] = plant.getDamage();
        return result;
    }

    public float[] showZombie(String name) {
        String zombieName = App.getZombieId(name);
        Zombie zombie;
        try {
            zombie = new ZombieFactory().createZombie(zombieName);
        } catch (Exception e) {
            return new float[]{0,0,0};
        }
        float[] result = new float[3];
        result[0] = zombie.getHealth();
        result[1] = (float) zombie.getSpeed();
        result[2] = zombie.getDamage();

        if (zombie instanceof ArmoredZombie armoredZombie){
            int finalHealth = (int) result[0] + armoredZombie.getArmorHealth();
            result[0] = finalHealth;
        }

        return result;
    }

    public boolean upgradePlant(PlantType type){
        return upgradePlant(type, plantsCollectionMenuScreen);
    }

    public static boolean upgradePlant(PlantType type, MenuScreen menuScreen) {
        int plantLevel = App.getCurrentUser().getUnlockedPlantsLevels().get(type);
        if (plantLevel == 4) {
            if (menuScreen != null){
                menuScreen.addToast("Error", "This plant has max level.");
            }
            return false;
        }
        int currentSeedPacket = App.getCurrentUser().getSeedPacketsCount(type);
        int neededSeedPacket = plantLevel * 10;
        int currentCoin = App.getCurrentUser().getCoins();
        int neededCoin = plantLevel * 100;
        if (currentSeedPacket < neededSeedPacket) {
            if (menuScreen != null){
                menuScreen.addToast("Error", "You need " + neededSeedPacket +
                    " seed packets.");
            }
            return false;
        }
        if (currentCoin < neededCoin) {
            if (menuScreen != null){
                menuScreen.addToast("Error", "You need " + neededCoin + " coins.");
            }
            return false;
        }
        App.getCurrentUser().getUnlockedPlantsLevels().put(type, plantLevel + 1);
        App.getCurrentUser().getSeedPackets().put(type, currentSeedPacket - neededSeedPacket);
        App.getCurrentUser().spendCoins(neededCoin);
        if (menuScreen != null){
            menuScreen.addToast("plant " + type + " upgraded.",
                "new level: " + plantLevel + 1);
        }
        return true;
    }

    public boolean purchasePlant(PlantType type) {
        if (App.getCurrentUser().getCoins() < 2000) {
            if (plantsCollectionMenuScreen != null){
                plantsCollectionMenuScreen.addToast("Error", "you need 2000 coins.");
            }
            return false;
        }
        App.getCurrentUser().unlockPlant(type);
        if (plantsCollectionMenuScreen != null){
            plantsCollectionMenuScreen.addToast("Purchased successfully",
                "Now you have " + type.name() + ".");
        }
        App.getCurrentUser().notifyPlantUnlock(type.name());
        App.getCurrentUser().spendCoins(2000);
        return true;
    }

    public void setPlantsCollectionMenuScreen(PlantsCollectionMenuScreen plantsCollectionMenuScreen) {
        this.plantsCollectionMenuScreen = plantsCollectionMenuScreen;
    }
}
