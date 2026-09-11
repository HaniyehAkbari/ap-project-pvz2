package com.pvz2.view.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.pvz2.Main;
import com.pvz2.models.world.levelSetup.*;
import com.pvz2.view.util.UiUtils;
import pvz.skin.BorderedTable;

import java.util.ArrayList;
import java.util.List;

public class LevelObjectivesOverlay extends Table {

    private final Texture backgroundTexture;
    private final Runnable onContinue;

    public LevelObjectivesOverlay(Main game, Skin skin, LevelSetup levelSetup, Runnable onContinue) {
        this.onContinue = onContinue;
        setFillParent(true);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.6f));
        pixmap.fill();
        backgroundTexture = new Texture(pixmap);
        setBackground(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        pixmap.dispose();

        BorderedTable frame = new BorderedTable();
        frame.pad(40, 30, 30, 30);

        Label title = new Label("LEVEL OBJECTIVES", skin, "big");
        title.setColor(Color.BLACK);
        title.setAlignment(Align.center);

        Table objectivesTable = new Table();
        objectivesTable.top();

        for (String objective : buildObjectives(levelSetup)) {
            Table row = new Table();

            Image bullet = new Image(UiUtils.getSolidColorRegion(new Color(0.95f, 0.95f, 0.85f, 1f)));
            Label text = new Label(objective, skin, "medium");
            text.setColor(Color.BLACK);
            text.setWrap(true);

            row.add(bullet).size(16f).padRight(12f).top().padTop(6f);
            row.add(text).width(480f).left();

            objectivesTable.add(row).left().padBottom(14f).row();
        }

        TextButton continueBtn = new TextButton("CONTINUE", skin, "purple");
        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
            }
        });

        frame.add(title).padBottom(20).row();
        frame.add(objectivesTable).padBottom(20).row();
        frame.add(continueBtn).width(220);

        add(frame);
    }

    private List<String> buildObjectives(LevelSetup levelSetup) {
        List<String> objectives = new ArrayList<>();

        if (levelSetup instanceof DeadLineLevelSetup) {
            objectives.add("Do not let a single zombie cross the marked line, or it's game over!");
        } else if (levelSetup instanceof SaveOurSeedsLevelSetup) {
            objectives.add("Protect the marked plants at all costs — losing even one means defeat!");
        } else if (levelSetup instanceof LockedPlantsLevelSetup) {
            objectives.add("Some of your plants are locked for this level. Make do with what you have!");
        } else if (levelSetup instanceof ConveyorLevelSetup) {
            objectives.add("No sun will fall from the sky. " +
                "Plants arrive on the conveyor belt instead — defeat every zombie!");
        } else if (levelSetup instanceof NightOpsLevelSetup) {
            objectives.add("It's nighttime — no sun will fall from the sky. Rely on sun-producing plants to survive!");
        } else if (levelSetup instanceof PlantWhatYouGetLevelSetup) {
            objectives.add("You start with a fixed amount of sun and no Sunflowers." +
                " Plant wisely, then bring on the zombies!");
        } else if (levelSetup instanceof BigWaveBeachLevelSetup) {
            objectives.add("Defeat all the zombies before they reach your house.");
            objectives.add("Watch the tide line — it can rise and shrink your lawn!");
        } else {
            objectives.add("Defeat all the zombies before they reach your house.");
        }

        return objectives;
    }

    @Override
    public boolean remove() {
        boolean removed = super.remove();
        backgroundTexture.dispose();
        if (onContinue != null) {
            onContinue.run();
        }
        return removed;
    }
}
