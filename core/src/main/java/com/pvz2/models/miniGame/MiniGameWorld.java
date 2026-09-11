package com.pvz2.models.miniGame;

import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.world.loseCondition.LoseCondition;
import com.pvz2.models.world.mechanics.Mechanic;
import com.pvz2.models.world.winCondition.WinCondition;

import java.util.ArrayList;
import java.util.function.Supplier;

public class MiniGameWorld extends GameWorld {
    private Supplier<GameWorld> builder;

    public MiniGameWorld(LevelSetup levelSetup, ArrayList<LoseCondition> loseConditions,
                         WinCondition winCondition, ArrayList<Mechanic> mechanics, Supplier<GameWorld> builder) {
        super(levelSetup, loseConditions, winCondition, mechanics);
        this.builder = builder;
    }

    @Override
    protected void applyChapterRules() {

    }

    public Supplier<GameWorld> getBuilder() {
        return builder;
    }
}
