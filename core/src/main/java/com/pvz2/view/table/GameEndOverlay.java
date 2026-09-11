package com.pvz2.view.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.pvz2.Main;
import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.core.UserManager;
import com.pvz2.models.miniGame.MiniGameWorld;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.levelSetup.DeadLineLevelSetup;
import com.pvz2.models.miniGame.IZombie.IZombieLevel;
import com.pvz2.view.audios.AudioManager;
import com.pvz2.view.audios.GameMusic;
import com.pvz2.view.screen.LevelMenuScreen;
import com.pvz2.view.screen.MainMenuScreen;
import pvz.skin.BorderedTable;

public class GameEndOverlay extends Table {

    private final Texture backgroundTexture;

    public GameEndOverlay(Skin skin, boolean won, String statusMessage, Runnable onRestart, Runnable onExit) {
        setFillParent(true);

        this.backgroundTexture = createBackgroundTexture();
        setBackground(new TextureRegionDrawable(new TextureRegion(this.backgroundTexture)));

        BorderedTable frame = new BorderedTable();
        frame.pad(40, 30, 30, 30);

        frame.add(createTitleLabel(skin, won)).padBottom(20).row();
        frame.add(createStatusLabel(skin, statusMessage)).width(480f).padBottom(20).row();
        frame.add(createButtonsTable(onRestart, onExit));

        add(frame);

        AudioManager.getInstance().playMusic(won ? GameMusic.WIN : GameMusic.LOSE, false);
    }

    public GameEndOverlay(Main game, Skin skin, GameWorld world, Runnable onRestart) {
        this(skin,
            world.getState() == GameState.WON,
            buildStatusMessage(world, world.getState() == GameState.WON),
            onRestart,
            buildOfflineExitAction(game, world));
    }

    private static Runnable buildOfflineExitAction(Main game, GameWorld world) {
        return () -> {
            App.setCurrentGame(null);
            if (world instanceof MiniGameWorld) {
                game.setScreen(new MainMenuScreen(game));
            } else {
                game.setScreen(new LevelMenuScreen(game));
            }
        };
    }

    private Texture createBackgroundTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.6f));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Label createTitleLabel(Skin skin, boolean won) {
        Label title = new Label(won ? "LEVEL COMPLETE!" : "GAME OVER", skin, "big");
        title.setColor(won ? Color.BLACK : new Color(0.55f, 0.05f, 0.05f, 1f));
        title.setAlignment(Align.center);
        return title;
    }

    private Label createStatusLabel(Skin skin, String message) {
        Label status = new Label(message, skin, "medium");
        status.setColor(Color.BLACK);
        status.setWrap(true);
        status.setAlignment(Align.center);
        return status;
    }

    private Table createButtonsTable(Runnable onRestart, Runnable onExit) {
        Table buttonsTable = new Table();

        TextButton exitBtn = new TextButton("EXIT", App.getGameApp().skin, "green");
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                User user = App.getCurrentUser();
                if (user != null) {
                    user.save();
                    UserManager.syncCurrentUser();
                }
                remove();
                if (onExit != null) onExit.run();
                AudioManager.getInstance().playMusic(GameMusic.TITLE, true);
            }
        });
        buttonsTable.add(exitBtn).pad(10).width(180);

        if (onRestart != null) {
            TextButton restartBtn = new TextButton("RESTART", App.getGameApp().skin, "brown");
            restartBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    remove();
                    onRestart.run();
                    AudioManager.getInstance().playMusic(GameMusic.HOUSE, true);

                }
            });
            buttonsTable.add(restartBtn).pad(10).width(180);
        }

        return buttonsTable;
    }

    private static String buildStatusMessage(GameWorld world, boolean won) {
        if (won) {
            if (world instanceof IZombieLevel) {
                return "Delicious! You ate all the brains and won the level!";
            }
            return "You defended your lawn and cleared every wave. Nice work, neighbor!";
        }

        if (world instanceof IZombieLevel) {
            return "You ran out of zombies and failed to eat all the brains!";
        }
        if (world.getLevelSetup() instanceof DeadLineLevelSetup) {
            return "A zombie crossed the marked line. Better luck next time!";
        }
        return "The zombies got through and ate your brains. Try again!";
    }

    @Override
    public boolean remove() {
        boolean removed = super.remove();
        backgroundTexture.dispose();
        return removed;
    }
}
