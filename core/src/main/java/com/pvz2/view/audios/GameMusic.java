package com.pvz2.view.audios;

public enum GameMusic {
    TITLE("Audios/Music/1-01. Title Screen.mp3"),
    HOUSE("Audios/Music/1-02. Player's House.mp3"),
    WIN("Audios/Music/Win Sound Effect.mp3"),
    LOSE("Audios/Music/Fail Sound Effect.mp3");



    private final String filePath;

    GameMusic(String filePath){
        this.filePath = filePath;
    }

    public String getFilePath(){
        return filePath;
    }
}
