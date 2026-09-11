package com.pvz2.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pvz2.models.core.App;
import com.pvz2.models.core.News;
import com.pvz2.models.core.User;
import com.pvz2.models.core.UserManager;
import com.pvz2.models.mupoint.MuPointLevel;
import com.pvz2.models.world.GameWorld;
import com.pvz2.view.screen.*;

import java.util.List;


public class MainMenuController implements MenuController {
    MainMenuScreen mainMenuScreen;

    public MainMenuController(MainMenuScreen mainMenuScreen) {
        this.mainMenuScreen = mainMenuScreen;
    }

    @Override
    public void changeMenu() {
    }

    public void enterMenu(String menuName) {
        switch (menuName.toLowerCase()) {
            case "play":
                mainMenuScreen.fadeAndSwitchScreen(new ChapterMenuScreen(mainMenuScreen.getGame()));
                break;
            case "news":
                mainMenuScreen.fadeAndSwitchScreen(new NewsMenuScreen(mainMenuScreen.getGame()));
                break;
            case "mu point":
                GameWorld game = MuPointLevel.createMuPointLevel();
                App.setCurrentGame(game);
                break;
            case "online room":
                mainMenuScreen.fadeAndSwitchScreen(new OnlineRoomMenuScreen(mainMenuScreen.getGame()));
                break;
        }

    }

    public boolean checkUnreadNews(){
        User user = App.getCurrentUser();
        List<News> allNews = user.getAllNews();
        for (News news : allNews) {
            if (!news.isRead()){
                return true;
            }
        }
        return false;
    }

    public Table getNews(Skin skin) {
        User user = App.getCurrentUser();
        List<News> allNews = user.getAllNews();
        Table table = new Table();
        for (News news : allNews) {
            Label titleLabel = new Label(news.getTitle(), skin, "medium_outline");
            titleLabel.setColor(Color.CYAN);
            Label dateLabel = new Label(news.getDate(), skin, "medium_outline");
            dateLabel.setColor(Color.ORANGE);
            String content = "\n" + news.getMessage() +
                "\n------------------------------------------------\n";
            Label contentLabel = new Label(content, skin);
            contentLabel.setColor(Color.BLACK);
            table.left();
            table.add(titleLabel).left().expandX().row();
            table.add(dateLabel).left().expandX().row();
            table.add(contentLabel).left().expandX().row();
            news.markAsRead();
        }
        return table;
    }


    @Override
    public void exitMenu() {
        logout();
    }

    public void logout() {
        User user = App.getCurrentUser();
        if (user == null) {
            return;
        }
        UserManager.logout();
        App.setCurrentUser(null);
        mainMenuScreen.fadeAndSwitchScreen(new SignupMenuScreen(mainMenuScreen.getGame()));
    }
}
