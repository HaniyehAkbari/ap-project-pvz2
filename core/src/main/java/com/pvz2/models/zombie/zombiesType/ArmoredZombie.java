package com.pvz2.models.zombie.zombiesType;

import com.pvz2.models.enums.Zombies;
import com.pvz2.models.zombie.Zombie;

public class ArmoredZombie extends Zombie {
    private int armorHealth;
    private boolean isMagnetic;

    public ArmoredZombie(int health, double speed, int damage, int armorHealth, boolean isMagnetic) {
        super(Zombies.ARMORED, health, speed, damage);
        this.armorHealth = armorHealth;
        this.isMagnetic = isMagnetic;
    }

    @Override
    public void takeDamage(int amount, String damageType) {
        if (isDead) return;

        if (armorHealth > 0) {
            triggerDamageFlash();

            int excess = amount - armorHealth;
            if (excess > 0) {
                armorHealth = 0;
                super.takeDamage(excess, damageType);
            } else {
                armorHealth -= amount;
            }
        } else {
            super.takeDamage(amount, damageType);
        }
    }

    public void stripArmor() {
        if (isMagnetic) this.armorHealth = 0;
    }

    public int getArmorHealth() {
        return armorHealth;
    }
}
