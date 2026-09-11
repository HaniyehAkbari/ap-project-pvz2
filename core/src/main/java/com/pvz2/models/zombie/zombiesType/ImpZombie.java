package com.pvz2.models.zombie.zombiesType;

import com.pvz2.models.enums.Zombies;
import com.pvz2.models.zombie.Zombie;

public class ImpZombie extends Zombie {
    private boolean isDragon;

    public ImpZombie(int health, double speed, int damage, boolean isDragon) {
        super(Zombies.IMP, health, speed, damage);
        this.isDragon = isDragon;
    }

    public void throwImp(float targetX, float targetY) {
        this.x = targetX;
        this.y = targetY;
    }

    @Override
    public void takeDamage(int amount, String damageType) {
        System.out.println("🔍 ImpZombie.takeDamage: amount=" + amount + ", damageType='" + damageType + "'");
        if (isDead) return;
        if (isDragon && damageType != null && damageType.toUpperCase().contains("FIRE")) {
            System.out.println("🛡️ ImpDragon ignored " + amount + " fire damage.");
            return;
        }
        super.takeDamage(amount, damageType);
    }


}
