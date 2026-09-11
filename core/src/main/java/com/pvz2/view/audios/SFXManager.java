package com.pvz2.view.audios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class SFXManager {
    private static SFXManager instance;

    private final HashMap<GameSFX, Sound> sfxCache = new HashMap<>();

    private float volume = 0.8f;

    private int chompIndex = 0;
    private final GameSFX[] chompSounds = new GameSFX[]{
        GameSFX.CHOMP,
        GameSFX.CHOMP_SOFT,
        GameSFX.CHOMP_2
    };
    private long lastChompTime = 0;
    private static final long CHOMP_COOLDOWN_MS = 600;

    private SFXManager() {}

    public static SFXManager getInstance() {
        if (instance == null) instance = new SFXManager();
        return instance;
    }


    public void playSound(GameSFX gameSFX) {
        if (!sfxCache.containsKey(gameSFX)) {
            if (Gdx.files.internal(gameSFX.getFilePath()).exists()) {
                Sound sound = Gdx.audio.newSound(Gdx.files.internal(gameSFX.getFilePath()));
                sfxCache.put(gameSFX, sound);
            } else {
                System.out.println(" فایل افکت صوتی پیدا نشد: " + gameSFX.getFilePath());
                return;
            }
        }
        sfxCache.get(gameSFX).play(volume);
    }
    public void playChompSound() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastChompTime < CHOMP_COOLDOWN_MS) {
            return;
        }
        lastChompTime = currentTime;

        playSound(chompSounds[chompIndex]);
        chompIndex = (chompIndex + 1) % chompSounds.length;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return volume;
    }

    public void dispose() {
        for (Sound sound : sfxCache.values()) {
            sound.dispose();
        }
        sfxCache.clear();
    }
}
