package com.pvz2.models.network;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pvz2.models.Damageable;
import com.pvz2.models.core.LocalDateAdapter;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.components.shooterPlantFoodBehaviors.PlantFoodBehavior;
import com.pvz2.models.projectile.hitStrategies.HitStrategy;
import com.pvz2.models.projectile.movementStrategies.MovementStrategy;
import com.pvz2.models.projectile.strikeStrategies.CheckStrike;
import com.pvz2.models.world.cellTerrains.CellTerrain;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.world.loseCondition.LoseCondition;
import com.pvz2.models.world.mechanics.Mechanic;
import com.pvz2.models.world.winCondition.WinCondition;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.state.ZombieState;

import java.time.LocalDate;

public class NetworkGson {
    public static final Gson INSTANCE = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .registerTypeAdapter(Zombie.class, new PolymorphicTypeAdapter<Zombie>())
        .registerTypeAdapter(ZombieState.class, new PolymorphicTypeAdapter<ZombieState>())
        .registerTypeAdapter(GameComponent.class, new PolymorphicTypeAdapter<GameComponent>())
        .registerTypeAdapter(CellTerrain.class, new PolymorphicTypeAdapter<CellTerrain>())
        .registerTypeAdapter(LevelSetup.class, new PolymorphicTypeAdapter<LevelSetup>())
        .registerTypeAdapter(WinCondition.class, new PolymorphicTypeAdapter<WinCondition>())
        .registerTypeAdapter(LoseCondition.class, new PolymorphicTypeAdapter<LoseCondition>())
        .registerTypeAdapter(Mechanic.class, new PolymorphicTypeAdapter<Mechanic>())
        .registerTypeAdapter(PlantFoodBehavior.class, new PolymorphicTypeAdapter<PlantFoodBehavior>())
        .registerTypeAdapter(Damageable.class, new PolymorphicTypeAdapter<Damageable>())
        .registerTypeAdapter(CheckStrike.class, new PolymorphicTypeAdapter<CheckStrike>())
        .registerTypeAdapter(HitStrategy.class, new PolymorphicTypeAdapter<HitStrategy>())
        .registerTypeAdapter(MovementStrategy.class, new PolymorphicTypeAdapter<MovementStrategy>())

        .setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return isUnwireable(f.getDeclaredClass());
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return isUnwireable(clazz);
            }

            private boolean isUnwireable(Class<?> clazz) {
                if (clazz == java.util.Random.class) return true;
                Package pkg = clazz.getPackage();
                return pkg != null && pkg.getName().equals("java.util.function");
            }
        })
        .create();
}
