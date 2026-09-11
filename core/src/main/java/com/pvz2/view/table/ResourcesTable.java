package com.pvz2.view.table;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.pvz2.Main;
import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.view.screen.MainMenuScreen;

public class ResourcesTable extends Table {
    private Table coinTable;
    private Table diamondTable;
    private User user;
    private Main game;
    private Label coinLabel;
    private Label diamondLabel;

    public ResourcesTable(User user, Main game) {
        this.user = user;
        this.game = game;
        build();
    }

    public void build(){
        this.clear();
        coinLabel = new Label("0", game.skin);
        diamondLabel = new Label("0", game.skin);
        coinTable = buildResourceTbl("IMAGE_UI_HUD_INGAME_COIN", coinLabel);
        diamondTable = buildResourceTbl("IMAGE_EFFECTS_COIN_DIAMOND_COIN_DIAMOND_141X146",
            diamondLabel);
        this.add(coinTable).padRight(20);
        this.add(diamondTable);
    }

    private Table buildResourceTbl(String icon, Label label){
        Table tbl = new Table();
        tbl.setBackground(new TextureRegionDrawable(game.textureBank.region(
            "IMAGE_UI_GENERIC_BUTTON_GENERIC_LTECURRENCY")));
        Image img = new Image(game.textureBank.region(icon));
        img.setScaling(Scaling.fit);
        tbl.add(img).height(60).padLeft(-8);
        tbl.add().expandX();
        tbl.add(label);
        tbl.add().expandX();
        if (App.getCurrentUser().isDebugMode()){
            ImageButton buyBtn = MainMenuScreen.createImageButton("IMAGE_UI_HUD_INGAME_COIN_BUY",
                "IMAGE_UI_HUD_INGAME_COIN_BUY_DOWN",
                game.textureBank);
            buyBtn.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (icon.equals("IMAGE_UI_HUD_INGAME_COIN")) {
                        user.addCoins(1000);
                    } else {
                        user.addGems(10);
                    }
                    update();

                }
            });
            tbl.add(buyBtn).size(40,40).padRight(-5);
        }
        update();
        return tbl;
    }

    public void update() {
        if (user != null) {
            coinLabel.setText(String.valueOf(user.getCoins()));
            diamondLabel.setText(String.valueOf(user.getGems()));
        }
    }
}
