package com.pvz2.view.table;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pvz2.Main;
import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.plant.card.PlantCardFactory;
import com.pvz2.models.world.GameWorld;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SelectedPlantsList extends PlantsTable{
    private PlantType[] slots = new PlantType[8];
    private final ArrayList<PlantCardView> plantCardViewList = new ArrayList<PlantCardView>();
    private final Main game;
    private PlantType imitatorCardType;
    private boolean isActive = false;
    private boolean noBoost = false;

    public SelectedPlantsList(int column, int pad, boolean upgradeBar,
                              int cardWidth, int cardHeight, Consumer<PlantCardView> cardClickMethod, Main game) {
        super(column, pad, upgradeBar, cardWidth, cardHeight, cardClickMethod);
        this.game = game;
        build();
    }

    @Override
    public void build(){
        this.clear();
        plantCardViewList.clear();
        User user = App.getCurrentUser();
        if (user == null) return;
        int i = 1;
        for (PlantType plantType : slots){
            Table cardTable = new Table();
            if (plantType == null){
                Stack stack = new Stack();
                stack.add(new Image(game.textureBank.region("IMAGE_UI_PACKETS_EMPTY_PACKET")));
                cardTable.add(stack).size(cardWidth, cardHeight);
            }
            else{
                int cardLevel = user.getUnlockedPlantsLevels().getOrDefault(plantType, 0);
                PlantCard card;
                if (plantType == PlantType.IMITATER ){
                    if (imitatorCardType == null) return;
                    int targetLevel = user.getUnlockedPlantsLevels().getOrDefault(imitatorCardType, 0);
                    card = PlantCardFactory.createImitatorCard(imitatorCardType, targetLevel,
                        cardLevel);
                    cardLevel = targetLevel;
                }
                else{
                    card = PlantCardFactory.createCard(plantType, Math.max(1, cardLevel));
                }
                boolean boost = !noBoost && user.hasBoost(plantType);
                PlantCardView plantCardView = new PlantCardView(false, boost, false
                    , Math.max(1, cardLevel), card.getSunCost(), plantType);
                cardTable.add(plantCardView).size(cardWidth, cardHeight);
                plantCardView.setClickMethod(cardClickMethod);
                plantCardViewList.add(plantCardView);
            }
            this.add(cardTable).top();
            i++;
            if (i > column){
                i = 1;
                this.row();
            }
        }
    }

    public void unselectPlants(){
        for (PlantCardView plantCardView : plantCardViewList){
            plantCardView.setSelectedState(false);
        }
    }

    public void setImitatorCardType(PlantType imitatorCardType) {
        this.imitatorCardType = imitatorCardType;
    }

    public void activate(java.util.List<PlantCard> cards){
        isActive = true;
        for (PlantCardView plantCardView : plantCardViewList){
            for (PlantCard card : cards){
                if (plantCardView.getType() == card.getType()){
                    plantCardView.setCard(card);
                    break;
                }
            }
        }
    }

    public void activate(){
        isActive = true;
    }

    public void update(){
        if (!isActive) return;
        for (PlantCardView plantCardView : plantCardViewList){
            plantCardView.update();
        }
    }
    public void update(GameWorld world){
        if (!isActive) return;
        for (PlantCardView plantCardView : plantCardViewList){
            plantCardView.update();
            activate(world.getPlantLists());
        }
    }


    public boolean hasPlant(PlantType plantType){
        for (PlantType type : slots){
            if (type == plantType){
                return true;
            }
        }
        return false;
    }

    public void removePlant(PlantType type){
        for (int i = 0; i < 8; i++){
            if (slots[i] == type){
                slots[i] = null;
                for (int j = i + 1; j < 8; j++){
                    if (slots[j] != null){
                        slots[j-1] = slots[j];
                        slots[j] = null;
                    }
                }
                break;
            }
        }
    }

    public void addPlant(PlantType type){
        for (int i = 0; i < 8; i++){
            if (slots[i] == null){
                slots[i] = type;
                break;
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public ArrayList<PlantCardView> getPlantCardViewList() {
        return plantCardViewList;
    }

    public PlantType[] getSlots() {
        return slots;
    }

    public void setNoBoost(boolean noBoost) {
        this.noBoost = noBoost;
    }
}
