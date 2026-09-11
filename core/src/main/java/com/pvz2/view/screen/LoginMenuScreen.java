package com.pvz2.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pvz2.Main;
import com.pvz2.controller.LoginMenuController;
import com.pvz2.controller.SignupMenuController;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.BorderedTable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LoginMenuScreen extends MenuScreen {
    TextureRegion textureRegion;
    private TextureBank textureBank;

    private final LoginMenuController controller = new LoginMenuController(this);
    private final SignupMenuController passwordRules = new SignupMenuController();

    private NinePatchDrawable errorBorderDrawable;

    private static class ValidatedField {
        Actor field;
        Label errorLabel;
        TextField.TextFieldStyle style;
        Drawable defaultBackground;
    }

    public LoginMenuScreen(Main game) {
        super(game);
        FileHandle assetsFolder = Gdx.files.internal("");
        textureBank = new TextureBank("786", assetsFolder);
        textureRegion = textureBank.region("IMAGE_UI_CALENDAR_CALENDAR_CARD_7DAY_FOODFIGHT");
    }

    @Override
    protected void buildUI() {
        errorBorderDrawable = createBorderDrawable(Color.RED, 3);
        mainStack.add(new Image(textureRegion));
        BorderedTable formTable = new BorderedTable();
        formTable.pad(25);
        TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("username");
        ValidatedField usernameVF = wrapTextField(usernameField);
        attachFocusValidation(usernameVF, () -> requiredFieldErrors(usernameField.getText(), "Please enter username."));
        TextField passwordField = createPasswordField("Password");
        ValidatedField passwordVF = wrapTextField(passwordField);
        attachFocusValidation(passwordVF, () -> requiredFieldErrors(passwordField.getText(), "Please enter password."));
        CheckBox stayLoggedInBox = new CheckBox(" Stay logged in", skin);
        Label statusLabel = createDialogErrorLabel();
        TextButton loginBtn = createButton("Login", () -> {
            boolean uOk = showFieldErrors(usernameVF,
                requiredFieldErrors(usernameField.getText(), "Please enter username."));
            boolean pOk = showFieldErrors(passwordVF,
                requiredFieldErrors(passwordField.getText(), "Please enter password."));
            if (!uOk || !pOk) return;
            new Thread(() -> {
                String result = controller.loginUser(usernameField.getText(),
                    passwordField.getText(), stayLoggedInBox.isChecked());
                Gdx.app.postRunnable(() -> {
                    boolean success = result != null && result.toLowerCase().contains("success");
                    if (success) {
                        statusLabel.setVisible(false);
                        controller.changeMenu();
                    } else {
                        statusLabel.setText(result);
                        statusLabel.setVisible(true);
                        shake(usernameField);
                        shake(passwordField);
                    }
                });
            }).start();
        });
        TextButton forgotBtn = createButton("Forgot password?", this::showForgotPasswordPopup);
        TextButton signupBtn = createButton("Don't have an account?", controller::exitMenu);
        addRow(formTable, usernameVF);
        addRow(formTable, passwordVF);
        formTable.add(stayLoggedInBox).left().padBottom(10).row();
        formTable.add(statusLabel).width(300).padBottom(10).row();
        formTable.add(loginBtn).width(150).padBottom(8).row();
        formTable.add(forgotBtn).width(170).padBottom(8).row();
        formTable.add(signupBtn).width(220).row();
        Table wrapper = new Table();
        wrapper.center().add(formTable);
        mainStack.add(wrapper);
    }

    private void showForgotPasswordPopup() {
        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.setBackground(createSolidColor(new Color(0, 0, 0, 0.6f)));
        overlay.setTouchable(Touchable.enabled);
        overlay.addListener(new ClickListener());

        BorderedTable popupBox = new BorderedTable();
        popupBox.pad(20);

        overlay.add(popupBox);
        stage.addActor(overlay);

        buildForgotStepOne(overlay, popupBox);
    }

    private void buildForgotStepOne(Table overlay, BorderedTable box) {
        box.clear();
        Label title = new Label("Forgot password", skin);

        TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("username");

        TextField emailField = new TextField("", skin);
        emailField.setMessageText("email");

        Label errorLabel = createDialogErrorLabel();

        TextButton nextBtn = createButton("Next", () -> {
            new Thread(() -> {
                String result = controller.forgetPassword(usernameField.getText(), emailField.getText());
                Gdx.app.postRunnable(() -> {
                    if (result != null && result.startsWith("Please answer security question:")) {
                        String question = result.substring(result.indexOf("\n") + 1);
                        buildForgotStepTwo(overlay, box, question);
                    } else {
                        errorLabel.setText(result);
                        errorLabel.setVisible(true);
                        shake(usernameField);
                        shake(emailField);
                    }
                });
            }).start();
        });

        TextButton cancelBtn = createButton("Cancel", overlay::remove);

        box.add(title).padBottom(15).row();
        box.add(usernameField).width(280).padBottom(10).row();
        box.add(emailField).width(280).padBottom(10).row();
        box.add(errorLabel).width(280).padBottom(10).row();

        Table buttons = new Table();
        buttons.add(cancelBtn).padRight(10);
        buttons.add(nextBtn);
        box.add(buttons);
    }

    private void buildForgotStepTwo(Table overlay, BorderedTable box, String question) {
        box.clear();
        Label title = new Label("Security question", skin);

        Label questionLabel = new Label(question, skin);
        questionLabel.setWrap(true);

        TextField answerField = new TextField("", skin);
        answerField.setMessageText("your answer");

        Label errorLabel = createDialogErrorLabel();

        TextButton nextBtn = createButton("Next", () -> {
            new Thread(() -> {
                String result = controller.answerSQ(answerField.getText());
                Gdx.app.postRunnable(() -> {
                    if ("Enter your new password:".equals(result)) {
                        buildForgotStepThree(overlay, box);
                    } else {
                        errorLabel.setText(result);
                        errorLabel.setVisible(true);
                        shake(answerField);
                    }
                });
            }).start();
        });

        TextButton cancelBtn = createButton("Cancel", overlay::remove);

        box.add(title).padBottom(15).row();
        box.add(questionLabel).width(280).padBottom(10).row();
        box.add(answerField).width(280).padBottom(10).row();
        box.add(errorLabel).width(280).padBottom(10).row();

        Table buttons = new Table();
        buttons.add(cancelBtn).padRight(10);
        buttons.add(nextBtn);
        box.add(buttons);
    }

    private void buildForgotStepThree(Table overlay, BorderedTable box) {
        box.clear();
        Label title = new Label("New password", skin);

        TextField newPasswordField = createPasswordField("new password");
        ValidatedField passwordVF = wrapTextField(newPasswordField);

        TextField confirmField = createPasswordField("confirm new password");
        ValidatedField confirmVF = wrapTextField(confirmField);

        attachFocusValidation(passwordVF, () -> passwordRules.validatePasswordStrength(newPasswordField.getText()));
        attachFocusValidation(confirmVF, () ->
            passwordRules.getPasswordErrors(newPasswordField.getText(), confirmField.getText()));

        Label errorLabel = createDialogErrorLabel();

        TextButton submitBtn = createButton("Change password", () -> {
            boolean pOk = showFieldErrors(passwordVF, passwordRules.validatePasswordStrength
                (newPasswordField.getText()));
            boolean cOk = showFieldErrors(confirmVF, passwordRules.getPasswordErrors(newPasswordField.getText(),
                confirmField.getText()));
            if (!pOk || !cOk) return;

            new Thread(() -> {
                String result = controller.newPassword(newPasswordField.getText());
                Gdx.app.postRunnable(() -> {
                    if ("Your password changed successfully.".equals(result)) {
                        buildForgotStepDone(overlay, box);
                    } else {
                        errorLabel.setText(result);
                        errorLabel.setVisible(true);
                        shake(newPasswordField);
                    }
                });
            }).start();
        });

        box.add(title).padBottom(15).row();
        addRow(box, passwordVF);
        addRow(box, confirmVF);
        box.add(errorLabel).width(280).padBottom(10).row();
        box.add(submitBtn).width(200).row();
    }

    private void buildForgotStepDone(Table overlay, BorderedTable box) {
        box.clear();
        Label doneLabel = new Label("Your password changed successfully.", skin);
        doneLabel.setWrap(true);

        TextButton closeBtn = createButton("Close", overlay::remove);

        box.add(doneLabel).width(280).padBottom(15).row();
        box.add(closeBtn).width(150).row();
    }

    // --- Helper Methods to Reduce Boilerplate ---

    private TextButton createButton(String text, Runnable action) {
        TextButton btn = new TextButton(text, skin);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return btn;
    }

    private TextField createPasswordField(String message) {
        TextField field = new TextField("", skin);
        field.setPasswordMode(true);
        field.setPasswordCharacter('*');
        field.setMessageText(message);
        return field;
    }

    // --------------------------------------------

    private Label createDialogErrorLabel() {
        Label label = new Label("", skin);
        label.setColor(Color.RED);
        label.setFontScale(0.8f);
        label.setWrap(true);
        label.setVisible(false);
        return label;
    }

    private List<String> requiredFieldErrors(String value, String message) {
        List<String> errors = new ArrayList<>();
        if (value == null || value.trim().isEmpty()) {
            errors.add(message);
        }
        return errors;
    }

    private ValidatedField wrapTextField(TextField field) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(field.getStyle());
        field.setStyle(style);

        ValidatedField vf = new ValidatedField();
        vf.field = field;
        vf.style = style;
        vf.defaultBackground = style.background;
        vf.errorLabel = createDialogErrorLabel();
        return vf;
    }

    private void attachFocusValidation(ValidatedField vf, Supplier<List<String>> validator) {
        vf.field.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) return;
                showFieldErrors(vf, validator.get());
            }
        });
    }

    private boolean showFieldErrors(ValidatedField vf, List<String> errors) {
        boolean valid = errors == null || errors.isEmpty();
        vf.style.background = valid ? vf.defaultBackground : errorBorderDrawable;

        if (valid) {
            vf.errorLabel.setVisible(false);
            vf.errorLabel.setText("");
        } else {
            vf.errorLabel.setText(String.join("\n", errors));
            vf.errorLabel.setVisible(true);
            shake(vf.field);
        }
        return valid;
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

    private void addRow(Table table, ValidatedField vf) {
        table.add(vf.field).width(300).padBottom(2).row();
        table.add(vf.errorLabel).width(300).padBottom(10).left().row();
    }

    private NinePatchDrawable createBorderDrawable(Color borderColor, int thickness) {
        int size = thickness * 3;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        pixmap.setColor(borderColor);
        for (int i = 0; i < thickness; i++) {
            pixmap.drawRectangle(i, i, size - i * 2, size - i * 2);
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        NinePatch patch = new NinePatch(texture, thickness, thickness, thickness, thickness);
        return new NinePatchDrawable(patch);
    }

    private Drawable createSolidColor(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }
}
