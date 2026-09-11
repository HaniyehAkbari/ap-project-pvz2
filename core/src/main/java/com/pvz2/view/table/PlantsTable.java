package com.pvz2.view.table;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantFamily;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.plant.card.PlantCardFactory;

import java.util.HashMap;
import java.util.function.Consumer;

public class PlantsTable extends Table {

    public enum LockFilter { ALL, UNLOCKED_ONLY, LOCKED_ONLY }
    protected int column;
    private boolean upgradeBar;
    private static HashMap<PlantType, String> plantsMap;
    private static HashMap<PlantFamily, String> plantsFamilyMap;
    protected int cardWidth, cardHeight;
    protected Consumer<PlantCardView> cardClickMethod;

    private PlantFamily selectedFamily = null;
    private LockFilter lockFilter = LockFilter.ALL;
    private boolean upgradeableOnly = false;

    public PlantsTable(int column, int pad, boolean upgradeBar, int cardWidth,
                       int cardHeight, Consumer<PlantCardView> cardClickMethod) {
        this.column = column;
        this.cardClickMethod = cardClickMethod;
        this.defaults().pad(pad);
        this.upgradeBar = upgradeBar;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
    }

    public void applyFilters(PlantFamily family, LockFilter lockFilter, boolean upgradeableOnly) {
        this.selectedFamily = family;
        this.lockFilter = lockFilter;
        this.upgradeableOnly = upgradeableOnly;
        build();
    }

    private boolean passesFilter(User user, PlantType plantType) {
        int cardLevel = user.getUnlockedPlantsLevels().getOrDefault(plantType, 0);
        boolean isUnlocked = cardLevel > 0;

        if (selectedFamily != null && plantType.family != selectedFamily) {
            return false;
        }

        if (lockFilter == LockFilter.UNLOCKED_ONLY && !isUnlocked) return false;
        if (lockFilter == LockFilter.LOCKED_ONLY && isUnlocked) return false;

        if (upgradeableOnly) {
            if (user.getUnlockedPlantsLevels().getOrDefault(plantType, 1) == 4) return false;
            int seedPacketAmount = user.getSeedPacketsCount(plantType);
            int requiredSeeds = Math.max(1, cardLevel) * 10;
            if (seedPacketAmount < requiredSeeds) return false;
        }

        return true;
    }

    public void build(){
        this.clear();
        User user = App.getCurrentUser();
        if (user == null) return;
        int i = 1;
        for (PlantType plantType : PlantType.values()){
            if (plantType == PlantType.GIANT_WALLNUT || plantType == PlantType.MARIGOLD) continue;
            if (!passesFilter(user, plantType)) continue;
            Table cardTable = new Table();
            int cardLevel = user.getUnlockedPlantsLevels().getOrDefault(plantType, 0);
            PlantCard card = PlantCardFactory.createCard(plantType, Math.max(1, cardLevel));
            boolean lock = cardLevel == 0;
            PlantCardView plantCardView = new PlantCardView(!lock, user.hasBoost(plantType), lock
                , cardLevel, card.getSunCost(), plantType);
            cardTable.add(plantCardView).size(cardWidth, cardHeight);
            plantCardView.setClickMethod(cardClickMethod);

            if (upgradeBar && cardLevel < 4){
                Stack progress = createProgressStack(user, plantType, cardLevel, "default");
                cardTable.row();
                cardTable.add(progress);
            }
            this.add(cardTable).top();
            i++;
            if (i > column){
                i = 1;
                this.row();
            }
        }
    }

    public static Stack createProgressStack(User user, PlantType plantType, int level,
                                            String labelStyle) {
        Stack stack = new Stack();
        int seedPacketAmount = user.getSeedPacketsCount(plantType);
        String style = seedPacketAmount >= Math.max(1, level) * 10 ?
            "xp_green" : "xp_yellow";
        ProgressBar progressBar = new ProgressBar(0, Math.max(1, level) * 10, 1, false,
            App.getGameApp().skin, style);
        progressBar.setValue(Math.min(seedPacketAmount, Math.max(1, level) * 10));
        Label amount = new Label(seedPacketAmount + "/" + Math.max(1, level) * 10,
            App.getGameApp().skin, labelStyle);
        stack.add(progressBar);
        Table amountWrapper = new Table();
        amountWrapper.add(amount);
        stack.add(amountWrapper);
        return stack;
    }

    public static HashMap<PlantType, String> getPlantsMap(){
        if (plantsMap == null){
            plantsMap = new HashMap<>();
            for (PlantType type : PlantType.values()){
                if (type == PlantType.GIANT_WALLNUT) continue;
                String newName = type.name().toUpperCase().replaceAll("_", "");
                if (type == PlantType.CHERRY_BOMB) newName = "CHERRY_BOMB";
                String address = "IMAGE_UI_PACKETS_" + newName;
                plantsMap.put(type, address);
            }
        }
        return plantsMap;
    }

    public static HashMap<PlantFamily, String> getPlantsFamilyMap() {
        if (plantsFamilyMap == null){
            plantsFamilyMap = new HashMap<>();
            plantsFamilyMap.put(PlantFamily.SUN_PRODUCER, "IMAGE_UI_PACKETS_MINTFAM_SUN");
            plantsFamilyMap.put(PlantFamily.SHOOTER, "IMAGE_UI_PACKETS_MINTFAM_PEASHOOTER");
            plantsFamilyMap.put(PlantFamily.LOBBER, "IMAGE_UI_PACKETS_MINTFAM_LOBBER");
            plantsFamilyMap.put(PlantFamily.EXPLOSIVE, "IMAGE_UI_PACKETS_MINTFAM_EXPLOSIVE");
            plantsFamilyMap.put(PlantFamily.MELEE, "IMAGE_UI_PACKETS_MINTFAM_MELEE");
            plantsFamilyMap.put(PlantFamily.WALL_NUTS, "IMAGE_UI_PACKETS_MINTFAM_DEFENSE");
            plantsFamilyMap.put(PlantFamily.STRIKE_THROUGH, "IMAGE_UI_PACKETS_MINTFAM_SHARP");
            plantsFamilyMap.put(PlantFamily.MODIFIER, "IMAGE_UI_PACKETS_MINTFAM_MAGIC");
            plantsFamilyMap.put(PlantFamily.HOMING, "IMAGE_UI_PACKETS_MINTFAM_MAGIC");
        }
        return plantsFamilyMap;
    }
}
