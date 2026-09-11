package com.pvz2.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pvz2.Main;
import com.pvz2.controller.LevelMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.Chapter;
import com.pvz2.view.audios.GameSFX;
import com.pvz2.view.audios.SFXManager;
import com.pvz2.view.table.ResourcesTable;
import pvz.libpvz.pam.PamPlayer;

import java.util.List;

public class LevelMenuScreen extends MenuScreen {

    private TextureRegion backgroundRegion;
    private PamPlayer pamPlayer;

    private final LevelMenuController controller = new LevelMenuController(this);

    private Group contentGroup;
    private float scrollX = 0f;
    private float minScrollX = 0f;
    private float velocityX = 0f;

    private boolean isDragging = false;
    private final Vector2 touchStartPos = new Vector2();
    private final Vector2 currTouch = new Vector2();
    private final Vector2 prevTouch = new Vector2();

    public LevelMenuScreen(Main game) {
        super(game);

        FileHandle assetsFolder = Gdx.files.internal("");
        backgroundRegion = game.textureBank.region("IMAGE_MAINMENU_BACKGROUND");
        pamPlayer = new PamPlayer(game.textureBank, assetsFolder);
    }

    @Override
    protected void buildUI() {
        float stageWidth = stage.getWidth();
        float stageHeight = stage.getHeight();

        contentGroup = new Group() {
            @Override
            public void act(float delta) {
                super.act(delta);
                handleViewportIndependentInput();
            }
        };

        User currentUser = App.getCurrentUser();
        Chapter currentChapter = currentUser.getCurrentChapter();
        int unlockedLevel = currentUser.getUnlockedLevel();
        int unlockedChapterOrdinal = currentUser.getUnlockedChapter();
        boolean isChapterLocked = (currentChapter != null) && (currentChapter.ordinal() > unlockedChapterOrdinal);

        int totalLevels = 4;
        float nodeSize = 350f;
        float startX = 250f;
        float baseY = (stageHeight - nodeSize) / 2f;

        Vector2[] nodeCenters = new Vector2[totalLevels];
        Vector2[] nodePositions = new Vector2[totalLevels];

        calculateNodePositions(totalLevels, nodeSize, startX, baseY, nodePositions, nodeCenters);
        buildPathActors(totalLevels, nodeCenters, currentChapter);
        buildLevelNodes(totalLevels, nodeSize, nodePositions, currentChapter,
            unlockedLevel, unlockedChapterOrdinal, isChapterLocked);

        float maxX = nodePositions[totalLevels - 1].x;
        float totalWidth = maxX + nodeSize + startX;
        minScrollX = Math.min(0, stageWidth - totalWidth);

        mainStack.add(contentGroup);
        buildTopBar();
    }

    private void calculateNodePositions(int totalLevels, float nodeSize, float startX, float baseY,
                                        Vector2[] nodePositions, Vector2[] nodeCenters) {
        float spacingX = 300f;
        float[] yOffsets = { -120f, 150f, -100f, 130f, -80f, 100f };

        for (int i = 0; i < totalLevels; i++) {
            float offsetY = yOffsets[i % yOffsets.length];
            float posX = startX + i * (nodeSize + spacingX);
            float posY = baseY + offsetY;

            nodePositions[i] = new Vector2(posX, posY);
            nodeCenters[i] = new Vector2(posX + nodeSize / 2f, posY + nodeSize / 2f);
        }
    }

    private void buildPathActors(int totalLevels, Vector2[] nodeCenters, Chapter currentChapter) {
        List<String> chapterPathPams = getPathPamsForChapter(currentChapter);
        float tileSpacing = getTileSpacingForChapter(currentChapter);

        for (int i = 0; i < totalLevels - 1; i++) {
            Vector2 c1 = nodeCenters[i];
            Vector2 c2 = nodeCenters[i + 1];

            float distance = c1.dst(c2);
            float angleDeg = MathUtils.atan2(c2.y - c1.y, c2.x - c1.x) * MathUtils.radiansToDegrees;
            int tileCount = Math.max(1, (int) (distance / tileSpacing));

            Actor pathActor = createPathActor(c1, c2, angleDeg, tileCount, i, chapterPathPams);
            contentGroup.addActor(pathActor);
        }
    }

    private Actor createPathActor(final Vector2 c1, final Vector2 c2, final float angleDeg,
                                  final int tileCount, final int levelIndex, final List<String> chapterPathPams) {
        return new Actor() {
            private float stateTime = 0f;
            private final Matrix4 originalMatrix = new Matrix4();
            private final Matrix4 transformMatrix = new Matrix4();

            @Override
            public void act(float delta) {
                super.act(delta);
                stateTime += delta;
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                try {
                    originalMatrix.set(batch.getTransformMatrix());

                    for (int k = 1; k < tileCount; k++) {
                        float t = (float) k / tileCount;
                        float px = MathUtils.lerp(c1.x, c2.x, t);
                        float py = MathUtils.lerp(c1.y, c2.y, t);

                        int pamIndex = (levelIndex + k) % chapterPathPams.size();
                        String selectedPam = chapterPathPams.get(pamIndex);

                        transformMatrix.set(originalMatrix);
                        transformMatrix.translate(px, py, 0);
                        transformMatrix.rotate(0, 0, 1, angleDeg);

                        batch.setTransformMatrix(transformMatrix);
                        pamPlayer.draw(batch, selectedPam, "idle", stateTime, 0, 0, true);
                    }

                    batch.setTransformMatrix(originalMatrix);
                } catch (Exception ignored) {}
            }
        };
    }

    private void buildLevelNodes(int totalLevels, float nodeSize, Vector2[] nodePositions, Chapter currentChapter,
                                 int unlockedLevel, int unlockedChapterOrdinal, boolean isChapterLocked) {
        for (int i = 0; i < totalLevels; i++) {
            final int levelIndex = i + 1;
            final boolean isBoss = (i == totalLevels - 1);
            final String nodePam = getNodePamForLevel(currentChapter, isBoss);
            final int levelStatus = determineLevelStatus(levelIndex, currentChapter,
                unlockedLevel, unlockedChapterOrdinal, isChapterLocked);

            Actor levelNodeActor = createNodeActor(nodePam, levelStatus);
            float currentSize = isBoss ? nodeSize * 1.25f : nodeSize;
            levelNodeActor.setSize(currentSize, currentSize);
            levelNodeActor.setPosition(nodePositions[i].x, nodePositions[i].y);

            levelNodeActor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (isDragging) return;
                    if (levelStatus == 0) {
                        System.out.println("this level is locked!");
                        return;
                    }
                    String result = controller.chooseLevel(levelIndex);
                    System.out.println(result);
                    SFXManager.getInstance().playSound(GameSFX.BUTTON_CLICK);
                }
            });

            contentGroup.addActor(levelNodeActor);
        }
    }

    private int determineLevelStatus(int levelIndex, Chapter currentChapter,
                                     int unlockedLevel, int unlockedChapterOrdinal, boolean isChapterLocked) {
        if (isChapterLocked) return 0;
        if (levelIndex < unlockedLevel || currentChapter.ordinal() + 1 < unlockedChapterOrdinal) return 2;
        if (levelIndex == unlockedLevel) return 1;
        return 0;
    }

    private Actor createNodeActor(final String nodePam, final int levelStatus) {
        return new Actor() {
            private float stateTime = 0f;

            @Override
            public void act(float delta) {
                super.act(delta);
                stateTime += delta;
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                float centerX = getX() + getWidth() / 2f;
                float centerY = getY() + getHeight() / 2f;

                String animName = "locked_idle";
                if (levelStatus == 2) animName = "finished";
                else if (levelStatus == 1) animName = "unlocked";

                try {
                    pamPlayer.draw(batch, nodePam, animName, stateTime, centerX, centerY, true);
                } catch (Exception ignored) {}
            }
        };
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
                fadeAndSwitchScreen(new ChapterMenuScreen(game));
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
                fadeAndSwitchScreen(new CollectionMenuScreen(game, LevelMenuScreen.this));
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
            if (Gdx.input.justTouched()) {
                touchStartPos.set(Gdx.input.getX(), Gdx.input.getY());
            }

            currTouch.set(Gdx.input.getX(), Gdx.input.getY());
            prevTouch.set(Gdx.input.getX() - Gdx.input.getDeltaX(), Gdx.input.getY() - Gdx.input.getDeltaY());

            stage.screenToStageCoordinates(currTouch);
            stage.screenToStageCoordinates(prevTouch);

            float deltaStageX = currTouch.x - prevTouch.x;

            if (!isDragging && touchStartPos.dst(Gdx.input.getX(), Gdx.input.getY()) > 10f) {
                isDragging = true;
            }

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

            if (isDragging) {
                Gdx.app.postRunnable(() -> isDragging = false);
            }
        }
    }

    @Override
    protected void drawBackground(float delta) {
        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        if (backgroundRegion != null) {
            game.batch.draw(backgroundRegion, 0, 0, stage.getWidth(), stage.getHeight());
        }
        game.batch.end();
    }

    private List<String> getPathPamsForChapter(Chapter chapter) {
        if (chapter == null) {
            return List.of("768/INITIAL/WORLDMAP/PATHS/ANIM6.PAM");
        }

        switch (chapter) {
            case EGYPT:
                return List.of(
                    "768/INITIAL/WORLDMAP/EGYPT/ANIM5/ANIM5.PAM",
                    "768/INITIAL/WORLDMAP/EGYPT/ANIM6/ANIM6.PAM",
                    "768/INITIAL/WORLDMAP/EGYPT/ANIM7/ANIM7.PAM",
                    "768/INITIAL/WORLDMAP/EGYPT/ANIM9/ANIM9.PAM"
                );
            case BIG_WAVE_BEACH:
                return List.of(
                    "768/FULL/WORLDMAP/BEACH/ANIM10/ANIM10.PAM",
                    "768/FULL/WORLDMAP/BEACH/ANIM11/ANIM11.PAM",
                    "768/FULL/WORLDMAP/BEACH/ANIM12/ANIM12.PAM",
                    "768/FULL/WORLDMAP/BEACH/ANIM13/ANIM13.PAM",
                    "768/FULL/WORLDMAP/BEACH/ANIM14/ANIM14.PAM"
                );
            case DARK_AGES:
                return List.of(
                    "768/FULL/WORLDMAP/DARK/ANIM15/ANIM15.PAM",
                    "768/FULL/WORLDMAP/DARK/ANIM16/ANIM16.PAM",
                    "768/FULL/WORLDMAP/DARK/ANIM13/ANIM13.PAM",
                    "768/FULL/WORLDMAP/DARK/ANIM12/ANIM12.PAM"
                );
            case FROSTBITE_CAVES:
                return List.of(
                    "768/FULL/WORLDMAP/ICEAGE/ANIM10/ANIM10.PAM",
                    "768/FULL/WORLDMAP/ICEAGE/ANIM11/ANIM11.PAM",
                    "768/FULL/WORLDMAP/ICEAGE/ANIM12/ANIM12.PAM",
                    "768/FULL/WORLDMAP/ICEAGE/ANIM26/ANIM26.PAM",
                    "768/FULL/WORLDMAP/ICEAGE/ANIM28/ANIM28.PAM"
                );
            default:
                return List.of("768/INITIAL/WORLDMAP/PATHS/ANIM6.PAM");
        }
    }

    private float getTileSpacingForChapter(Chapter chapter) {
        if (chapter == null) return 90f;

        switch (chapter) {
            case BIG_WAVE_BEACH:
                return 180f;
            case FROSTBITE_CAVES:
                return 210f;
            case DARK_AGES:
                return 70f;
            case EGYPT:
                return 120f;
            default:
                return 90f;
        }
    }

    private String getNodePamForLevel(Chapter chapter, boolean isBoss) {
        if (chapter == null) return "768/INITIAL/WORLDMAP/LEVEL_NODE/LEVEL_NODE.PAM";

        if (isBoss) {
            return "768/INITIAL/WORLDMAP/LEVEL_NODE_GARGANTUAR/LEVEL_NODE_GARGANTUAR.PAM";
        } else {
            return "768/INITIAL/WORLDMAP/LEVEL_NODE/LEVEL_NODE.PAM";
        }
    }
}

