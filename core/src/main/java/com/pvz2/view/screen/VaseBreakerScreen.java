package com.pvz2.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pvz2.Main;
import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.miniGame.vaseBreaker.Vase;
import com.pvz2.models.miniGame.vaseBreaker.VaseBreakerLevel;
import com.pvz2.models.miniGame.vaseBreaker.VaseType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.pool.GenericObjectPool;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameState;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.view.audios.AudioManager;
import com.pvz2.view.audios.GameMusic;
import com.pvz2.view.audios.GameSFX;
import com.pvz2.view.audios.SFXManager;
import com.pvz2.view.graphic.PlantGraphic;
import com.pvz2.view.graphic.ProjectileGraphic;
import com.pvz2.view.graphic.ProjectileImpactGraphic;
import com.pvz2.view.graphic.ZombieGraphic;
import com.pvz2.view.table.GameEndOverlay;
import com.pvz2.view.table.PlantCardView;
import com.pvz2.view.table.ResourcesTable;
import com.pvz2.view.table.ZombiesTable;
import com.pvz2.view.util.LawnGrid;
import com.pvz2.view.util.PlantPlacementManager;
import pvz.libpvz.pam.PamPlayer;
import java.util.*;
public class VaseBreakerScreen extends MenuScreen {
    private final VaseBreakerLevel world;
    private TextureRegion lawnBackground;
    private final List<VaseGraphic> vaseGraphics = new ArrayList<>();
    private final List<ZombieGraphic> zombieGraphics = new ArrayList<>();
    private final List<PlantGraphic> plantGraphics = new ArrayList<>();
    private final Map<Projectile, ProjectileGraphic> projectileGraphics = new HashMap<>();
    private final List<ProjectileImpactGraphic> projectileImpacts = new ArrayList<>();
    private final FileHandle assetsFolder;
    private PamPlayer pamPlayer;
    private final List<PlantCardView> seedPackets = new ArrayList<>();
    private final PlantPlacementManager plantPlacementManager = new PlantPlacementManager();
    private ImageButton shovelBtn;
    private ImageButton pauseBtn;
    private Table pauseOverlay;
    private GameEndOverlay endGameOverlay;
    private ResourcesTable resourcesTable;
    public enum VaseState {
        DROPPING,
        IDLE,
        BREAKING,
        BROKEN
    }
    public VaseBreakerScreen(Main game, VaseBreakerLevel world) {
        super(game);
        this.world = world;
        this.assetsFolder = Gdx.files.internal("");
        this.pamPlayer = new PamPlayer(game.textureBank, assetsFolder);
    }
    @Override
    public void show() {
        super.show();
        initWorldCamera(1800, 1000);
        lawnBackground = game.textureBank.region("IMAGE_BACKGROUNDS_JOUST_TEXTURE");
        initVaseGraphics();
    }
    @Override
    protected void buildUI() {
        Table topBar = new Table();
        topBar.setFillParent(true);
        topBar.top();
        resourcesTable = new ResourcesTable(App.getCurrentUser(), game);
        shovelBtn = createShovelBtn();
        pauseBtn = new ImageButton(skin, "ingame_pause");
        pauseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
                SFXManager.getInstance().playSound(GameSFX.PAUSE);
            }
        });
        topBar.add(resourcesTable).pad(10).left();
        topBar.add(shovelBtn).pad(20).left();
        topBar.add().expandX();
        topBar.add(pauseBtn).pad(20).right();
        mainStack.addActor(topBar);
    }
    private ImageButton createShovelBtn() {
        TextureRegion shovelIcon = game.textureBank.region("IMAGE_UI_HUD_INGAME_SHOVEL_BUTTON");
        TextureRegion shovelIconSelected = game.textureBank.region("IMAGE_UI_HUD_INGAME_SHOVEL_BUTTON_DOWN");
        Drawable shovel = shovelIcon != null ? new TextureRegionDrawable(shovelIcon) : null;
        Drawable shovelSelected = shovelIconSelected != null ? new TextureRegionDrawable(shovelIconSelected) : null;
        ImageButton button = new ImageButton(shovel, shovel, shovelSelected != null ? shovelSelected : shovel);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameMenuController.selectAndUnselectShovel();
            }
        });
        return button;
    }
    private void togglePause() {
        if (world.getState() == GameState.PLAYING) {
            world.setState(GameState.PAUSED);
            showPauseOverlay();
        } else if (world.getState() == GameState.PAUSED) {
            world.setState(GameState.PLAYING);
            hidePauseOverlay();
        }
    }
    private void showPauseOverlay() {
        if (pauseOverlay != null) pauseOverlay.remove();
        pauseOverlay = new Table();
        pauseOverlay.setFillParent(true);
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.6f));
        pixmap.fill();
        Texture bgTexture = new Texture(pixmap);
        pixmap.dispose();
        pauseOverlay.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        TextButton resumeBtn = new TextButton("RESUME", skin, "purple");
        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });
        TextButton restartBtn = new TextButton("RESTART", skin, "purple");
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restartLevel();
                AudioManager.getInstance().playMusic(GameMusic.HOUSE, true);
            }
        });
        TextButton exitBtn = new TextButton("EXIT", skin, "green");
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                world.setState(GameState.PLAYING);
                game.setScreen(new MainMenuScreen(game));
                AudioManager.getInstance().playMusic(GameMusic.TITLE, true);
            }
        });
        pauseOverlay.add(resumeBtn).pad(10).row();
        pauseOverlay.add(restartBtn).pad(10).row();
        pauseOverlay.add(exitBtn).pad(10);
        modalStack.addActor(pauseOverlay);
    }
    private void hidePauseOverlay() {
        if (pauseOverlay != null) {
            pauseOverlay.remove();
            pauseOverlay = null;
        }
    }
    private void showEndGameOverlay() {
        if (endGameOverlay != null) endGameOverlay.remove();
        endGameOverlay = new GameEndOverlay(game, skin, world, this::restartLevel);
        modalStack.addActor(endGameOverlay);
    }
    private void hideEndGameOverlay() {
        if (endGameOverlay != null) {
            endGameOverlay.remove();
            endGameOverlay = null;
        }
    }
    public void restartLevel() {
        world.reset();
        world.setState(GameState.PLAYING);
        world.setEndGameHandled(false);
        hidePauseOverlay();
        hideEndGameOverlay();
        plantGraphics.clear();
        zombieGraphics.clear();
        seedPackets.clear();
        projectileGraphics.clear();
        projectileImpacts.clear();
        plantPlacementManager.cancelSelection();
        initVaseGraphics();
    }
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (worldViewport != null) {
            worldViewport.update(width, height, true);
        }
    }
    @Override
    protected void drawBackground(float delta) {
        updateLogic(delta);
        applyWorldViewport();
        game.batch.setProjectionMatrix(worldViewport.getCamera().combined);
        game.batch.begin();
        if (lawnBackground != null) {
            game.batch.draw(lawnBackground, 0, 0, 1800, 1000);
        }
        for (VaseGraphic vg : vaseGraphics) {
            vg.draw();
        }
        for (PlantGraphic pg : plantGraphics) {
            pg.draw(game.batch, pamPlayer);
        }
        for (ZombieGraphic zg : zombieGraphics) {
            zg.draw(game.batch, pamPlayer);
        }
        renderProjectiles(delta, game.batch, pamPlayer);
        for (PlantCardView card : seedPackets) {
            card.draw(game.batch, 1f);
        }
        Vector3 cursorWorldPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        worldViewport.unproject(cursorWorldPos);
        plantPlacementManager.drawPreview(pamPlayer, game.batch, cursorWorldPos, delta);
        game.batch.end();
    }
    private void updateLogic(float delta) {
        handleInput();
        if (world.getState() == GameState.PAUSED) return;
        if (world.getState() == GameState.PLAYING) {
            checkWinLoseConditions();
        }
        world.tick(delta);
        for (VaseGraphic vg : vaseGraphics) {
            vg.update(delta);
        }
        for (PlantGraphic pg : plantGraphics) {
            pg.update(delta);
        }
        for (ZombieGraphic zg : zombieGraphics) {
            zg.update(delta, pamPlayer);
        }
        zombieGraphics.removeIf(zg -> zg.getZombie().isDead());
        plantGraphics.removeIf(PlantGraphic::isDead);
        if (world.getState() != GameState.PAUSED && !world.isEndGameHandled()) {
            if (world.getState() == GameState.WON) {
                world.setEndGameHandled(true);
                GameMenuController.handleWinning(world);
                showEndGameOverlay();
            } else if (world.getState() == GameState.LOST) {
                world.setEndGameHandled(true);
                GameMenuController.handleLosing(world);
                showEndGameOverlay();
            }
        }
        if (resourcesTable != null) {
            resourcesTable.update();
        }
    }
    private void checkWinLoseConditions() {
        float houseEntryX = LawnGrid.ORIGIN_X - (LawnGrid.CELL_WIDTH / 2f);
        boolean zombieReachedHouse = zombieGraphics.stream()
            .anyMatch(zg -> !zg.getZombie().isDead() && zg.getZombie().getX() <= houseEntryX);
        boolean allVasesBroken = vaseGraphics.stream().allMatch(VaseGraphic::isBroken);
        boolean noCardsOrSelection = seedPackets.isEmpty() && !plantPlacementManager.isPlantSelected();
        boolean noPlantsOnBoard = plantGraphics.isEmpty();
        boolean activeZombiesExist = zombieGraphics.stream().anyMatch(zg -> !zg.getZombie().isDead());
        if (zombieReachedHouse || (allVasesBroken && noCardsOrSelection && noPlantsOnBoard && activeZombiesExist)) {
            world.setState(GameState.LOST);
            return;
        }
        if (allVasesBroken && !activeZombiesExist) {
            world.setState(GameState.WON);
        }
    }
    private void renderProjectiles(float delta, SpriteBatch batch, PamPlayer pamPlayer) {
        List<Projectile> active = world.getActiveProjectiles();
        GenericObjectPool<Projectile> pool = App.getCurrentGame().getProjectilesPool();
        Iterator<Map.Entry<Projectile, ProjectileGraphic>> it = projectileGraphics.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Projectile, ProjectileGraphic> entry = it.next();
            ProjectileGraphic pg = entry.getValue();
            boolean reused = pool.getGeneration(entry.getKey()) != pg.getGeneration();
            boolean noLongerActive = !active.contains(entry.getKey());
            if (reused || noLongerActive) {
                projectileImpacts.add(new ProjectileImpactGraphic(pg.getType(), pg.getLastX(), pg.getLastY()));
                SFXManager.getInstance().playSound(GameSFX.SPLAT);
                it.remove();}}
        for (Projectile p : active) {
            int currentGen = pool.getGeneration(p);
            ProjectileGraphic existing = projectileGraphics.get(p);
            if (existing == null || existing.getGeneration() != currentGen) {
                projectileGraphics.put(p, new ProjectileGraphic(p, currentGen));}}
        for (ProjectileGraphic pg : projectileGraphics.values()) {
            pg.update(delta);
            pg.draw(batch, pamPlayer);
        }
        Iterator<ProjectileImpactGraphic> impactIt = projectileImpacts.iterator();
        while (impactIt.hasNext()) {
            ProjectileImpactGraphic ig = impactIt.next();
            ig.update(delta);
            ig.draw(batch, pamPlayer);
            if (ig.isFinished(pamPlayer)) impactIt.remove();
        }
    }
    private void initVaseGraphics() {
        vaseGraphics.clear();
        zombieGraphics.clear();
        for (Vase vase : world.getVases()) {
            float targetX = LawnGrid.getCellX(vase.getCol());
            float targetY = LawnGrid.getCellY(vase.getRow());
            vaseGraphics.add(new VaseGraphic(vase, targetX, targetY));
            if (vase.getHiddenZombie() != null) {
                String lookupName = vase.getHiddenZombie().getName().name();
                lookupName = App.getArmoredZombieName(lookupName);
                String pamPath = ZombiesTable.getZombiesAnimAddress().get(lookupName);
                if (pamPath != null) {
                    pamPlayer.loadAsync(pamPath, null);}}}
        vaseGraphics.sort((v1, v2) -> Float.compare(v2.targetY, v1.targetY));
    }
    private void handleInput() {
        if (world.getState() == GameState.PAUSED) return;
        if (Gdx.input.justTouched()) {
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            worldViewport.unproject(touchPoint);
            if (handleShovelAction(touchPoint)) return;
            if (handlePlantPlacement(touchPoint)) return;
            if (handleCardClick(touchPoint)) return;
            handleVaseClick(touchPoint);
        }
    }
    private boolean handleShovelAction(Vector3 touchPoint) {
        if (App.getCurrentGame() != null && App.getCurrentGame().isSelectedShovel()) {
            GameMenuController.pluckPlant(touchPoint.x, touchPoint.y);
            plantGraphics.removeIf(pg -> pg.isDead() || pg.getPlant() == null || pg.getPlant().getCell() == null);
            return true;}
        return false;
    }
    private boolean handlePlantPlacement(Vector3 touchPoint) {
        if (!plantPlacementManager.isPlantSelected()) return false;
        int row = LawnGrid.getRowFromY(touchPoint.y);
        int col = LawnGrid.getColFromX(touchPoint.x);
        if (row >= 0 && col >= 0) {
            PlantType selectedPlant = plantPlacementManager.getSelectedPlant();
            if (canPlantAt(row, col)) {
                if (plantPlacementManager.tryPlace()) {
                    placePlantInCell(row, col, selectedPlant);}
            }
        } else {
            plantPlacementManager.cancelSelection();
        }
        return true;
    }
    private boolean canPlantAt(int row, int col) {
        Cell[][] grid = world.getGrid();
        if (grid == null || row >= grid.length || col >= grid[0].length) return false;
        Cell cell = grid[row][col];
        if (cell == null) return false;

        Vase vase = world.getVaseAt(row, col);
        if (vase != null && !vase.isBroken()) return false;

        return cell.getPlant() == null;
    }
    private void placePlantInCell(int row, int col, PlantType selectedPlant) {
        Cell[][] grid = world.getGrid();
        if (grid != null && row < grid.length && col < grid[0].length) {
            Cell cell = grid[row][col];
            if (cell != null) {
                boolean boosted = App.getCurrentUser() != null && App.getCurrentUser().hasBoost(selectedPlant);
                Plant plantModel = cell.handlePlanting(selectedPlant, boosted);
                if (plantModel != null) {
                    plantModel.setCell(cell);
                    plantGraphics.add(new PlantGraphic(plantModel, pamPlayer));
                    SFXManager.getInstance().playSound(GameSFX.PLANT);
                }
            }
        }
    }
    private boolean handleCardClick(Vector3 touchPoint) {
        for (int i = seedPackets.size() - 1; i >= 0; i--) {
            PlantCardView card = seedPackets.get(i);
            if (touchPoint.x >= card.getX() && touchPoint.x <= card.getX() + card.getWidth() &&
                touchPoint.y >= card.getY() && touchPoint.y <= card.getY() + card.getHeight()) {
                if (card.isActive()) {
                    plantPlacementManager.selectPlant(card.getType(), () -> seedPackets.remove(card));
                }
                return true;
            }
        }
        return false;
    }
    private void handleVaseClick(Vector3 touchPoint) {
        for (int i = vaseGraphics.size() - 1; i >= 0; i--) {
            VaseGraphic vg = vaseGraphics.get(i);
            if (vg.contains(touchPoint.x, touchPoint.y)) {
                vg.onClicked();
                SFXManager.getInstance().playSound(GameSFX.VASE_BREAKING);
                break;
            }
        }
    }
    private class VaseGraphic {
        private final Vase vase;
        private final float targetY;
        private float currentX;
        private float currentY;
        private VaseState state = VaseState.DROPPING;
        private float dropTimer = 0f;
        private final float dropDuration = 0.77f;
        private float animTime = 0f;
        private String currentClip;
        private boolean isLoop = true;
        private final String pamPath;
        private boolean contentSpawned = false;
        private static final Random RANDOM = new Random();
        public VaseGraphic(Vase vase, float targetX, float targetY) {
            this.vase = vase;
            this.targetY = targetY;
            this.currentX = targetX;
            this.currentY = targetY + 600f;
            this.pamPath = getPamPathForType(vase.getType());
            this.currentClip = "idle";
            pamPlayer.loadAsync(pamPath, null);
        }
        private String getPamPathForType(VaseType type) {
            if (type == VaseType.PLANT) return "768/FULL/VASEBREAKER/VASE_GREEN/VASE_GREEN.PAM";
            if (type == VaseType.GIANT) return "768/FULL/VASEBREAKER/VASE_GARGANTUAR/VASE_GARGANTUAR.PAM";
            return "768/FULL/VASEBREAKER/VASE_BROWN/VASE_BROWN.PAM";
        }
        public boolean contains(float x, float y) {
            float width = 110f;
            float height = 110f;
            float minX = currentX - (width / 2f);
            float maxX = currentX + (width / 2f);
            float minY = currentY - 30f;
            float maxY = currentY + height - 30f;
            return state == VaseState.IDLE &&
                x >= minX && x <= maxX &&
                y >= minY && y <= maxY;}
        public void onClicked() {
            if (state == VaseState.IDLE) {
                startBreak();
            }
        }
        public void startBreak() {
            if (state == VaseState.BREAKING || state == VaseState.BROKEN) return;
            state = VaseState.BREAKING;
            animTime = 0f;
            currentClip = RANDOM.nextBoolean() ? "break" : "break2";
            isLoop = false;
        }
        public void update(float delta) {
            animTime += delta;
            if (state == VaseState.DROPPING) {
                dropTimer += delta;
                float progress = Math.min(1f, dropTimer / dropDuration);
                currentY = Interpolation.bounceOut.apply(targetY + 600f, targetY, progress);
                if (progress >= 1f) {
                    currentY = targetY;
                    state = VaseState.IDLE;
                    currentClip = "idle";
                    animTime = 0f;}
            } else if (state == VaseState.BREAKING) {
                float clipDuration = pamPlayer.clipDurationSeconds(pamPath, currentClip);
                if (!contentSpawned && animTime >= clipDuration * 0.1f) {
                    contentSpawned = true;
                    spawnContent();}
                if (animTime >= clipDuration) {
                    state = VaseState.BROKEN;
                }
            }
        }
        private void spawnContent() {
            world.breakVaseAt(vase.getRow(), vase.getCol());
            if (vase.getHiddenZombie() != null) {
                Zombie zombie = vase.getHiddenZombie();
                zombie.setSpeed(zombie.getSpeed() * 15.0 / 100.0);
                float startX = LawnGrid.getCellX(vase.getCol());
                float startY = LawnGrid.getCellY(vase.getRow());
                zombie.setX(startX);
                zombie.setY(startY);
                world.getActiveZombies().add(zombie);
                zombieGraphics.add(new ZombieGraphic(zombie));}
            else if (vase.getHiddenSeed() != null) {
                PlantType plantType = vase.getHiddenSeed().getPlantType();
                float x = LawnGrid.getCellX(vase.getCol());
                float y = LawnGrid.getCellY(vase.getRow());
                int level = App.getCurrentUser() != null ?
                    App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(plantType, 1) : 1;
                int cost = 0;
                PlantCardView card = new PlantCardView(true, false, false, level, cost, plantType);
                float width = 100f;
                float height = 130f;
                card.setSize(width, height);
                card.clearChildren();
                card.build();
                card.setPosition(x - width / 2f, y - height / 2f);
                card.setClickMethod(clickedCard -> {
                    plantPlacementManager.selectPlant(clickedCard.getType(), () -> {
                        seedPackets.remove(clickedCard);});});
                seedPackets.add(card);}
        }
        public void draw() {
            if (state == VaseState.BROKEN) return;
            pamPlayer.draw(game.batch, pamPath, currentClip, animTime, currentX, currentY, isLoop);
        }
        public boolean isBroken() {
            return this.state == VaseState.BROKEN;
        }
    }
}
