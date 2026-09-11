package com.pvz2.models.plant.components;

import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.zombie.Zombie;

public class ArmorComponent implements GameComponent {
    private int armorHp;
    private int initHp;

    public ArmorComponent(int armorHp) {
        this.armorHp = armorHp;
        initHp = armorHp;
    }

    @Override
    public void update(Plant owner, float delta) {
    }

    @Override
    public int onTakeDamage(Plant owner, int damageAmount, Zombie attacker) {
        if (armorHp <= 0) {
            return damageAmount;
        }

        if (damageAmount <= armorHp) {
            armorHp -= damageAmount;
            return 0;
        } else {
            int remainingDamage = damageAmount - armorHp;
            armorHp = 0;
            onDestroy(owner);
            return remainingDamage;
        }
    }

    public int getArmorHp() {
        return armorHp;
    }

    public int getInitHp() {
        return initHp;
    }

    @Override
    public void activatePlantFood(Plant owner) {
        armorHp = initHp;
    }

    protected void onDestroy(Plant owner) {

    }

    public void setArmorHp(int armorHp) {
        this.armorHp = armorHp;
    }
}
