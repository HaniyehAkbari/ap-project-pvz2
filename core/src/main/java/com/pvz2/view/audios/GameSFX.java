package com.pvz2.view.audios;

public enum GameSFX {
    BUTTON_CLICK("Audios/SFX/1-18. SFX buttonclick.ogg"),
    CHERRY_BOMB("Audios/SFX/1-21. SFX cherrybomb.ogg"),
    CHIME("Audios/SFX/1-22. SFX chime.ogg"),
    CHOMP("Audios/SFX/1-23. SFX chomp.ogg"),
    CHOMP_2("Audios/SFX/1-24. SFX chomp2.ogg"),
    CHOMP_SOFT("Audios/SFX/1-25. SFX chompsoft.ogg"),
    FROZEN("Audios/SFX/1-37. SFX frozen.ogg"),
    GRAVE_BUSTER_CHOMP("Audios/SFX/1-41. SFX gravebusterchomp.ogg"),
    JALAPENO("Audios/SFX/1-50. SFX jalapeno.ogg"),
    LAWNMOWER("Audios/SFX/1-55. SFX lawnmower.ogg"),
    PLANT("Audios/SFX/1-66. SFX plant.ogg"),
    SEED_LIFT("Audios/SFX/1-86. SFX seedlift.ogg"),
    VASE_BREAKING("Audios/SFX/1-104. SFX vase breaking.ogg"),
    HUGE_WAVE("Audios/SFX/03. hugewave.ogg"),
    PAUSE("Audios/SFX/24. SFX pause.ogg"),
    SPLAT("Audios/SFX/splat3.ogg"),
    LOBBED("Audios/SFX/butter (1).ogg");

    private final String filePath;

    GameSFX(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
