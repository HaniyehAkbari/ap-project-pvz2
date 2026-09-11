package com.pvz2.models.quest.reward;

import com.pvz2.models.core.News;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.NewsType;
import com.pvz2.models.enums.PlantType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomUnlockReward implements Reward {
    @Override
    public void apply(User user) {
        List<PlantType> allPlants = Arrays.stream(PlantType.values())
                .filter(pt -> pt != PlantType.MARIGOLD)
                .collect(Collectors.toList());

        List<PlantType> unlocked = new ArrayList<>(user.getUnlockedPlantsLevels().keySet());

        List<PlantType> locked = allPlants.stream()
                .filter(pt -> !unlocked.contains(pt))
                .collect(Collectors.toList());

        if (locked.isEmpty()) {
            new CurrencyReward(50, 0).apply(user);
            user.addNews(new News(
                    "All plants unlocked!",
                    "Received 50 coins as bonus reward.",
                    NewsType.PLANT_UNLOCKED
            ));
        } else {
            PlantType toUnlock = locked.get(new Random().nextInt(locked.size()));
            user.unlockPlant(toUnlock);
            user.notifyPlantUnlock(toUnlock.name());
        }
    }
}
