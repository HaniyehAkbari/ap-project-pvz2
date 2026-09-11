package com.pvz2.models.shop;

import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopList {
    private List<ShopItem> permanentItems;
    private DailyOffer dailyOffer;

    public ShopList() {
        permanentItems = new ArrayList<>();
        permanentItems.add(new ShopItem("1", "Unlock Pot", 2000, 0, 20, true));
        permanentItems.add(new ShopItem("2", "Plant Food", 0, 3, 3, true));
        permanentItems.add(new ShopItem("3", "Random Seed Packet", 1000, 0, -1, true));
        permanentItems.add(new ShopItem("4", "Specific Seed Packet", 0, 5, -1, true));
        permanentItems.add(new ShopItem("5", "Currency Exchange", 0, 5, -1, true));

        long todaySeed = LocalDate.now().toEpochDay();
        PlantType[] types = PlantType.values();

        int dailyIndex = new Random(todaySeed).nextInt(types.length);
        PlantType dailyType = types[dailyIndex];

        dailyOffer = new DailyOffer(dailyType, 1600);
    }

    private String checkDailyItem(int count, User user) {
        if (user.hasPurchasedDailyOfferToday()) {
            return "Error: Daily offer already purchased today.";
        }
        if (count > 1) return "Error: Can only buy 1 daily offer.";
        if (!dailyOffer.isAffordable(user.getCoins(), user.getGems()))
            return "Error: Not enough money for daily offer!";

        user.spendCoins(dailyOffer.getCoinCost());
        user.addSeedPackets(dailyOffer.getPlantType(), 10);
        user.markDailyOfferPurchased();

        return dailyOffer.getPlantType() + " seeds unlocked permanently!";
    }

    private String buyRandomSeedPacket(User user, int count, int totalCoinCost) {
        List<PlantType> unlockedPlants = new ArrayList<>(user.getUnlockedPlantsLevels().keySet());
        if (unlockedPlants.isEmpty()) {
            return "Error: You have no unlocked plants to buy seeds for.";
        }
        user.spendCoins(totalCoinCost);
        PlantType randomPlant = unlockedPlants.get(new Random().nextInt(unlockedPlants.size()));
        int seedsToGive = 5 * count;
        user.addSeedPackets(randomPlant, seedsToGive);
        return count + " Random Seed Packets bought successfully! Received " +
                seedsToGive + " seeds for " + randomPlant.name() + ".";
    }

    private String buySpecificSeedPacket(PlantType plantType, User user, int count, int totalGemCost) {
        if (plantType == null) {
            return "Error: You must specify a plant type using -t.";
        }
        if (!user.getUnlockedPlantsLevels().containsKey(plantType)) {
            return "Error: Plant " + plantType.name() + " is not unlocked yet.";
        }
        user.spendGems(totalGemCost);
        int seedsToGive = 10 * count;
        user.addSeedPackets(plantType, seedsToGive);
        return count + " Specific Seed Packets for " + plantType.name() +
                " bought successfully! Received " + seedsToGive + " seeds.";
    }

    public String buy(String itemId, PlantType plantType, int count) {
        User user = App.getCurrentUser();
        if (user == null) return "Error: No user logged in.";
        if (count <= 0) return "Error: Count must be greater than zero.";
        if (dailyOffer != null && dailyOffer.getId().equals(itemId))
            return checkDailyItem(count, user);
        ShopItem selectedItem = null;
        for (ShopItem item : permanentItems) {
            if (item.getId().equals(itemId)) {
                selectedItem = item;
                break;
            }
        }
        if (selectedItem == null) return "Error: Item not found.";
        int totalCoinCost = selectedItem.getCoinCost() * count;
        int totalGemCost = selectedItem.getDiamondCost() * count;
        if (user.getCoins() < totalCoinCost || user.getGems() < totalGemCost) {
            return "Error: Not enough currency. Need " + totalCoinCost + " coins and " + totalGemCost + " gems.";
        }
        String itemName = selectedItem.getName();
        if (itemName.equalsIgnoreCase("Unlock Pot")) {
            String result = "";
            for (int i = 0; i < count; i++) {
                result = user.getGreenhouse().unlockFirstLockedPot();
                if (!result.contains("successfully")) {
                    break;
                }
                user.spendCoins(selectedItem.getCoinCost());
            }
            return count + " pots processed. Status: " + result;
        } else if (itemName.equalsIgnoreCase("Plant Food")) {
            if (user.getPlantFoods() + count > 3) {
                return "Error: Maximum capacity for Plant Food is 3. You currently have " + user.getPlantFoods() + ".";
            }
            user.spendGems(totalGemCost);
            user.addPlantFood(count);
            return count + " Plant Food bought successfully! You now have " + user.getPlantFoods() + " Plant Foods.";
        } else if (itemName.equalsIgnoreCase("Random Seed Packet")) {
            return buyRandomSeedPacket(user, count, totalCoinCost);
        } else if (itemName.equalsIgnoreCase("Specific Seed Packet")) {
            return buySpecificSeedPacket(plantType, user, count, totalGemCost);
        } else if (itemName.equalsIgnoreCase("Currency Exchange")) {
            user.spendGems(totalGemCost);
            user.addCoins(500 * count);
            return "Exchanged " + totalGemCost + " gems for " + (500 * count) + " coins.";
        }
        return "Error: Custom logic needed for this item.";
    }

    public DailyOffer getDailyOffer() {
        return dailyOffer;
    }

    public List<ShopItem> getPermanentItems() {
        return permanentItems;
    }
}
