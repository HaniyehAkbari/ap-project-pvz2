package com.pvz2.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pvz2.Main;
import com.pvz2.controller.ChapterMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.Chapter;
import com.pvz2.view.audios.GameSFX;
import com.pvz2.view.audios.SFXManager;
import com.pvz2.view.table.ResourcesTable;
import pvz.libpvz.pam.PamPlayer;

public class ChapterMenuScreen extends MenuScreen {

    private TextureRegion textureRegion;
    private PamPlayer pamPlayer;

    private final ChapterMenuController controller = new ChapterMenuController();
    private static final String LOCK_PAM_PATH = "768/INITIAL/UI/UNIVERSE/WORLD_LOCK/WORLD_LOCK.PAM";
    private static final String[] REGION_NAMES = {
        "IMAGE_WORLDMAP_ZOMBOSS_NODE_EGYPT_ZOMBOSS_NODE_EGYPT_914X994",
        "IMAGE_WORLDMAP_BEACH_ANIM27_ANIM27_1362X953",
        "IMAGE_WORLDMAP_ZOMBOSS_NODE_DARK_ZOMBOSS_NODE_DARK_905X1096",
        "IMAGE_WORLDMAP_ZOMBOSS_NODE_ICEAGE_ZOMBOSS_NODE_ICEAGE_1055X1280"
    };

    private Group contentGroup;
    private float scrollX = 0f;
    private float minScrollX = 0f;
    private float velocityX = 0f;

    private boolean isDragging = false;
    private final Vector2 touchStartPos = new Vector2();
    private final Vector2 currTouch = new Vector2();
    private final Vector2 prevTouch = new Vector2();

    public ChapterMenuScreen(Main game) {
        super(game);
        FileHandle assetsFolder = Gdx.files.internal("");
        textureRegion = game.textureBank.region("IMAGE_MAINMENU_BACKGROUND");
        pamPlayer = new PamPlayer(game.textureBank, assetsFolder);
    }

    @Override
    protected void buildUI() {
        buildContentGroup();
        buildTopBar();
    }

    private void buildContentGroup() {
        contentGroup = new Group() {
            @Override
            public void act(float delta) {
                super.act(delta);
                handleViewportIndependentInput();
            }
        };

        Chapter[] chapters = Chapter.values();
        int unlockedChapter = (App.getCurrentUser() != null) ? App.getCurrentUser().getUnlockedChapter() : 1;
        float nodeSize = 600f;
        float spacing = 200f;
        float startX = 200f;
        float startY = (stage.getHeight() - nodeSize) / 2f;
        int count = Math.min(4, chapters.length);

        for (int i = 0; i < count; i++) {
            Chapter chapter = chapters[i];
            boolean isLocked = chapter.ordinal() >= unlockedChapter;
            float posX = startX + i * (nodeSize + spacing);

            Actor nodeActor = createNodeActor(REGION_NAMES[i], chapter, isLocked);
            nodeActor.setSize(nodeSize, nodeSize);
            nodeActor.setPosition(posX, startY);
            contentGroup.addActor(nodeActor);

            Table infoTable = createChapterInfoTable(chapter, i, nodeSize);
            infoTable.setPosition(posX, startY - 85f);
            contentGroup.addActor(infoTable);
        }

        float totalWidth = startX * 2 + count * nodeSize + (count - 1) * spacing;
        minScrollX = Math.min(0, stage.getWidth() - totalWidth);
        mainStack.add(contentGroup);
    }

    private Actor createNodeActor(String regionName, Chapter chapter, boolean isLocked) {
        TextureRegion nodeRegion = game.textureBank.region(regionName);

        Actor nodeActor = new Actor() {
            private float stateTime = 0f;
            @Override
            public void act(float delta) {
                super.act(delta);
                stateTime += delta;
            }
            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (nodeRegion != null) {
                    batch.draw(nodeRegion, getX(), getY(), getWidth(), getHeight());
                }
                if (isLocked) {
                    float centerX = getX() + getWidth() / 2f;
                    float centerY = getY() + getHeight() / 2f;
                    try {
                        pamPlayer.draw(batch, LOCK_PAM_PATH, "idle", stateTime, centerX, centerY, true);
                    } catch (Exception ignored) {}
                }
            }
        };

        nodeActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isDragging) {
                    String result = controller.chooseChapter(chapter);
                    System.out.println(result);
                    if (!isLocked) {
                        game.setScreen(new LevelMenuScreen(game));
                    }
                }
            }
        });
        return nodeActor;
    }

    private Table createChapterInfoTable(Chapter chapter, int index, float nodeSize) {
        int totalLevels = 4;
        int completedLevels = 0;

        if (App.getCurrentUser() != null) {
            int userUnlockedChapter = App.getCurrentUser().getUnlockedChapter();
            if (userUnlockedChapter > index + 1) {
                completedLevels = totalLevels;
            } else if (userUnlockedChapter == index + 1) {
                completedLevels = App.getCurrentUser().getUnlockedLevel() - 1;
            }
        }

        Table infoTable = new Table();
        infoTable.setSize(nodeSize, 80f);

        Label nameLabel = new Label(chapter.name().replace("_", " "), game.skin, "big_outline");
        nameLabel.setFontScale(0.85f);
        Label progressLabel = new Label(completedLevels + "/" + totalLevels, game.skin, "medium_outline");
        progressLabel.setColor(Color.GOLD);

        infoTable.add(nameLabel).row();
        infoTable.add(progressLabel).padTop(4f);

        return infoTable;
    }

    private void buildTopBar() {
        Table topBar = new Table();
        topBar.top().setFillParent(true);
        Table buttonsTable = new Table();

        ImageButton backBtn = createIconButton("IMAGE_UI_ALMANAC_BUTTONS_HUD_BACK_NORMAL",
            "IMAGE_UI_ALMANAC_BUTTONS_HUD_BACK_SELECTED");
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fadeAndSwitchScreen(new MainMenuScreen(game));
                SFXManager.getInstance().playSound(GameSFX.BUTTON_CLICK);
            }
        });

        ImageButton greenBtn = createIconButton("IMAGE_UI_GENERIC_BUTTONS_HUD_ZG_NORMAL",
            "IMAGE_UI_GENERIC_BUTTONS_HUD_ZG_SELECTED");
        greenBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fadeAndSwitchScreen(new GreenhouseMenuScreen(game));
                SFXManager.getInstance().playSound(GameSFX.BUTTON_CLICK);
            }
        });

        ImageButton collectionBtn = createIconButton("IMAGE_UI_HUD_ALMANACBUTTON_BUTTONS_HUD_ALMANAC_NORMAL",
            "IMAGE_UI_HUD_ALMANACBUTTON_BUTTONS_HUD_ALMANAC_NORMAL");
        collectionBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fadeAndSwitchScreen(new CollectionMenuScreen(game, ChapterMenuScreen.this));
                SFXManager.getInstance().playSound(GameSFX.BUTTON_CLICK);
            }
        });

        buttonsTable.add(backBtn).size(55, 55).padRight(10);
        buttonsTable.add(greenBtn).size(55, 55).padRight(10);
        buttonsTable.add(collectionBtn).size(55, 55);

        topBar.add(buttonsTable).left().pad(15);
        topBar.add().expandX();

        if (App.getCurrentUser() != null) {
            ResourcesTable resourcesTable = new ResourcesTable(App.getCurrentUser(), game);
            topBar.add(resourcesTable).right().pad(15);
        }

        mainStack.add(topBar);
    }

    private ImageButton createIconButton(String upRegionName, String downRegionName) {
        TextureRegion upRegion = game.textureBank.region(upRegionName);
        TextureRegion downRegion = game.textureBank.region(downRegionName);

        if (upRegion == null) upRegion = game.textureBank.region("IMAGE_MAINMENU_BACKGROUND");

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(upRegion);
        if (downRegion != null) style.down = new TextureRegionDrawable(downRegion);

        return new ImageButton(style);
    }

    private void handleViewportIndependentInput() {
        if (Gdx.input.isTouched()) {
            if (Gdx.input.justTouched()) touchStartPos.set(Gdx.input.getX(), Gdx.input.getY());

            currTouch.set(Gdx.input.getX(), Gdx.input.getY());
            prevTouch.set(Gdx.input.getX() - Gdx.input.getDeltaX(), Gdx.input.getY() - Gdx.input.getDeltaY());

            stage.screenToStageCoordinates(currTouch);
            stage.screenToStageCoordinates(prevTouch);

            float deltaStageX = currTouch.x - prevTouch.x;

            if (!isDragging && touchStartPos.dst(Gdx.input.getX(), Gdx.input.getY()) > 10f) isDragging = true;

            if (isDragging) {
                scrollX = MathUtils.clamp(scrollX + deltaStageX, minScrollX, 0);
                contentGroup.setX(scrollX);
                velocityX = deltaStageX;
            }
        } else {
            if (Math.abs(velocityX) > 0.5f) {
                scrollX = MathUtils.clamp(scrollX + velocityX, minScrollX, 0);
                contentGroup.setX(scrollX);
                velocityX *= 0.92f;
            }
            if (isDragging) Gdx.app.postRunnable(() -> isDragging = false);
        }
    }

    @Override
    protected void drawBackground(float delta) {
        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.batch.draw(textureRegion, 0, 0, stage.getWidth(), stage.getHeight());
        game.batch.end();
    }
}
