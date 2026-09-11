package com.pvz2.view.table;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.pvz2.Main;
import com.pvz2.models.world.GameWorld;
import pvz.libpvz.pam.PamPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrazyDaveOverlay extends WidgetGroup {
    private Runnable onCompleteAction;
    private final Main game;
    private final GameWorld world;
    private final PamPlayer davePam;
    private final PamPlayer pennyPam;
    private final List<String> dialogs;

    private int currentDialogIndex = 0;
    private boolean isStarted = false;
    private boolean finished = false;
    private boolean isLeaving = false;

    private float stateTime = 0f;
    private String currentAnim = "anim_enter";
    private String currentSpeaker = "DAVE";

    private boolean hasDave = false;
    private boolean hasPenny = false;

    private final Image bubbleImage;
    private final TextureRegionDrawable daveBubbleDrawable;
    private final TextureRegionDrawable pennyBubbleDrawable;

    private final Label textLabel;
    private final Label tapToContinueLabel;

    private boolean isTyping = false;
    private float typeTimer = 0f;
    private String targetText = "";

    private static final float DAVE_X = 145f;
    private static final float DAVE_Y = 310f;
    private static final float DAVE_SCALE = 0.36f;

    private static final float PENNY_X = 1780f;
    private static final float PENNY_Y = 160f;
    private static final float PENNY_SCALE = 0.85f;

    private static final String DAVE_PAM_PATH = "768/INITIAL/CRAZYDAVE/CRAZYDAVE/CRAZYDAVE.PAM";
    private static final String PENNY_PAM_PATH =
        "768/INITIAL/CRAZYDAVE/DAVEWINNIE_NARRATIONICONS/DAVEWINNIE_NARRATIONICONS.PAM";

    private final String[] talkAnimations = {
        "anim_smalltalk",
        "anim_mediumtalk",
        "anim_blahblah",
        "anim_crazyblahblah",
    };

    public CrazyDaveOverlay(Main game, List<String> dialogs, GameWorld world) {
        this.game = game;
        this.dialogs = new ArrayList<>(dialogs != null ? dialogs : Collections.emptyList());
        this.world = world;

        setFillParent(true);
        setVisible(false);

        davePam = createPamPlayer(DAVE_PAM_PATH);
        pennyPam = createPamPlayer(PENNY_PAM_PATH);

        TextureRegion bubbleReg = game.textureBank.region("IMAGE_STORE_SPEECHBUBBLE2");
        daveBubbleDrawable = new TextureRegionDrawable(bubbleReg);
        pennyBubbleDrawable = createFlippedBubbleDrawable(bubbleReg);

        bubbleImage = new Image(daveBubbleDrawable);
        bubbleImage.setVisible(false);

        textLabel = createTextLabel(bubbleReg);
        tapToContinueLabel = createTapLabel();

        addActor(bubbleImage);
        addActor(textLabel);
        addActor(tapToContinueLabel);

        setupClickListener();
    }


    private PamPlayer createPamPlayer(String path) {
        PamPlayer pam = new PamPlayer(game.textureBank, Gdx.files.internal(""));
        pam.loadAsync(path, null);
        return pam;
    }

    private TextureRegionDrawable createFlippedBubbleDrawable(TextureRegion baseRegion) {
        TextureRegion flippedReg = new TextureRegion(baseRegion);
        flippedReg.flip(true, false);
        return new TextureRegionDrawable(flippedReg);
    }

    private Label createTextLabel(TextureRegion bubbleReg) {
        Label.LabelStyle baseStyle = game.skin.get(Label.LabelStyle.class);
        Label.LabelStyle textStyle = new Label.LabelStyle(baseStyle);
        textStyle.fontColor = Color.BLACK;

        if (textStyle.font != null && textStyle.font.getRegion() != null &&
            textStyle.font.getRegion().getTexture() != null) {
            textStyle.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        }

        Label label = new Label("", textStyle);
        label.setFontScale(1.25f);
        label.setWrap(true);
        label.setWidth(bubbleReg != null ? bubbleReg.getRegionWidth() - 65 : 270);
        label.setAlignment(Align.topLeft);
        label.setVisible(false);
        return label;
    }

    private Label createTapLabel() {
        Label.LabelStyle tapStyle = new Label.LabelStyle(game.skin.get(Label.LabelStyle.class));
        tapStyle.fontColor = Color.DARK_GRAY;
        Label label = new Label("TAP TO CONTINUE", tapStyle);
        label.setFontScale(0.8f);
        label.setVisible(false);
        return label;
    }

    private void setupClickListener() {
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleOverlayClick();
            }
        });
    }

    private void handleOverlayClick() {
        if (finished || !isStarted || isLeaving || currentAnim.equals("anim_enter")) return;

        if (isTyping) {
            isTyping = false;
            textLabel.setText(targetText);
            currentAnim = "anim_idle";
            tapToContinueLabel.setVisible(true);
        } else {
            tapToContinueLabel.setVisible(false);
            currentDialogIndex++;
            if (currentDialogIndex >= dialogs.size()) {
                startLeaving();
            } else {
                startTyping(dialogs.get(currentDialogIndex));
            }
        }
    }


    private void checkSpeakersInDialogs() {
        hasDave = false;
        hasPenny = false;
        for (String d : dialogs) {
            if (d.startsWith("PENNY:")) {
                hasPenny = true;
            } else if (d.startsWith("DAVE:")) {
                hasDave = true;
            } else {
                hasDave = true;
            }
        }
    }

    public void startPresentation() {
        if (dialogs.isEmpty()) return;

        checkSpeakersInDialogs();

        this.currentDialogIndex = 0;
        this.isTyping = false;
        this.typeTimer = 0f;
        this.targetText = "";
        this.textLabel.setText("");
        this.bubbleImage.setVisible(false);
        this.textLabel.setVisible(false);
        this.tapToContinueLabel.setVisible(false);

        this.isStarted = true;
        this.finished = false;
        this.isLeaving = false;
        this.setVisible(true);
        this.world.setDialogActive(true);
        this.stateTime = 0f;
        this.currentAnim = "anim_enter";
    }

    private void startTyping(String rawText) {
        tapToContinueLabel.setVisible(false);

        if (rawText.startsWith("PENNY:")) {
            currentSpeaker = "PENNY";
            targetText = rawText.substring(6);
        } else if (rawText.startsWith("DAVE:")) {
            currentSpeaker = "DAVE";
            targetText = rawText.substring(5);
        } else {
            currentSpeaker = "DAVE";
            targetText = rawText;
        }

        typeTimer = 0f;
        isTyping = true;
        textLabel.setText("");

        currentAnim = talkAnimations[MathUtils.random(talkAnimations.length - 1)];
        bubbleImage.setVisible(true);
        textLabel.setVisible(true);

        if (currentSpeaker.equals("DAVE")) {
            bubbleImage.setDrawable(daveBubbleDrawable);
            bubbleImage.setPosition(DAVE_X + 30f, DAVE_Y + 20f);
        } else {
            bubbleImage.setDrawable(pennyBubbleDrawable);
            bubbleImage.setPosition(PENNY_X - bubbleImage.getWidth() - 40f, PENNY_Y + 60f);
        }

        textLabel.setPosition(bubbleImage.getX() + 32, bubbleImage.getY() + bubbleImage.getHeight() - 30);

        tapToContinueLabel.setPosition(
            bubbleImage.getX() + (bubbleImage.getWidth() - tapToContinueLabel.getPrefWidth()) / 2f,
            bubbleImage.getY() + 35f
        );
    }

    private void startLeaving() {
        isLeaving = true;
        currentAnim = "anim_leave";
        stateTime = 0f;
        bubbleImage.setVisible(false);
        textLabel.setVisible(false);
        tapToContinueLabel.setVisible(false);
    }

    private void finishPresentation() {
        finished = true;
        setVisible(false);
        world.setDialogActive(false);

        if (onCompleteAction != null) {
            onCompleteAction.run();
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (finished || !isStarted) return null;
        if (touchable && getTouchable() == Touchable.disabled) return null;
        if (!isVisible()) return null;
        return this;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (finished || !isStarted) return;
        stateTime += delta;

        if (isLeaving) {
            if (stateTime > 1.2f) {
                finishPresentation();
            }
            return;
        }

        if (currentAnim.equals("anim_enter") && stateTime > 1.2f) {
            startTyping(dialogs.get(currentDialogIndex));
        }

        if (isTyping) {
            typeTimer += delta;
            int charsToShow = (int) (typeTimer * 30);

            if (charsToShow >= targetText.length()) {
                charsToShow = targetText.length();
                isTyping = false;
                currentAnim = "anim_idle";
                tapToContinueLabel.setVisible(true);
            }
            textLabel.setText(targetText.substring(0, charsToShow));
        }
    }

    public void startPresentation(List<String> newDialogs, Runnable onComplete) {
        if (newDialogs == null || newDialogs.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        this.dialogs.clear();
        this.dialogs.addAll(newDialogs);
        this.onCompleteAction = onComplete;

        checkSpeakersInDialogs();

        this.currentDialogIndex = 0;
        this.isTyping = false;
        this.typeTimer = 0f;
        this.targetText = "";
        this.textLabel.setText("");
        this.bubbleImage.setVisible(false);
        this.textLabel.setVisible(false);
        this.tapToContinueLabel.setVisible(false);

        this.isStarted = true;
        this.finished = false;
        this.isLeaving = false;
        this.setVisible(true);
        this.world.setDialogActive(true);
        this.stateTime = 0f;
        this.currentAnim = "anim_enter";
    }

    public void startPresentation(Runnable onComplete) {
        if (dialogs.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }
        this.onCompleteAction = onComplete;
        startPresentation();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (finished || !isStarted) return;

        Matrix4 oldMatrix = batch.getTransformMatrix().cpy();

        try {
            if (hasDave) {
                String daveAnimToPlay = currentSpeaker.equals("DAVE") ? currentAnim : "anim_idle";
                if (currentAnim.equals("anim_enter") || currentAnim.equals("anim_leave")) {
                    daveAnimToPlay = currentAnim;
                }

                Matrix4 newMatrix = new Matrix4(oldMatrix);
                newMatrix.translate(DAVE_X, DAVE_Y, 0);
                newMatrix.scale(DAVE_SCALE, DAVE_SCALE, 1f);
                newMatrix.translate(-DAVE_X, -DAVE_Y, 0);
                batch.setTransformMatrix(newMatrix);

                davePam.draw(batch, DAVE_PAM_PATH, daveAnimToPlay, stateTime, DAVE_X, DAVE_Y, true);
            }

            if (hasPenny) {
                Matrix4 newMatrix = new Matrix4(oldMatrix);
                newMatrix.translate(PENNY_X, PENNY_Y, 0);
                newMatrix.scale(PENNY_SCALE, PENNY_SCALE, 1f);
                newMatrix.translate(-PENNY_X, -PENNY_Y, 0);
                batch.setTransformMatrix(newMatrix);

                String pennyAnimToPlay = (currentSpeaker.equals("PENNY") && isTyping) ? "winnie" : "open";

                pennyPam.draw(batch, PENNY_PAM_PATH, pennyAnimToPlay, stateTime, PENNY_X, PENNY_Y, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            batch.setTransformMatrix(oldMatrix);
        }

        super.draw(batch, parentAlpha);
    }

    public boolean isStarted() { return isStarted; }
}
