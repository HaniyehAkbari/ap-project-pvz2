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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.pvz2.Main;
import com.pvz2.controller.SignupMenuController;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.BorderedTable;

import java.util.List;
import java.util.function.Supplier;

public class SignupMenuScreen extends MenuScreen {

    TextureRegion textureRegion;
    private TextureBank textureBank;

    private SignupMenuController controller;
    private NinePatchDrawable errorBorderDrawable;

    private static class ValidatedField {
        Actor field;
        Label errorLabel;
        Object style;
        Object defaultBackground;
    }

    public SignupMenuScreen(Main game) {
        super(game);

        FileHandle assetsFolder = Gdx.files.internal("");
        textureBank = new TextureBank("786", assetsFolder);
        textureRegion = textureBank.region("IMAGE_UI_CALENDAR_CALENDAR_CARD_7DAY_FOODFIGHT");
    }

    @Override
    protected void buildUI() {
        controller = new SignupMenuController(this);
        errorBorderDrawable = createBorderDrawable(Color.RED, 3);

        setupBackground();

        // Create all validated fields
        ValidatedField usernameVF = createUsernameField();
        ValidatedField nicknameVF = createNicknameField();
        ValidatedField emailVF = createEmailField();
        ValidatedField passwordVF = createPasswordField();
        ValidatedField passwordConfirmVF = createPasswordConfirmField(passwordVF);
        ValidatedField genderVF = createGenderField();
        ValidatedField questionVF = createQuestionField();
        ValidatedField answerVF = createAnswerField();
        ValidatedField answerConfirmVF = createAnswerConfirmField(questionVF, answerVF);

        // Build form tables and buttons
        Table formTable = buildFormTable(usernameVF, nicknameVF, emailVF,
            passwordVF, passwordConfirmVF, genderVF);
        Table securityTable = buildSecurityTable(questionVF, answerVF, answerConfirmVF);

        TextButton signupBtn = createSignupButton(usernameVF, nicknameVF, emailVF,
            passwordVF, passwordConfirmVF, genderVF,
            questionVF, answerVF, answerConfirmVF);
        TextButton loginBtn = createLoginButton();

        securityTable.add(signupBtn).width(150).padTop(10).row();
        securityTable.add(loginBtn).width(150).padTop(10);

        Table wrapper = new Table();
        wrapper.center();
        wrapper.add(formTable).top().padRight(20);
        wrapper.add(securityTable).top();

        mainStack.add(wrapper);
    }

    // ========== Setup ==========
    private void setupBackground() {
        Image backgroundImage = new Image(textureRegion);
        mainStack.add(backgroundImage);
    }

    // ========== Field Creators ==========
    private ValidatedField createUsernameField() {
        TextField field = new TextField("", skin);
        field.setMessageText("username");
        ValidatedField vf = wrapTextField(field);
        attachFocusValidation(vf, () -> controller.getUsernameErrors(field.getText()));
        return vf;
    }

    private ValidatedField createNicknameField() {
        TextField field = new TextField("", skin);
        field.setMessageText("nickname");
        ValidatedField vf = wrapTextField(field);
        attachFocusValidation(vf, () -> controller.getNicknameErrors(field.getText()));
        return vf;
    }

    private ValidatedField createEmailField() {
        TextField field = new TextField("", skin);
        field.setMessageText("email");
        ValidatedField vf = wrapTextField(field);
        attachFocusValidation(vf, () -> controller.getEmailErrors(field.getText()));
        return vf;
    }

    private ValidatedField createPasswordField() {
        TextField field = new TextField("", skin);
        field.setPasswordMode(true);
        field.setPasswordCharacter('*');
        field.setMessageText("Password");
        ValidatedField vf = wrapTextField(field);
        attachFocusValidation(vf, () -> controller.validatePasswordStrength(field.getText()));
        return vf;
    }

    private ValidatedField createPasswordConfirmField(ValidatedField passwordVF) {
        TextField field = new TextField("", skin);
        field.setPasswordMode(true);
        field.setPasswordCharacter('*');
        field.setMessageText("Confirm password");
        ValidatedField vf = wrapTextField(field);
        TextField passwordField = (TextField) passwordVF.field;
        attachFocusValidation(vf, () -> controller.getPasswordErrors(passwordField.getText(), field.getText()));
        return vf;
    }

    private ValidatedField createGenderField() {
        SelectBox<String> box = new SelectBox<>(skin);
        box.setItems("male", "female");
        ValidatedField vf = wrapSelectBox(box);
        attachChangeValidation(vf, box, () -> controller.getGenderErrors(box.getSelected()));
        return vf;
    }

    private ValidatedField createQuestionField() {
        SelectBox<String> box = new SelectBox<>(skin);
        Array<String> items = new Array<>();
        for (String q : controller.getQuestions()) items.add(q);
        box.setItems(items);
        return wrapSelectBox(box);
    }

    private ValidatedField createAnswerField() {
        TextField field = new TextField("", skin);
        field.setMessageText("answer");
        return wrapTextField(field);
    }

    private ValidatedField createAnswerConfirmField(ValidatedField questionVF, ValidatedField answerVF) {
        TextField field = new TextField("", skin);
        field.setMessageText("confirm answer");
        ValidatedField vf = wrapTextField(field);

        SelectBox<String> questionBox = (SelectBox<String>) questionVF.field;
        TextField answerField = (TextField) answerVF.field;

        Supplier<List<String>> answerValidator = () -> controller.getAnswerErrors(
            questionBox.getSelectedIndex() + 1,
            answerField.getText());

        Supplier<List<String>> answerConfirmValidator = () -> controller.getAnswerConfirmErrors(
            questionBox.getSelectedIndex() + 1,
            answerField.getText(),
            field.getText());

        attachFocusValidation(answerVF, answerValidator);
        attachFocusValidation(vf, answerConfirmValidator);
        return vf;
    }

    // ========== Wrappers ==========
    private ValidatedField wrapTextField(TextField field) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(field.getStyle());
        field.setStyle(style);

        ValidatedField vf = new ValidatedField();
        vf.field = field;
        vf.style = style;
        vf.defaultBackground = style.background;
        vf.errorLabel = createErrorLabel();
        return vf;
    }

    private ValidatedField wrapSelectBox(SelectBox<String> box) {
        SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle(box.getStyle());
        box.setStyle(style);

        ValidatedField vf = new ValidatedField();
        vf.field = box;
        vf.style = style;
        vf.defaultBackground = style.background;
        vf.errorLabel = createErrorLabel();
        return vf;
    }

    private Label createErrorLabel() {
        Label label = new Label("", skin);
        label.setColor(Color.RED);
        label.setFontScale(0.8f);
        label.setWrap(true);
        label.setVisible(false);
        return label;
    }

    // ========== Validation Attachments ==========
    private void attachFocusValidation(ValidatedField vf, Supplier<List<String>> validator) {
        vf.field.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) return;
                showFieldErrors(vf, validator.get());
            }
        });
    }

    private void attachChangeValidation(ValidatedField vf, SelectBox<String> box, Supplier<List<String>> validator) {
        box.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showFieldErrors(vf, validator.get());
            }
        });
    }

    // ========== Error Handling ==========
    private boolean showFieldErrors(ValidatedField vf, List<String> errors) {
        boolean valid = errors == null || errors.isEmpty();

        if (vf.style instanceof TextField.TextFieldStyle) {
            TextField.TextFieldStyle s = (TextField.TextFieldStyle) vf.style;
            s.background = valid ? (com.badlogic.gdx.scenes.scene2d.utils.Drawable)
                vf.defaultBackground : errorBorderDrawable;
        } else if (vf.style instanceof SelectBox.SelectBoxStyle) {
            SelectBox.SelectBoxStyle s = (SelectBox.SelectBoxStyle) vf.style;
            s.background = valid ? (com.badlogic.gdx.scenes.scene2d.utils.Drawable)
                vf.defaultBackground : errorBorderDrawable;
        }

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

    // ========== Table Builders ==========
    private Table buildFormTable(ValidatedField... fields) {
        BorderedTable table = new BorderedTable();
        table.pad(25);
        for (ValidatedField vf : fields) {
            addRow(table, vf);
        }
        return table;
    }

    private Table buildSecurityTable(ValidatedField questionVF,
                                     ValidatedField answerVF, ValidatedField answerConfirmVF) {
        BorderedTable table = new BorderedTable();
        table.pad(25);
        addRow(table, questionVF);
        addRow(table, answerVF);
        addRow(table, answerConfirmVF);
        return table;
    }

    private void addRow(Table table, ValidatedField vf) {
        table.add(vf.field).width(300).padBottom(2).row();
        table.add(vf.errorLabel).width(300).padBottom(10).left().row();
    }

    private TextButton createSignupButton(ValidatedField usernameVF, ValidatedField nicknameVF,
                                          ValidatedField emailVF, ValidatedField passwordVF,
                                          ValidatedField passwordConfirmVF, ValidatedField genderVF,
                                          ValidatedField questionVF, ValidatedField answerVF,
                                          ValidatedField answerConfirmVF) {
        TextButton btn = new TextButton("Sign Up", skin);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextField usernameField = (TextField) usernameVF.field;
                TextField nicknameField = (TextField) nicknameVF.field;
                TextField emailField = (TextField) emailVF.field;
                TextField passwordField = (TextField) passwordVF.field;
                TextField passwordConfirmField = (TextField) passwordConfirmVF.field;
                SelectBox<String> genderBox = (SelectBox<String>) genderVF.field;
                SelectBox<String> questionBox = (SelectBox<String>) questionVF.field;
                TextField answerField = (TextField) answerVF.field;
                TextField answerConfirmField = (TextField) answerConfirmVF.field;
                boolean usernameOk = showFieldErrors(usernameVF, controller.getUsernameErrors(usernameField.getText()));
                boolean nicknameOk = showFieldErrors(nicknameVF, controller.getNicknameErrors(nicknameField.getText()));
                boolean emailOk = showFieldErrors(emailVF, controller.getEmailErrors(emailField.getText()));
                boolean passwordOk = showFieldErrors(passwordVF,
                    controller.validatePasswordStrength(passwordField.getText()));
                boolean passwordConfirmOk = showFieldErrors(passwordConfirmVF,
                    controller.getPasswordErrors(passwordField.getText(), passwordConfirmField.getText()));
                boolean genderOk = showFieldErrors(genderVF, controller.getGenderErrors(genderBox.getSelected()));
                Supplier<List<String>> answerValidator = () -> controller.getAnswerErrors(
                    questionBox.getSelectedIndex() + 1, answerField.getText());
                Supplier<List<String>> answerConfirmValidator = () -> controller.getAnswerConfirmErrors(
                    questionBox.getSelectedIndex() + 1, answerField.getText(), answerConfirmField.getText());
                boolean answerOk = showFieldErrors(answerVF, answerValidator.get());
                boolean answerConfirmOk = showFieldErrors(answerConfirmVF, answerConfirmValidator.get());
                boolean allOk = usernameOk && nicknameOk && emailOk && passwordOk
                    && passwordConfirmOk && genderOk && answerOk && answerConfirmOk;
                if (!allOk) return;
                new Thread(() -> {
                    String result = controller.createUser(usernameField.getText(), passwordField.getText(),
                        nicknameField.getText(), emailField.getText(), genderBox.getSelected(),
                        questionBox.getSelected(), answerField.getText());
                    Gdx.app.postRunnable(() -> {
                        if (result != null && !result.equals("Error: could not reach server.") &&
                            !result.equals("Username already exists.")) {
                            controller.changeMenu();
                        }
                        else {
                            addToast("Error", result);
                        }
                    });
                }).start();
            }
        });
        return btn;
    }

    private TextButton createLoginButton() {
        TextButton btn = new TextButton("Login", skin);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.changeMenu();
            }
        });
        return btn;
    }

    // ========== Border Drawable ==========
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

    @Override
    public void render(float delta) {
        super.render(delta);
    }
}
