package com.pvz2.controller;


import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.Chapter;

public class ChapterMenuController implements MenuController {

    @Override
    public void changeMenu() {
    }

    @Override
    public void exitMenu() {
    }

    public String chooseChapter(Chapter chapter) {
        User user = App.getCurrentUser();
        if (user.getUnlockedChapter() <= chapter.ordinal()) {
            return "this chapter is locked!";
        }
        user.setCurrentChapter(chapter);
        return "you choose " + chapter;
    }
}
