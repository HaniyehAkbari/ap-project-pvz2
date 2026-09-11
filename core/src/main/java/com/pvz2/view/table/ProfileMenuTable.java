package com.pvz2.view.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.pvz2.Main;
import com.pvz2.controller.ProfileMenuController;


public class ProfileMenuTable extends Table {

    private static final Color HEADER_COLOR = new Color(0.35f, 0.22f, 0.08f, 1f);
    private static final Color SUCCESS_COLOR = new Color(0.15f, 0.45f, 0.15f, 1f);
    private static final Color ERROR_COLOR = new Color(0.7f, 0.1f, 0.1f, 1f);

    private final ProfileMenuController controller = new ProfileMenuController();
    private final Skin skin;
    private final Main game;

    private Label statsLabel;

    public ProfileMenuTable(Main game, Skin skin) {
        this.game = game;
        this.skin = skin;
        build();
    }

    private void build() {
        pad(5);

        Table titleRow = new Table();
        TextureRegion avatarReg = game.textureBank.region("IMAGE_UI_MAINMENU_MM_PLAYERICON");
        if (avatarReg != null) {
            titleRow.add(new Image(avatarReg)).size(40, 40).padRight(10);
        }
        Label title = new Label("Profile", skin, "big");
        titleRow.add(title);
        add(titleRow).left().padBottom(12).row();

        statsLabel = new Label(controller.showInfo(), skin);
        statsLabel.setWrap(true);
        statsLabel.setAlignment(Align.left);
        statsLabel.setColor(Color.BLACK);
        add(statsLabel).width(520).left().padBottom(18).row();

        addDivider();
        add(buildUsernameSection()).padTop(12).row();

        addDivider();
        add(buildNicknameSection()).padTop(12).row();

        addDivider();
        add(buildEmailSection()).padTop(12).row();

        addDivider();
        add(buildPasswordSection()).padTop(12).padBottom(6).row();
    }

    private Table buildUsernameSection() {
        Table t = new Table();

        Label label = new Label("Change Username", skin, "medium");
        label.setColor(HEADER_COLOR);
        t.add(label).left().padBottom(6).row();

        TextField field = new TextField(controller.getCurrentUsername(), skin);
        field.setMessageText("new username");

        Label status = createStatusLabel();

        TextButton saveBtn = new TextButton("Save", skin);
        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String result = controller.changeUsername(field.getText());
                boolean success = applyStatus(status, result);
                if (success) {
                    refreshStats();
                } else {
                    shake(field);
                }
            }
        });

        Table row = new Table();
        row.add(field).width(380).padRight(10);
        row.add(saveBtn).width(100);

        t.add(row).left().row();
        t.add(status).width(520).left().padTop(4).row();

        return t;
    }

    private Table buildNicknameSection() {
        Table t = new Table();

        Label label = new Label("Change Nickname", skin, "medium");
        label.setColor(HEADER_COLOR);
        t.add(label).left().padBottom(6).row();

        TextField field = new TextField(controller.getCurrentNickname(), skin);
        field.setMessageText("new nickname");

        Label status = createStatusLabel();

        TextButton saveBtn = new TextButton("Save", skin);
        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String result = controller.changeNickname(field.getText());
                boolean success = applyStatus(status, result);
                if (success) {
                    refreshStats();
                } else {
                    shake(field);
                }
            }
        });

        Table row = new Table();
        row.add(field).width(380).padRight(10);
        row.add(saveBtn).width(100);

        t.add(row).left().row();
        t.add(status).width(520).left().padTop(4).row();

        return t;
    }

    private Table buildEmailSection() {
        Table t = new Table();

        Label label = new Label("Change Email", skin, "medium");
        label.setColor(HEADER_COLOR);
        t.add(label).left().padBottom(6).row();

        TextField field = new TextField(controller.getCurrentEmail(), skin);
        field.setMessageText("new email");

        Label status = createStatusLabel();

        TextButton saveBtn = new TextButton("Save", skin);
        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String result = controller.changeEmail(field.getText());
                boolean success = applyStatus(status, result);
                if (!success) {
                    shake(field);
                }
            }
        });

        Table row = new Table();
        row.add(field).width(380).padRight(10);
        row.add(saveBtn).width(100);

        t.add(row).left().row();
        t.add(status).width(520).left().padTop(4).row();

        return t;
    }

    private Table buildPasswordSection() {
        Table t = new Table();

        Label label = new Label("Change Password", skin, "medium");
        label.setColor(HEADER_COLOR);
        t.add(label).left().padBottom(6).row();

        TextField currentField = new TextField("", skin);
        currentField.setPasswordMode(true);
        currentField.setPasswordCharacter('*');
        currentField.setMessageText("current password");

        TextField newField = new TextField("", skin);
        newField.setPasswordMode(true);
        newField.setPasswordCharacter('*');
        newField.setMessageText("new password");

        Label status = createStatusLabel();

        TextButton saveBtn = new TextButton("Change Password", skin, "purple");
        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String result = controller.changePassword(currentField.getText(), newField.getText());
                boolean success = applyStatus(status, result);
                if (success) {
                    currentField.setText("");
                    newField.setText("");
                } else {
                    shake(currentField);
                    shake(newField);
                }
            }
        });

        t.add(currentField).width(490).padBottom(8).row();
        t.add(newField).width(490).padBottom(8).row();
        t.add(saveBtn).width(260).padBottom(4).row();
        t.add(status).width(520).left().padTop(4).row();

        return t;
    }

    private void refreshStats() {
        if (statsLabel != null) {
            statsLabel.setText(controller.showInfo());
        }
    }

    private Label createStatusLabel() {
        Label label = new Label("", skin);
        label.setFontScale(0.8f);
        label.setWrap(true);
        label.setVisible(false);
        return label;
    }

    private boolean applyStatus(Label status, String message) {
        boolean success = message != null && message.toLowerCase().contains("changed");
        status.setColor(success ? SUCCESS_COLOR : ERROR_COLOR);
        status.setText(message);
        status.setVisible(true);
        return success;
    }

    private void shake(Actor actor) {
        actor.clearActions();
        final float originalX = actor.getX();
        actor.addAction(Actions.sequence(
            Actions.moveBy(8, 0, 0.04f),
            Actions.moveBy(-16, 0, 0.04f),
            Actions.moveBy(16, 0, 0.04f),
            Actions.moveBy(-16, 0, 0.04f),
            Actions.moveBy(8, 0, 0.04f),
            Actions.run(() -> actor.setX(originalX))
        ));
    }

    private void addDivider() {
        Image divider = new Image(createSolidColor(new Color(0.55f, 0.42f, 0.22f, 0.7f)));
        add(divider).growX().height(2).padTop(8).padBottom(2).row();
    }

    private Drawable createSolidColor(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
