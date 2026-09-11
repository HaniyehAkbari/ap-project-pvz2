package com.pvz2.models.plant.components;

import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.AnimationDurations;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;

public class ImitatorIntroComponent implements GameComponent {
    private static final float DEFAULT_IDLE_DURATION = 0.6f;
    private static final float DEFAULT_ATTACK_DURATION = 0.6f;

    private final float idleDuration;
    private final float attackDuration;
    private float timer = 0f;
    private boolean started = false;
    private boolean finished = false;

    public ImitatorIntroComponent() {
        this.idleDuration = AnimationDurations.getDuration(PlantType.IMITATER, "idle", DEFAULT_IDLE_DURATION);
        this.attackDuration = AnimationDurations.getDuration(PlantType.IMITATER, "attack", DEFAULT_ATTACK_DURATION);
    }

    @Override
    public void update(Plant owner, float delta) {
        if (finished) return;

        if (!started) {
            started = true;
            owner.setState(Plant.State.IMITATE_IDLE);
        }

        timer += delta;

        if (owner.getState() == Plant.State.IMITATE_IDLE && timer >= idleDuration) {
            owner.setState(Plant.State.IMITATE_ATTACK);
        }

        if (owner.getState() == Plant.State.IMITATE_ATTACK && timer >= idleDuration + attackDuration) {
            finished = true;
            owner.setImitate(false);
            owner.setState(Plant.State.IDLE);
        }
    }

    @Override
    public void activatePlantFood(Plant owner) { }
}
