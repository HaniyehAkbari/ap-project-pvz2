package com.pvz2.models.plant.factory;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantLayer;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.*;
import com.pvz2.models.plant.components.explosionRanges.CircularRange;
import com.pvz2.models.plant.components.explosiveBehaviors.AreaDamageBehavior;
import com.pvz2.models.plant.components.explosiveTriggers.InstantTrigger;
import com.pvz2.models.plant.components.moveZombieStrategy.AttractStrategy;
import com.pvz2.models.plant.components.moveZombieStrategy.EjectStrategy;
import com.pvz2.models.world.Sun;
import com.pvz2.models.zombie.Zombie;

import java.util.Map;
import java.util.function.Supplier;

public class WallNutFactory {
    static void register(Map<PlantType, Supplier<Plant>> registry) {
        registry.put(PlantType.WALL_NUT, WallNutFactory::buildWallNut);
        registry.put(PlantType.TALL_NUT, WallNutFactory::buildTallNut);
        registry.put(PlantType.ENDURIAN, WallNutFactory::buildEndurian);
        registry.put(PlantType.GARLIC, WallNutFactory::buildGarlic);
        registry.put(PlantType.SWEET_POTATO, WallNutFactory::buildSweetPotato);
        registry.put(PlantType.EXPLODE_O_NUT, WallNutFactory::buildExplodeONut);
        registry.put(PlantType.PUMPKIN, WallNutFactory::buildPumpkin);
        registry.put(PlantType.SUN_BEAN, WallNutFactory::buildSunBean);
    }

    private static Plant buildWallNut() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.WALL_NUT, 1);
        int health = 4000;
        if (level >= 2) health += 1000;
        if (level >= 4) health += 1500;
        Plant p = new Plant(PlantType.WALL_NUT, health, 0);
        int finalHealth = health;
        p.addComponent(new WallNutsComponent() {
            @Override
            public void activatePlantFood(Plant owner) {
                super.activatePlantFood(owner);
                ArmorComponent armor = p.getComponent(ArmorComponent.class);
                if (armor == null) {
                    p.addComponent(new ArmorComponent(4000));
                } else {
                    armor.setArmorHp(4000);
                }
            }
            @Override
            public void update(Plant owner, float delta) {
                super.update(owner, delta);
                if (owner.getHealth() < finalHealth / 4) owner.setState(Plant.State.DAMAGE3);
                else if (owner.getHealth() < finalHealth / 2) owner.setState(Plant.State.DAMAGE2);
                else if (owner.getHealth() < finalHealth * 3 / 4f) owner.setState(Plant.State.DAMAGE);
            }
        });
        return p;
    }

    private static Plant buildTallNut() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.TALL_NUT, 1);
        int health = 8000;
        if (level >= 2) health += 2000;
        if (level >= 4) health += 3000;
        int finalHealth = health;
        Plant p = new Plant(PlantType.TALL_NUT, health, 0);
        p.addComponent(new WallNutsComponent() {
            @Override
            public void activatePlantFood(Plant owner) {
                super.activatePlantFood(owner);
                ArmorComponent armor = p.getComponent(ArmorComponent.class);
                if (armor == null) {
                    p.addComponent(new ArmorComponent(8000));
                } else {
                    armor.setArmorHp(8000);
                }
            }
            @Override
            public void update(Plant owner, float delta) {
                super.update(owner, delta);
                if (owner.getHealth() < finalHealth / 3) owner.setState(Plant.State.DAMAGE2);
                else if (owner.getHealth() < finalHealth * 2 / 3f) owner.setState(Plant.State.DAMAGE);
            }
        });
        return p;
    }

    private static Plant buildEndurian() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.ENDURIAN, 1);
        int health = (level >= 3) ? 4000 : 3000;
        int damage = (level >= 2) ? 25 : 20;
        Plant p = new Plant(PlantType.ENDURIAN, health, damage);
        p.addComponent(new WallNutsComponent() {
            @Override
            public int onTakeDamage(Plant owner, int damageAmount, Zombie attacker) {
                if (attacker == null) return 0;
                attacker.takeDamage(owner.getDamage(), "NORMAL");
                return damageAmount;
            }
            @Override
            public void activatePlantFood(Plant owner) {
                super.activatePlantFood(owner);
                ArmorComponent armor = p.getComponent(ArmorComponent.class);
                if (armor == null) {
                    p.addComponent(new ArmorComponent(4000));
                } else {
                    armor.setArmorHp(4000);
                }
                if (owner.getDamage() != damage) return;
                owner.setDamage(owner.getDamage() + 5);
            }
            @Override
            public void update(Plant owner, float delta) {
                super.update(owner, delta);
                if (owner.getHealth() < health / 4) owner.setState(Plant.State.DAMAGE3);
                else if (owner.getHealth() < health / 2) owner.setState(Plant.State.DAMAGE2);
                else if (owner.getHealth() < health * 3 / 4f) owner.setState(Plant.State.DAMAGE);
            }
        });
        return p;
    }

    private static Plant buildGarlic() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.GARLIC, 1);
        int health = 300;
        if (level >= 2) health += 150;
        if (level >= 4) health += 250;
        int finalHealth = health;
        Plant p = new Plant(PlantType.GARLIC, health, 0);
        p.addComponent(new MoveZombieComponent(new EjectStrategy()));
        p.addComponent(new WallNutsComponent() {
            @Override
            public void activatePlantFood(Plant owner) {
                super.activatePlantFood(owner);
            }
            @Override
            public void update(Plant owner, float delta) {
                super.update(owner, delta);
                if (owner.getHealth() < finalHealth / 3) owner.setState(Plant.State.DAMAGE2);
                else if (owner.getHealth() < finalHealth * 2 / 3f) owner.setState(Plant.State.DAMAGE);
            }
        });
        return p;
    }

    private static Plant buildSweetPotato() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.SWEET_POTATO, 1);
        int health = 3000;
        if (level >= 2) health += 1000;
        if (level >= 4) health += 1500;
        int finalHealth = health;
        Plant p = new Plant(PlantType.SWEET_POTATO, health, 0);
        p.addComponent(new MoveZombieComponent(new AttractStrategy()));
        p.addComponent(new WallNutsComponent() {
            @Override
            public void activatePlantFood(Plant owner) {
                super.activatePlantFood(owner);
            }
            @Override
            public void update(Plant owner, float delta) {
                super.update(owner, delta);
                if (owner.getHealth() < finalHealth / 4) owner.setState(Plant.State.DAMAGE3);
                else if (owner.getHealth() < finalHealth / 2) owner.setState(Plant.State.DAMAGE2);
                else if (owner.getHealth() < finalHealth * 3 / 4f) owner.setState(Plant.State.DAMAGE);
            }
        });
        return p;
    }

    private static Plant buildExplodeONut() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.EXPLODE_O_NUT, 1);
        int health = (level >= 2) ? 5000 : 4000;
        int damage = (level >= 3) ? 2000 : 1800;
        Plant p = new Plant(PlantType.EXPLODE_O_NUT, health, damage);
        p.addComponent(new WallNutsComponent() {
            @Override
            public void activatePlantFood(Plant owner) {
                super.activatePlantFood(owner);
                ArmorComponent armor = p.getComponent(ArmorComponent.class);
                if (armor == null) {
                    p.addComponent(new ArmorComponent(4000) {
                        @Override
                        protected void onDestroy(Plant owner) {
                            ExplosivesComponent explosivesComponent = new ExplosivesComponent(InstantTrigger.INSTANCE,
                                    new AreaDamageBehavior(damage, new CircularRange(1)), 0, 0);
                            explosivesComponent.setPostTriggerDelay(0);
                            explosivesComponent.update(owner, App.getCurrentGame().getElapsedTime());
                            explosivesComponent.update(owner, App.getCurrentGame().getElapsedTime());
                        }
                        @Override
                        public void update(Plant owner, float delta) {
                            super.update(owner, delta);
                            if (owner.getHealth() < health / 4) owner.setState(Plant.State.DAMAGE3);
                            else if (owner.getHealth() < health / 2) owner.setState(Plant.State.DAMAGE2);
                            else if (owner.getHealth() < health * 3 / 4f) owner.setState(Plant.State.DAMAGE);
                        }
                    });
                } else {
                    armor.setArmorHp(4000);
                }
            }
            @Override
            public void onDeath(Plant owner, float delta) {
                ExplosivesComponent explosivesComponent = new ExplosivesComponent(InstantTrigger.INSTANCE,
                        new AreaDamageBehavior(damage, new CircularRange(1)), 0, 0);
                explosivesComponent.setPostTriggerDelay(0);
                explosivesComponent.update(owner, delta);
                explosivesComponent.update(owner, delta);
            }
        });
        return p;
    }

    private static Plant buildPumpkin() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.PUMPKIN, 1);
        int health = 4000;
        if (level >= 2) health += 1000;
        if (level >= 4) health += 1500;
        int finalHealth = health;
        Plant p = new Plant(PlantType.PUMPKIN, health, 0);
        p.addComponent(new WallNutsComponent() {
            @Override
            public void activatePlantFood(Plant owner) {
                super.activatePlantFood(owner);
                ArmorComponent armor = p.getComponent(ArmorComponent.class);
                if (armor == null) {
                    p.addComponent(new ArmorComponent(4000));
                } else {
                    armor.setArmorHp(4000);
                }
            }
            @Override
            public void update(Plant owner, float delta) {
                super.update(owner, delta);
                if (owner.getHealth() < finalHealth / 3) owner.setState(Plant.State.DAMAGE2);
                else if (owner.getHealth() < finalHealth * 2 / 3f) owner.setState(Plant.State.DAMAGE);
            }
        });
        p.addComponent(new PlacementBehaviorComponent(PlantLayer.SHIELD, false, 0, false));
        return p;
    }

    private static Plant buildSunBean() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.SUN_BEAN, 1);
        int health = (level >= 3) ? 1000 : 1150;
        int sunSize = (level >= 2) ? 10 : 5;
        Plant p = new Plant(PlantType.SUN_BEAN, health, 0);
        p.addComponent(new WallNutsComponent() {
            @Override
            public void activatePlantFood(Plant owner) {
                super.activatePlantFood(owner);
                ArmorComponent armor = p.getComponent(ArmorComponent.class);
                if (armor == null) {
                    p.addComponent(new ArmorComponent(1000));
                } else {
                    armor.setArmorHp(1000);
                }
            }

            @Override
            public int onTakeDamage(Plant owner, int damageAmount, Zombie attacker) {
                Sun sun = App.getCurrentGame().getSunsPool().acquire();
                sun.reset(owner.getX(), owner.getY(), sunSize, null);
                App.getCurrentGame().addSunToPlayer(sunSize);
                return damageAmount;
            }
        });
        return p;
    }


}
