package com.pvz2.models.mupoint;

import com.pvz2.controller.GameMenuController;
import com.pvz2.view.screen.GameScreen;

import java.util.ArrayList;
import java.util.List;

public class MupointManager {
    private int totalMupoints = 0;
    private List<ScoreStrategy> strategies = new ArrayList<>();

    public MupointManager() {
        strategies.add(new FastKillStrategy());
        strategies.add(new SunMilestoneStrategy());
        strategies.add(new CleanKillStrategy());
        strategies.add(new ToughZombieStrategy());
        strategies.add(new ComboKillStrategy());
    }

    public void onZombieDeath(KillEvent event) {
        int pointsGained = 0;
        for (ScoreStrategy strategy : strategies) {
            int pts = strategy.calculatePoints(event);
            if (pts > 0) {
                String strategyName = strategy.getClass().getSimpleName();
                GameScreen.announce(strategyName + ": +" + pts + " pts!");

                GameMenuController.updateState(strategyName + ": +" + pts + " point");
                pointsGained += pts;
            }
        }
        applyPoints(pointsGained);
    }

    public void checkSunMilestones() {
        for (ScoreStrategy strategy : strategies) {
            if (strategy instanceof SunMilestoneStrategy) {
                int pts = strategy.calculatePoints(null);
                if (pts > 0) {
                    GameScreen.announce("Sun Milestone: +" + pts + " pts!");
                    applyPoints(pts);
                }
            }
        }
    }

    private void applyPoints(int pointsGained) {
        if (pointsGained <= 0) return;

        totalMupoints += pointsGained;


        GameMenuController.updateState("Current Mupoints: " + totalMupoints);
    }

    public int getTotalMupoints() {
        return totalMupoints;
    }
}
