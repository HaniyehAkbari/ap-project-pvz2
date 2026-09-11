package com.pvz2.view.audios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import java.util.HashMap;

public class AudioManager {
    private static AudioManager instance;

    private final HashMap<GameMusic, Music> musicCache = new HashMap<>();

    private Music currentMusic;
    private Music nextMusic;
    private boolean nextLoop;

    private enum FadeState { IDLE, FADING_OUT, FADING_IN }
    private FadeState fadeState = FadeState.IDLE;

    private float fadeSpeed = 1.2f;
    private float volume = 0.6f;
    private float currentVolume = 0f;

    private AudioManager(){}

    public static AudioManager getInstance(){
        if(instance == null) instance = new AudioManager();
        return instance;
    }


    private Music getOrLoadMusic(GameMusic gameMusic) {
        if (!musicCache.containsKey(gameMusic)) {
            if (Gdx.files.internal(gameMusic.getFilePath()).exists()) {
                Music music = Gdx.audio.newMusic(Gdx.files.internal(gameMusic.getFilePath()));
                musicCache.put(gameMusic, music);
            } else {
                System.out.println("فایل موزیک پیدا نشد: " + gameMusic.getFilePath());
                return null;
            }
        }
        return musicCache.get(gameMusic);
    }

    public void playMusic(GameMusic gameMusic, boolean loop) {
        Music target = getOrLoadMusic(gameMusic);
        if (target == null) return;

        if (currentMusic != null && currentMusic.isPlaying() && currentMusic == target) {
            return;
        }

        stopMusic();

        currentMusic = target;
        currentMusic.setLooping(loop);
        currentMusic.setVolume(volume);
        currentMusic.play();
        fadeState = FadeState.IDLE;
    }

    public void update(float delta) {
        if (fadeState == FadeState.IDLE) return;

        if (fadeState == FadeState.FADING_OUT) {
            currentVolume -= fadeSpeed * delta;
            if (currentVolume <= 0f) {
                currentVolume = 0f;
                if (currentMusic != null) {
                    currentMusic.stop();
                }

                currentMusic = nextMusic;
                nextMusic = null;

                if (currentMusic != null) {
                    currentMusic.setLooping(nextLoop);
                    currentMusic.setVolume(0f);
                    currentMusic.play();
                    fadeState = FadeState.FADING_IN;
                } else {
                    fadeState = FadeState.IDLE;
                }
            } else if (currentMusic != null) {
                currentMusic.setVolume(currentVolume);
            }
        }

        if (fadeState == FadeState.FADING_IN) {
            currentVolume += fadeSpeed * delta;
            if (currentVolume >= volume) {
                currentVolume = volume;
                fadeState = FadeState.IDLE;
            }
            if (currentMusic != null) {
                currentMusic.setVolume(currentVolume);
            }
        }
    }

    public void stopMusic() {
        fadeState = FadeState.IDLE;
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }

    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (currentMusic != null && fadeState == FadeState.IDLE) {
            currentMusic.setVolume(volume);
        }
    }


    public void dispose() {
        stopMusic();
        for (Music music : musicCache.values()) {
            music.dispose();
        }
        musicCache.clear();
        currentMusic = null;
        nextMusic = null;
    }

    public float getVolume() {
        return volume;
    }
}
