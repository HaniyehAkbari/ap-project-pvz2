package com.pvz2.models.zombie.zombiesType;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.state.EatingState;
import com.pvz2.models.zombie.state.WalkingState;
import com.pvz2.models.zombie.state.ZombieState;
import com.pvz2.view.screen.GameScreen;

import java.util.ArrayList;
import java.util.Set;

public class ElementalZombie extends Zombie {
    private static final Set<String> FIRE_TYPES = Set.of(
        "FIRE_PEA", "PEPPER", "SPECIAL_PEPPER", "FIRE_PEASHOOTER", "FIRE"
    );
    private static final Set<String> ICE_TYPES = Set.of(
        "ICE_PEA", "SNOW_PEA", "ICE_MELON", "SPECIAL_ICE_MELON", "ICE_SHROOM", "ICE", "FROST"
    );

    private boolean isExplorer;
    private boolean isIgnited;
    private float fuseTimer;
    private boolean hasExploded;

    public enum FlightState { NONE, BLASTOFF, FLY, LAND }
    private FlightState flightState = FlightState.NONE;
    private float stateTime = 0f;

    public ElementalZombie(int health, double speed, int damage, boolean isExplorer) {
        super(Zombies.ELEMENTAL, health, speed, damage);
        this.isExplorer = isExplorer;
        this.isIgnited = true;
        this.fuseTimer = 10.0f;
        this.hasExploded = false;
    }

    @Override
    public void setState(ZombieState state) {
        if (isExplorer && isIgnited && state instanceof EatingState) {
            return;
        }
        super.setState(state);
    }

    @Override
    public void update(float delta) {
        if (isDead) return;
        if (!isExplorer && isIgnited && !hasExploded) {
            fuseTimer -= delta;
            if (fuseTimer <= 0) {
                startBlastoff();
            }
        }
        if (flightState != FlightState.NONE) {
            handleFlight(delta);
            return;
        }
        super.update(delta);
        if (isExplorer) {
            GameWorld game = App.getCurrentGame();
            if (game != null) {
                for (Plant plant : new ArrayList<>(game.getActivePlants())) {
                    if (plant == null || plant.isDead()) continue;
                    float plantCenterY = (plant.getCell() != null)
                        ? App.getCellCenterY(plant.getCell().getRow())
                        : plant.getY();
                    boolean sameRow = Math.abs(this.y - plantCenterY) < App.getCellHeight() * 0.5f;
                    if (sameRow) {
                        int plantRow = (plant.getCell() != null) ? plant.getCell().getRow() : 0;
                        float plantWorldX = plant.getX();
                        float dist = (this.speed <= 0) ? (this.x - plantWorldX) : (plantWorldX - this.x);
                        if (dist >= 0 && dist < App.getCellWidth()) {
                            String typeName = plant.getType().name().toUpperCase();
                            boolean isIcePlant = typeName.contains("ICE") || typeName.contains("SNOW") ||
                                ICE_TYPES.contains(typeName);
                            boolean isFirePlant = typeName.contains("FIRE") || typeName.contains("PEPPER") ||
                                FIRE_TYPES.contains(typeName);
                            if (isIcePlant) {
                                extinguish();
                            } else if (isFirePlant) {
                                ignite();
                            }
                            if (isIgnited) {
                                plant.setBurnt(true);
                                GameScreen.spawnPlantBurnEffect(plantWorldX, plantCenterY);
                                plant.die();
                                System.out.println("🔥 Explorer burned plant at row " + plantRow);
                                GameMenuController.updateState("Explorer burned plant");
                            }
                        }
                    }
                }
            }
        }
    }

    private void startBlastoff() {
        this.hasExploded = true;
        this.isIgnited = false;
        this.flightState = FlightState.BLASTOFF;
        this.stateTime = 0f;
        this.speed = 0;
    }

    private void handleFlight(float delta) {
        stateTime += delta;
        float targetX = App.getFirstCellX();

        switch (flightState) {
            case BLASTOFF:
                if (stateTime >= 1.0f) flightState = FlightState.FLY;
                break;
            case FLY:
                this.x -= 400 * delta;
                if (this.x <= targetX) {
                    this.x = targetX;
                    flightState = FlightState.LAND;
                    stateTime = 0f;
                }
                break;
            case LAND:
                if (stateTime >= 1.0f) {
                    flightState = FlightState.NONE;
                    this.speed = -Math.abs(this.originalSpeed);
                }
                break;
        }
    }

    @Override
    public String getAnimationClip() {
        if (flightState != FlightState.NONE) {
            return switch (flightState) {
                case BLASTOFF -> "blastoff";
                case FLY -> "fly";
                case LAND -> "land";
                default -> super.getAnimationClip();
            };
        }
        return super.getAnimationClip();
    }

    public void extinguish() {
        if (!isIgnited) return;
        this.isIgnited = false;
        GameMenuController.updateState(isExplorer ?
            "Explorer's torch extinguished." : "Prospector's dynamite extinguished.");
    }

    public void ignite() {
        if (!isExplorer || isIgnited) return;
        this.isIgnited = true;
        setState(new WalkingState());
        GameMenuController.updateState("Explorer's torch ignited.");
    }

    @Override
    public void takeDamage(int amount, String damageType) {
        if (isDead) return;
        if (damageType != null) {
            String upper = damageType.toUpperCase();
            if (ICE_TYPES.contains(upper) || upper.contains("ICE") ||
                upper.contains("SNOW") || upper.contains("FROST")) {
                extinguish();
            } else if (FIRE_TYPES.contains(upper) || upper.contains("FIRE") || upper.contains("PEPPER")) {
                if (isExplorer) {
                    ignite();
                }
            }
        }
        super.takeDamage(amount, damageType);
    }

    @Override
    public void applySlow(float delta, double factor, boolean canWorkInFrostbite) {
        super.applySlow(delta, factor, canWorkInFrostbite);
        if (isExplorer && isIgnited) {
            extinguish();
        }
    }
}
