package com.pvz2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.core.GameInitializer;
import com.pvz2.models.core.UserManager;
import com.pvz2.models.zombie.ZombieRegistry;
import com.pvz2.models.network.NetworkClient;
import com.pvz2.view.audios.AudioManager;
import com.pvz2.view.audios.GameMusic;
import com.pvz2.view.screen.LoginMenuScreen;
import com.pvz2.view.screen.MainMenuScreen;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.PvzSkin;

import java.io.IOException;

public class Main extends Game {
    public TextureBank textureBank;
    public PamPlayer pamPlayer;
    public Skin skin;
    public SpriteBatch batch;



    @Override
    public void create() {
        try {
            NetworkClient.get().connect("localhost", 8080);
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }

        boolean foundUser = UserManager.loadInitialUser();

        GameInitializer.loadPlantUpgrades();
        ZombieRegistry.init();

        batch = new SpriteBatch();

        FileHandle assetsFolder = Gdx.files.internal("");
        textureBank = new TextureBank("768", assetsFolder);
        pamPlayer = new PamPlayer(textureBank, assetsFolder);
        skin = PvzSkin.get();
        App.setGameApp(this);

        if(foundUser){
            MainMenuScreen mainMenuScreen = new MainMenuScreen(this);
            setScreen(mainMenuScreen);
            AudioManager.getInstance().playMusic(GameMusic.TITLE, true);
            GameMenuController.setScreen(mainMenuScreen);
        } else {
            setScreen(new LoginMenuScreen(this));
            AudioManager.getInstance().playMusic(GameMusic.TITLE, true);
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (textureBank != null) textureBank.dispose();
    }
}
