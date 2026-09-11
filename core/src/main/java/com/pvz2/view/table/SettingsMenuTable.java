package com.pvz2.view.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pvz2.Main;
import com.pvz2.controller.GameMenuController;
import com.pvz2.controller.SettingMenuController;
import com.pvz2.view.audios.AudioManager;
import com.pvz2.view.audios.SFXManager;
import com.pvz2.view.screen.MainMenuScreen;

public class SettingsMenuTable extends Table {

    private final SettingMenuController controller = new SettingMenuController();

    public SettingsMenuTable(Main game, Skin skin) {
        super(skin);
        pad(20);

        buildDifficultyRow(skin);
        buildSpeedRow(skin);
        buildMusicVolumeRow(skin);
        buildSfxVolumeRow(skin);
        buildCheckboxes(skin);
    }

    private void buildDifficultyRow(Skin skin) {
        Label difficultyLabel = new Label("Difficulty Level:", skin, "medium");
        difficultyLabel.setColor(Color.BLACK);

        int currentDiff = controller.getCurrentDifficulty();
        final Label difficultyValLabel = new Label(String.valueOf(currentDiff), skin, "medium");
        difficultyValLabel.setColor(Color.BLACK);

        final Slider difficultySlider = new Slider(1f, 5f, 1f, false, skin);
        difficultySlider.setValue(currentDiff);
        difficultySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int val = (int) difficultySlider.getValue();
                difficultyValLabel.setText(String.valueOf(val));
                controller.changeDifficulty(val);
            }
        });

        add(difficultyLabel).left().padBottom(15);
        add(difficultySlider).width(220).padBottom(15).padLeft(10);
        add(difficultyValLabel).left().padLeft(15).padBottom(15).row();
    }

    private void buildSpeedRow(Skin skin) {
        Label speedLabel = new Label("Game Speed:", skin, "medium");
        speedLabel.setColor(Color.BLACK);

        int currentSpeed = controller.getCurrentGameSpeed();
        final Label speedValLabel = new Label(currentSpeed + "x", skin, "medium");
        speedValLabel.setColor(Color.BLACK);

        final Slider speedSlider = new Slider(1f, 3f, 1f, false, skin);
        speedSlider.setValue(currentSpeed);
        speedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int val = (int) speedSlider.getValue();
                speedValLabel.setText(val + "x");
                controller.changeGameSpeed(val);
            }
        });

        add(speedLabel).left().padBottom(15);
        add(speedSlider).width(220).padBottom(15).padLeft(10);
        add(speedValLabel).left().padLeft(15).padBottom(15).row();
    }

    private void buildMusicVolumeRow(Skin skin) {
        Label musicVolumeLabel = new Label("Music Volume:", skin, "medium");
        musicVolumeLabel.setColor(Color.BLACK);

        float currentMusicVol = AudioManager.getInstance().getVolume();
        final Label musicVolValLabel = new Label((int)(currentMusicVol * 100) + "%", skin, "medium");
        musicVolValLabel.setColor(Color.BLACK);

        final Slider musicVolumeSlider = new Slider(0f, 1f, 0.05f, false, skin);
        musicVolumeSlider.setValue(currentMusicVol);
        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = musicVolumeSlider.getValue();
                musicVolValLabel.setText((int)(val * 100) + "%");
                AudioManager.getInstance().setVolume(val);
            }
        });

        add(musicVolumeLabel).left().padBottom(15);
        add(musicVolumeSlider).width(220).padBottom(15).padLeft(10);
        add(musicVolValLabel).left().padLeft(15).padBottom(15).row();
    }

    private void buildSfxVolumeRow(Skin skin) {
        Label sfxVolumeLabel = new Label("SFX Volume:", skin, "medium");
        sfxVolumeLabel.setColor(Color.BLACK);

        float currentSfxVol = SFXManager.getInstance().getVolume();
        final Label sfxVolValLabel = new Label((int)(currentSfxVol * 100) + "%", skin, "medium");
        sfxVolValLabel.setColor(Color.BLACK);

        final Slider sfxVolumeSlider = new Slider(0f, 1f, 0.05f, false, skin);
        sfxVolumeSlider.setValue(currentSfxVol);
        sfxVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float val = sfxVolumeSlider.getValue();
                sfxVolValLabel.setText((int)(val * 100) + "%");
                SFXManager.getInstance().setVolume(val);
            }
        });

        add(sfxVolumeLabel).left().padBottom(20);
        add(sfxVolumeSlider).width(220).padBottom(20).padLeft(10);
        add(sfxVolValLabel).left().padLeft(15).padBottom(20).row();
    }

    private void buildCheckboxes(Skin skin) {
        final CheckBox pauseMusicCheckBox = new CheckBox(" Pause / Mute Music", skin);
        pauseMusicCheckBox.getLabel().setColor(Color.BLACK);
        pauseMusicCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (pauseMusicCheckBox.isChecked()) {
                    AudioManager.getInstance().pauseMusic();
                } else {
                    AudioManager.getInstance().resumeMusic();
                }
            }
        });

        final CheckBox showGridCheckBox = new CheckBox(" Show Lawn Grid Lines (Red)", skin);
        showGridCheckBox.getLabel().setColor(Color.BLACK);
        showGridCheckBox.setChecked(controller.isShowGrid());
        showGridCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setShowGrid(showGridCheckBox.isChecked());
            }
        });

        final CheckBox debugCheckBox = new CheckBox(" Enable Debug Mode (Add Sun, Food, Coins, Gems)", skin);
        debugCheckBox.getLabel().setColor(Color.BLACK);
        debugCheckBox.setChecked(controller.isDebugMode());
        debugCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setDebugMode(debugCheckBox.isChecked());
                if (GameMenuController.getScreen() instanceof MainMenuScreen mainMenuScreen) {
                    mainMenuScreen.getResourcesTable().build();
                }
            }
        });

        add(pauseMusicCheckBox).colspan(3).left().padBottom(15).row();
        add(showGridCheckBox).colspan(3).left().padBottom(15).row();
        add(debugCheckBox).colspan(3).left().padBottom(15).row();
    }
}
