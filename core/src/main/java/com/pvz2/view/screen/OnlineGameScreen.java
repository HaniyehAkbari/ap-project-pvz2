package com.pvz2.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pvz2.Main;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.miniGame.IZombie.Brain;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.PlantAnimationClips;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.world.*;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.network.onlineIZombie.BrainCurrency;
import com.pvz2.models.network.onlineIZombie.ClientGameController;
import com.pvz2.view.audios.GameSFX;
import com.pvz2.view.audios.SFXManager;
import com.pvz2.view.graphic.*;
import com.pvz2.view.table.GameEndOverlay;
import com.pvz2.view.table.OnlineGameHud;
import com.pvz2.view.table.ShovelPlacementManager;
import com.pvz2.view.util.*;
import pvz.libpvz.pam.PamPlayer;

import java.util.*;

public class OnlineGameScreen extends MenuScreen{
    public ClientGameController controller;
    private ZombiePlacementManager zombiePlacementManager = new ZombiePlacementManager();
    private OnlineGameHud hud;
    private PamPlayer pamPlayer;
    private GameEndOverlay endGameOverlay;
    private final TextureRegion bgLeft;
    private final TextureRegion bgMain;
    private final TextureRegion bgRight;
    private final float mainLawnWidth;
    private final float mainLawnHeight;
    private float leftWidthScaled;
    private float rightWidthScaled;
    private LawnGridRenderer lawnGridDebugRenderer;
    private final Map<String, PlantGraphic> plantGraphics = new HashMap<>();
    private final Map<String, ZombieGraphic> zombieGraphics = new HashMap<>();
    private final Map<String, ProjectileGraphic> projectileGraphics = new HashMap<>();
    private final Map<String, SunGraphic> sunGraphics = new HashMap<>();
    private final Map<String, BrainCurrencyGraphic> brainCurrencyGraphics = new HashMap<>();
    private Vector3 cursorWorldPos = new Vector3(0, 0, 0);
    private final List<ExplosionEffectGraphic> explosionGraphics = new ArrayList<>();
    private final List<ProjectileImpactGraphic> projectileImpacts = new ArrayList<>();
    private static OnlineGameScreen activeInstance;
    private TextureRegion brainRegion;
    private final PlantPlacementManager plantPlacementManager = new PlantPlacementManager();
    private final ShovelPlacementManager shovelPlacementManager = new ShovelPlacementManager();
    private float shakeTimeRemaining = 0f;
    private float shakeMagnitude = 0f;
    private Table loadingTable;

    public OnlineGameScreen(Main game, ClientGameController controller) {
        super(game);
        brainRegion = game.textureBank.region("IMAGE_UI_GAMEOVER_FAIL_SCREEN_BRAIN_ONLY");
        this.controller = controller;
        controller.setActionResultListener((success, message) ->
            addToast(success ? "Info" : "Error", message, !success));
        controller.setMatchOverListener((winner, msg) -> showEndScreen(winner, msg));
        String[] keys = getBackgroundKeys();
        bgLeft = game.textureBank.region(keys[0]);
        bgMain = game.textureBank.region(keys[1]);
        bgRight = game.textureBank.region(keys[2]);
        mainLawnWidth = 1800;
        mainLawnHeight = 1000;
        initWorldCamera(mainLawnWidth, mainLawnHeight);
        lawnGridDebugRenderer = new LawnGridRenderer();
        FileHandle assetsFolder = Gdx.files.internal("");
        pamPlayer = new PamPlayer(game.textureBank, assetsFolder);
        pamPlayer.loadAsync("768/INITIAL/EFFECTS/MOWER_SPAWN/MOWER_SPAWN.PAM", null);
        pamPlayer.loadAsync("768/INITIAL/EFFECTS/SUN/SUN.PAM", null);
        pamPlayer.loadAsync("768/FULL/EFFECTS/SUN_BOMB/SUN_BOMB.PAM", null);
        computeSideWidths();
        float mainCenterX = mainLawnWidth / 2f + 150f;
        worldCamera.position.set(mainCenterX, mainLawnHeight / 2f, 0);
        worldCamera.update();
    }

    public ZombiePlacementManager getZombiePlacementManager() {
        return zombiePlacementManager;
    }

    @Override
    public void show() {
        super.show();
        stage.setViewport(new ScreenViewport());
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        activeInstance = this;
    }
    @Override
    public void hide() {
        super.hide();
        if (activeInstance == this) {
            activeInstance = null;
        }
    }
    @Override
    protected void buildUI() {
        if (controller.getWorld() == null) {
            showLoadingUI();
            controller.setOnWorldReadyListener(() -> Gdx.app.postRunnable(this::initGameplayUI));
        } else {
            initGameplayUI();
        }
    }

    private void showLoadingUI() {
        loadingTable = new Table();
        loadingTable.setFillParent(true);
        loadingTable.add(new Label("Waiting for match to start...", skin));
        mainStack.addActor(loadingTable);
    }

    private void initGameplayUI() {
        if (loadingTable != null) {
            loadingTable.remove();
            loadingTable = null;
        }
        hud = new OnlineGameHud(game, skin, this);
        mainStack.addActor(hud);
        hud.resize(stage.getWidth(), stage.getHeight());
    }

    private String[] getBackgroundKeys() {
        return new String[]{
            "IMAGE_BACKGROUNDS_FRONTLAWN_SPRING_TEXTURE_LEFT",
            "IMAGE_BACKGROUNDS_FRONTLAWN_SPRING_TEXTURE",
            "IMAGE_BACKGROUNDS_FRONTLAWN_SPRING_TEXTURE_RIGHT"
        };
    }

    private void computeSideWidths() {
        float scale = (bgMain != null && bgMain.getRegionHeight() > 0)
            ? mainLawnHeight / bgMain.getRegionHeight()
            : 1f;
        leftWidthScaled = bgLeft != null ? bgLeft.getRegionWidth() * scale : mainLawnWidth * 0.4f;
        rightWidthScaled = bgRight != null ? bgRight.getRegionWidth() * scale : mainLawnWidth * 0.4f;
    }

    @Override
    protected void drawBackground(float delta) {
        if (controller.getWorld() == null) return;
        if (controller.getWorld().getState() != GameState.PLAYING) delta = 0;
        handleCameraShakes();
        applyWorldViewportWithShake(delta);
        game.batch.begin();
        drawLawnBackground();
        renderPlacementPreviews(delta);
        List<ZombieGraphic> sortedZombies = syncAndSortZombies(delta);
        syncPlantGraphics();
        game.batch.end();
        renderDebugGrids();
        game.batch.begin();
        renderPlantsAndZombies(delta, sortedZombies);
        renderEffectsAndSandstorms(delta);
        renderCollectablesAndProjectiles(delta);
        for (Brain brain : controller.getWorld().getBrains()){
            if (!brain.isEaten()){
                game.batch.draw(brainRegion, brain.getX()-30, brain.getY()-50, 90f, 90f);
            }
        }
        game.batch.end();

        if (hud != null) {
            hud.update(controller.getWorld(), delta);
        }
    }

    private void handleCameraShakes() {
        for (Zombie z : controller.getWorld().getActiveZombies()) {
            if (z.consumeScreenShakeRequest()) {
                triggerCameraShake(0.3f, 15f);
            }
        }
    }

    private void showEndScreen(ClientGameController.Side winner, String msg) {
        boolean won = winner == controller.getSide();
        if (endGameOverlay != null) endGameOverlay.remove();
        endGameOverlay = new GameEndOverlay(skin, won, msg, null, () -> {
            game.setScreen(new MainMenuScreen(game));
        });
        modalStack.addActor(endGameOverlay);
    }

    private void renderPlacementPreviews(float delta) {
        worldViewport.unproject(cursorWorldPos);
        plantPlacementManager.drawPreview(pamPlayer, game.batch, cursorWorldPos, delta);
        shovelPlacementManager.drawPreview(game.batch, cursorWorldPos);
        zombiePlacementManager.drawPreview(pamPlayer, game.batch, cursorWorldPos, delta);
    }

    private List<ZombieGraphic> syncAndSortZombies(float delta) {
        syncZombieGraphics();
        List<ZombieGraphic> sortedZombies = new ArrayList<>(zombieGraphics.values());
        sortedZombies.sort((z1, z2) -> Float.compare(z2.getZombie().getY(), z1.getZombie().getY()));
        return sortedZombies;
    }

    private void renderDebugGrids() {
        lawnGridDebugRenderer.drawLine(Color.RED,
            App.getCellCenterX(5) - App.getCellWidth() / 2,
            worldCamera);
    }

    private void renderPlantsAndZombies(float delta, List<ZombieGraphic> sortedZombies) {
        for (PlantGraphic pg : plantGraphics.values()) {
            pg.update(delta);
            pg.draw(game.batch, pamPlayer);
        }
        for (ZombieGraphic zg : sortedZombies) {
            zg.update(delta, pamPlayer);
            zg.draw(game.batch, pamPlayer);
        }
    }

    private void renderEffectsAndSandstorms(float delta) {
        for (ExplosionEffectGraphic eg : explosionGraphics) {
            eg.update(delta);
            eg.draw(game.batch, pamPlayer);
        }
        explosionGraphics.removeIf(eg -> eg.isFinished(pamPlayer));
    }

    private void renderCollectablesAndProjectiles(float delta) {
        renderSuns(delta, game.batch, pamPlayer);
        renderBrains(delta, game.batch, pamPlayer);
        renderProjectiles(delta, game.batch, pamPlayer);
    }

    private void drawLawnBackground() {
        float y = 0f;
        if (bgLeft != null) game.batch.draw(bgLeft, -leftWidthScaled, y, leftWidthScaled, mainLawnHeight);
        if (bgMain != null) {
            game.batch.draw(bgMain, 0, y, mainLawnWidth, mainLawnHeight);
        } else {
            game.batch.setColor(Color.DARK_GRAY);
            game.batch.draw(UiUtils.getSolidColorRegion(Color.DARK_GRAY), 0, y, mainLawnWidth, mainLawnHeight);
            game.batch.setColor(Color.WHITE);
        }
        if (bgRight != null) game.batch.draw(bgRight, mainLawnWidth, y, rightWidthScaled, mainLawnHeight);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        worldViewport.update(width, height, false);
        if (hud != null && stage != null) {
            hud.resize(stage.getWidth(), stage.getHeight());
        }
    }

    @Override
    public Table createToastNotification(String title, String message, boolean urgent) {
        if (!urgent) {
            return super.createToastNotification(title, message, urgent);
        }
        Table toast = new Table();
        toast.pad(10);
        Label messageLabel = new Label(message, skin, "big_outline");
        messageLabel.setColor(new Color(0.95f, 0.16f, 0.14f, 1f));
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true);
        messageLabel.setFontScale(1.3f);
        toast.add(messageLabel).width(800).center();
        return toast;
    }

    @Override
    protected void presentToast(Notif notif) {
        if (!notif.urgent) {
            super.presentToast(notif);
            return;
        }
        final Table toastTable = createToastNotification(notif.title, notif.message, true);
        final Table wrapper = new Table();
        wrapper.setFillParent(true);
        wrapper.center();
        wrapper.add(toastTable);
        toastTable.setTransform(true);
        toastTable.setOrigin(Align.center);
        toastTable.setScale(0.7f);
        toastTable.getColor().a = 0f;
        toastStack.addActor(wrapper);
        toastTable.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(0.3f),
                Actions.scaleTo(1f, 1f, 0.35f, Interpolation.swingOut)
            ),
            Actions.delay(2.4f),
            Actions.fadeOut(0.4f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    wrapper.remove();
                    showNextToast();
                }
            })
        ));
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        cursorWorldPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        handleInput();
    }

    public void handleInput() {
        if (controller.getWorld() != null && controller.getWorld().getState() != GameState.PLAYING) {
            return;}
        if (controller.getWorld() == null) return;
        Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        worldViewport.unproject(touchPoint);
        if (Gdx.input.justTouched()) {
            if (controller.getSide() == ClientGameController.Side.PLANTS) {
                handlePlantsInput(touchPoint);
            } else {
                handleZombiesInput(touchPoint);
            }
        }
    }

    private void handlePlantsInput(Vector3 touchPoint) {
        if (plantPlacementManager.isPlantSelected()) {
            int row = LawnGrid.getRowFromY(touchPoint.y);
            int col = LawnGrid.getColFromX(touchPoint.x);
            if (row >= 0 && col >= 0) {
                controller.plantPlant(
                    App.getCellCenterX(col),
                    App.getCellCenterY(row)
                );
                plantPlacementManager.tryPlace();
                SFXManager.getInstance().playSound(GameSFX.PLANT);
            }
        } else if (shovelPlacementManager.isSelected()){
            int row = LawnGrid.getRowFromY(touchPoint.y);
            int col = LawnGrid.getColFromX(touchPoint.x);
            if (row >= 0 && col >= 0) {
                controller.pluckPlant(App.getCellCenterX(col), App.getCellCenterY(row));}
        }
        else {
            controller.collectSun(touchPoint.x, touchPoint.y);
        }
        hud.getSelectedPlantsList().unselectPlants();
        hud.getShovelBtn().setChecked(false);
        plantPlacementManager.cancelSelection();
        shovelPlacementManager.setSelected(false);
    }

    private void handleZombiesInput(Vector3 touchPoint){
        if (zombiePlacementManager.isZombieSelected()) {
            int row = LawnGrid.getRowFromY(touchPoint.y);
            int col = LawnGrid.getColFromX(touchPoint.x);
            if (row >= 0 && col >= 0) {
                controller.placeZombie(
                    zombiePlacementManager.getSelectedZombie(),
                    App.getCellCenterX(col),
                    App.getCellCenterY(row)
                );
            }
        } else {
            controller.collectBrain(touchPoint.x, touchPoint.y);
        }
        hud.getSelectedZombiesList().unselectZombies();
        zombiePlacementManager.cancelSelection();
    }


    private void checkExplosion(Plant newPlant, PlantGraphic pg){
        String fxPath = PlantAnimationClips.getExplosionPamPath(newPlant.getType());
        String fxClip = PlantAnimationClips.getExplosionClip(newPlant.getType());
        if (fxPath != null) {
            float y=pg.getWorldY(),scaleX = 1,scaleY = 1;
            if (newPlant.getType() == PlantType.CHERRY_BOMB){
                y = pg.getWorldY()+100;
            }
            else if (newPlant.getType() == PlantType.JALAPENO){
                y = pg.getWorldY()+20;
                scaleX = 20;
                scaleY = 1.5f;
            }
            explosionGraphics.add(new ExplosionEffectGraphic(
                fxPath, fxClip, pg.getWorldX(), y, pamPlayer, scaleX,
                scaleY
            ));
            SFXManager.getInstance().playSound(GameSFX.CHERRY_BOMB);
            triggerCameraShake(0.3f, 10);
        }
    }

    private void renderSuns(float delta, SpriteBatch batch, PamPlayer pamPlayer){
        List<Sun> active = controller.getWorld().getActiveSuns();
        Set<String> currentIds = new HashSet<>();
        for (Sun s : active) {
            currentIds.add(s.getId());
            SunGraphic existing = sunGraphics.get(s.getId());
            if (existing == null) {
                sunGraphics.put(s.getId(), new SunGraphic(s));
            } else {
                existing.updateModel(s);
            }
        }
        for (SunGraphic sg : sunGraphics.values()) {
            sg.update(delta);
            sg.draw(batch, pamPlayer, game);
        }
        Iterator<Map.Entry<String, SunGraphic>> it = sunGraphics.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, SunGraphic> entry = it.next();
            SunGraphic sg = entry.getValue();
            if (!currentIds.contains(entry.getKey()) || sg.isPopFinished()) {
                it.remove();
            }
        }
    }

    private void renderBrains(float delta, SpriteBatch batch, PamPlayer pamPlayer){
        List<BrainCurrency> active = controller.getWorld().getActiveBrains();
        Set<String> currentIds = new HashSet<>();
        for (BrainCurrency s : active) {
            currentIds.add(s.getId());
            BrainCurrencyGraphic existing = brainCurrencyGraphics.get(s.getId());
            if (existing == null) {
                brainCurrencyGraphics.put(s.getId(), new BrainCurrencyGraphic(s));
            } else {
                existing.updateModel(s);
            }
        }
        for (BrainCurrencyGraphic sg : brainCurrencyGraphics.values()) {
            sg.update(delta);
            sg.draw(batch, pamPlayer, game);
        }
        Iterator<Map.Entry<String, BrainCurrencyGraphic>> it = brainCurrencyGraphics.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, BrainCurrencyGraphic> entry = it.next();
            if (!currentIds.contains(entry.getKey())) {
                it.remove();
            }
        }
    }

    private void renderProjectiles(float delta, SpriteBatch batch, PamPlayer pamPlayer) {
        List<Projectile> active = controller.getWorld().getActiveProjectiles();
        Set<String> currentIds = new HashSet<>();
        for (Projectile p : active) {
            currentIds.add(p.getId());
            ProjectileGraphic existing = projectileGraphics.get(p.getId());
            if (existing == null) {
                projectileGraphics.put(p.getId(), new ProjectileGraphic(p, 0));
            } else {
                existing.updateModel(p);
            }
        }
        Iterator<Map.Entry<String, ProjectileGraphic>> it = projectileGraphics.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ProjectileGraphic> entry = it.next();
            if (!currentIds.contains(entry.getKey())) {
                ProjectileGraphic pg = entry.getValue();
                projectileImpacts.add(new ProjectileImpactGraphic(pg.getType(), pg.getLastX(), pg.getLastY()));
                SFXManager.getInstance().playSound(GameSFX.SPLAT);
                it.remove();
            }
        }
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

    private void syncPlantGraphics() {
        Set<String> currentIds = new HashSet<>();
        for (Plant p : controller.getWorld().getActivePlants()) {
            currentIds.add(p.getId());
            PlantGraphic existing = plantGraphics.get(p.getId());
            if (existing == null) {
                PlantGraphic pg = new PlantGraphic(p, pamPlayer);
                plantGraphics.put(p.getId(), pg);
            } else {
                existing.updateModel(p);
            }
        }
        plantGraphics.entrySet().removeIf(entry -> {
            if (currentIds.contains(entry.getKey())) return false;
            if (entry.getValue().isReadyToRemoveAfterDeath()){
                if (entry.getValue().getPlant() != null){
                    checkExplosion(entry.getValue().getPlant(), entry.getValue());
                }
                return true;
            }
            return false;
        });
    }

    private void syncZombieGraphics() {
        Set<String> currentIds = new HashSet<>();
        for (Zombie z : controller.getWorld().getActiveZombies()) {
            currentIds.add(z.getId());
            ZombieGraphic existing = zombieGraphics.get(z.getId());
            if (existing == null) {
                zombieGraphics.put(z.getId(), new ZombieGraphic(z));
            } else {
                existing.updateModel(z);
            }
        }


        zombieGraphics.entrySet().removeIf(entry -> {
            if (!currentIds.contains(entry.getKey())){
                if (entry.getValue().isNetworkedDeathAnimationFinished()){
                    return true;
                }
                if (!entry.getValue().getCurrentClip().equals("die")){
                    entry.getValue().playClip("die", false);
                }
                return false;
            }
            return false;
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        if (lawnGridDebugRenderer != null) {
            lawnGridDebugRenderer.dispose();
        }
    }
    public PlantPlacementManager getPlantPlacementManager() {
        return plantPlacementManager;
    }
    public ShovelPlacementManager getShovelPlacementManager() {
        return shovelPlacementManager;
    }
    public Map<String, PlantGraphic> getPlantGraphics() {
        return plantGraphics;
    }
    private void triggerCameraShake(float duration, float magnitude) {
        this.shakeTimeRemaining = duration;
        this.shakeMagnitude = magnitude;
    }

    private void applyWorldViewportWithShake(float delta) {
        float baseX = worldCamera.position.x;
        float baseY = worldCamera.position.y;
        if (shakeTimeRemaining > 0f) {
            shakeTimeRemaining -= delta;
            float offsetX = MathUtils.random(-shakeMagnitude, shakeMagnitude);
            float offsetY = MathUtils.random(-shakeMagnitude, shakeMagnitude) * 0.5f;
            worldCamera.position.x = baseX + offsetX;
            worldCamera.position.y = baseY + offsetY;
            worldCamera.update();
        }
        applyWorldViewport();
        worldCamera.position.set(baseX, baseY, 0);
        worldCamera.update();
    }
    public ClientGameController getController() {
        return controller;
    }
}
