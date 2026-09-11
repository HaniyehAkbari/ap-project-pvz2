package com.pvz2.models.plant.components;

import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.AnimationDurations;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;

import java.util.function.Consumer;

public class MintComponent implements GameComponent {
    private static final String INTRO_CLIP = "intro";
    private static final float DEFAULT_INTRO_DURATION = 1f;

    private final PlantType plantType;
    private final Consumer<Plant> mintAction;
    private final float totalTime;

    private boolean executed = false;
    private boolean introStarted = false;
    private float currentTimer = 0f;

    private float introDuration = -1f;

    public MintComponent(PlantType plantType, Consumer<Plant> mintAction, float totalTime) {
        this.plantType = plantType;
        this.mintAction = mintAction;
        this.totalTime = totalTime;
    }

    private void ensureIntroDurationLoaded(Plant owner) {
        if (introDuration >= 0f) return;
        introDuration = AnimationDurations.getDuration(owner.getType(), INTRO_CLIP, DEFAULT_INTRO_DURATION);
        System.out.println(introDuration);
    }

    @Override
    public void update(Plant owner, float delta) {
        currentTimer += delta;
        if (executed){
            if (currentTimer >= totalTime) {
                owner.die();
            }
            return;
        }
        ensureIntroDurationLoaded(owner);

        if (!introStarted) {
            introStarted = true;
            owner.setState(Plant.State.INTRO);
        }

        if (owner.getState() == Plant.State.INTRO && currentTimer >= introDuration) {
            owner.setState(Plant.State.IDLE);
            executed = true;
            if (mintAction != null) {
                mintAction.accept(owner);
            }
        }


    }

    @Override
    public void activatePlantFood(Plant owner) {
    }

    public PlantType getPlantType() {
        return plantType;
    }
}
