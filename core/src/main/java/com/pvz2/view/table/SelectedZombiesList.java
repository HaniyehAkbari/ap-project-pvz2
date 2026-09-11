package com.pvz2.view.table;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pvz2.models.miniGame.IZombie.OnlineIZombieLevel;
import com.pvz2.models.network.onlineIZombie.ZombieCard;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SelectedZombiesList extends Table {
    private final int cardWidth, cardHeight;
    private final Consumer<ZombieCardView> cardClickMethod;
    private final List<ZombieCardView> zombieCardViewList = new ArrayList<>();

    public SelectedZombiesList(int cardWidth, int cardHeight, Consumer<ZombieCardView> cardClickMethod) {
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.cardClickMethod = cardClickMethod;
    }

    public void build(List<ZombieCard> zombieCards) {
        this.clear();
        zombieCardViewList.clear();

        for (ZombieCard card : zombieCards) {
            ZombieCardView view = new ZombieCardView(card.isReady(), card.getBrainCost(), card.getType());
            view.setCard(card);
            view.setClickMethod(cardClickMethod);
            zombieCardViewList.add(view);
            this.add(view).size(cardWidth, cardHeight);
        }
    }

    public void activate(java.util.List<ZombieCard> cards){
        for (ZombieCardView zombieCardView : zombieCardViewList){
            for (ZombieCard card : cards){
                if (zombieCardView.getZombieName().equals(card.getType())){
                    zombieCardView.setCard(card);
                    break;
                }
            }
        }
    }

    public void update(OnlineIZombieLevel world){
        for (ZombieCardView view : zombieCardViewList) {
            activate(world.getZombieCards());
            view.update();
        }
    }

    public void unselectZombies(){
        for (ZombieCardView zombieCardView : zombieCardViewList){
            zombieCardView.setSelectedState(false);
        }
    }

    public List<ZombieCardView> getZombieCardViewList() {
        return zombieCardViewList;
    }
}
