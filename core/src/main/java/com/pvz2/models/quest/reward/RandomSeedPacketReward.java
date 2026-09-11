package com.pvz2.models.quest.reward;

import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSeedPacketReward implements Reward {
    private int quantity;

    public RandomSeedPacketReward(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public void apply(User user) {
        List<PlantType> unlocked = new ArrayList<>(user.getUnlockedPlantsLevels().keySet());
        if (unlocked.isEmpty()) {
            user.addSeedPackets(PlantType.PEASHOOTER, quantity);
        } else {
            PlantType randomPlant = unlocked.get(new Random().nextInt(unlocked.size()));
            user.addSeedPackets(randomPlant, quantity);
        }
    }

    public int getQuantity() {
        return quantity;
    }
}
