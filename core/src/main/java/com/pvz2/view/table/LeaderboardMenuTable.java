package com.pvz2.view.table;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.pvz2.Main;
import com.pvz2.controller.LeaderboardMenuController;
import com.pvz2.models.enums.LeaderboardSortField;
import com.pvz2.models.network.messages.LeaderboardEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeaderboardMenuTable extends Table {

    private static final Color HEADER_COLOR = new Color(0.35f, 0.22f, 0.08f, 1f);
    private static final Color HEADER_TEXT_COLOR = new Color(0.99f, 0.93f, 0.78f, 1f);
    private static final Color HEADER_BG_COLOR = new Color(0.45f, 0.28f, 0.11f, 0.92f);
    private static final Color CONTROLS_BG_COLOR = new Color(0.93f, 0.85f, 0.65f, 0.55f);
    private static final Color ROW_STRIPE_COLOR = new Color(0.62f, 0.47f, 0.24f, 0.16f);
    private static final Color DIVIDER_COLOR = new Color(0.55f, 0.42f, 0.22f, 0.7f);
    private static final Color HEADER_DIVIDER_COLOR = new Color(0.99f, 0.93f, 0.78f, 0.35f);
    private static final Color GOLD = new Color(1f, 0.84f, 0f, 1f);
    private static final Color SILVER = new Color(0.75f, 0.75f, 0.78f, 1f);
    private static final Color BRONZE = new Color(0.80f, 0.50f, 0.20f, 1f);


    private static final float COL_RANK = 45;
    private static final float COL_USERNAME = 140;
    private static final float COL_STAGE = 150;
    private static final float COL_MINI = 90;
    private static final float COL_DAILY = 80;
    private static final float COL_NORMAL = 90;
    private static final float COL_SCORE = 95;
    private static final float COL_DIVIDER_W = 1;
    private static final float COL_DIVIDER_PAD = 4;

    private final Skin skin;

    private Table rowsTable;
    private SelectBox<LeaderboardSortField> sortBox;
    private TextButton orderBtn;
    private boolean ascending = false;

    private final LeaderboardMenuController controller = new LeaderboardMenuController();
    private List<LeaderboardEntry> currentEntries = new ArrayList<>();

    public LeaderboardMenuTable(Main game, Skin skin) {
        this.skin = skin;
        build();
    }

    private void refreshRows() {
        new Thread(() -> {
            List<LeaderboardEntry> entries = controller.loadLeaderboard();
            Gdx.app.postRunnable(() -> {
                currentEntries = entries;
                rebuildRows();
            });
        }).start();
    }

    private void rebuildRows() {
        rowsTable.clear();

        LeaderboardSortField field = sortBox.getSelected();
        List<LeaderboardEntry> sorted = getSortedLeaderboard(currentEntries, field, ascending);

        int rank = 1;
        for (LeaderboardEntry entry : sorted) {
            Table row = new Table();
            if (rank % 2 == 0) {
                row.setBackground(createSolidColor(ROW_STRIPE_COLOR));
            }
            row.pad(4, 8, 4, 8);

            row.add(buildRankCell(rank)).width(COL_RANK);
            addColumnDivider(row, DIVIDER_COLOR, 16);
            row.add(dataLabel(entry.username, rank == 1 ? HEADER_COLOR : Color.BLACK)).width(COL_USERNAME);
            addColumnDivider(row, DIVIDER_COLOR, 16);
            row.add(dataLabel("Season " + entry.unlockedChapter +
                " - Level " + entry.unlockedLevel, Color.BLACK)).width(COL_STAGE);
            addColumnDivider(row, DIVIDER_COLOR, 16);
            row.add(dataLabel(String.valueOf(entry.miniGamesCompleted), Color.BLACK)).width(COL_MINI);
            addColumnDivider(row, DIVIDER_COLOR, 16);
            row.add(dataLabel(String.valueOf(entry.dailyQuestsCount), Color.BLACK)).width(COL_DAILY);
            addColumnDivider(row, DIVIDER_COLOR, 16);
            row.add(dataLabel(String.valueOf(entry.normalQuestsCount), Color.BLACK)).width(COL_NORMAL);
            addColumnDivider(row, DIVIDER_COLOR, 16);
            row.add(dataLabel(entry.hasPlayedMuPoint ? String.valueOf(entry.maxMupoint)
                : "-", Color.BLACK)).width(COL_SCORE);

            rowsTable.add(row).growX().row();
            rank++;
        }

        if (sorted.isEmpty()) {
            Label empty = new Label("No players yet.", skin);
            empty.setColor(Color.BLACK);
            rowsTable.add(empty).left().padTop(10);
        }
    }

    public List<LeaderboardEntry> getSortedLeaderboard(List<LeaderboardEntry> allEntries,
                                                       LeaderboardSortField field, boolean ascending) {
        List<LeaderboardEntry> sortedList = new ArrayList<>(allEntries);
        Comparator<LeaderboardEntry> comparator = field.getComparator();

        if (!ascending) {
            comparator = comparator.reversed();
        }
        comparator = comparator.thenComparing(e -> e.username);

        sortedList.sort(comparator);
        return sortedList;
    }

    private void build() {
        pad(10);

        add(buildControlsRow()).left().padBottom(12).row();
        addDivider();

        add(buildHeaderRow()).growX().padBottom(6).row();

        rowsTable = new Table();
        add(rowsTable).growX().row();

        refreshRows();
    }

    private Table buildControlsRow() {
        Table outer = new Table();
        outer.setBackground(createSolidColor(CONTROLS_BG_COLOR));
        outer.pad(8, 12, 8, 12);

        Label sortLabel = new Label("Sort by:", skin);
        sortLabel.setColor(HEADER_COLOR);

        sortBox = new SelectBox<>(skin);
        sortBox.setItems(LeaderboardSortField.values());
        sortBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshRows();
            }
        });

        orderBtn = new TextButton(ascending ? "Ascending" : "Descending", skin);
        orderBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ascending = !ascending;
                orderBtn.setText(ascending ? "Ascending" : "Descending");
                refreshRows();
            }
        });

        outer.add(sortLabel).padRight(8);
        outer.add(sortBox).width(200).padRight(15);
        outer.add(orderBtn).width(140);

        return outer;
    }

    private Table buildHeaderRow() {
        Table t = new Table();
        t.setBackground(createSolidColor(HEADER_BG_COLOR));
        t.pad(6, 8, 6, 8);

        t.add(headerLabel("Rank")).width(COL_RANK);
        addColumnDivider(t, HEADER_DIVIDER_COLOR, 18);
        t.add(headerLabel("Username")).width(COL_USERNAME);
        addColumnDivider(t, HEADER_DIVIDER_COLOR, 18);
        t.add(headerLabel("Last Stage")).width(COL_STAGE);
        addColumnDivider(t, HEADER_DIVIDER_COLOR, 18);
        t.add(headerLabel("Mini-Games")).width(COL_MINI);
        addColumnDivider(t, HEADER_DIVIDER_COLOR, 18);
        t.add(headerLabel("Daily Q.")).width(COL_DAILY);
        addColumnDivider(t, HEADER_DIVIDER_COLOR, 18);
        t.add(headerLabel("Normal Q.")).width(COL_NORMAL);
        addColumnDivider(t, HEADER_DIVIDER_COLOR, 18);
        t.add(headerLabel("High Score")).width(COL_SCORE);
        return t;
    }

    private Label headerLabel(String text) {
        Label label = new Label(text, skin, "medium");
        label.setColor(HEADER_TEXT_COLOR);
        label.setFontScale(1.1f);
        label.setAlignment(Align.left);
        return label;
    }

    private Table buildRankCell(int rank) {
        Table cell = new Table();

        if (rank <= 3) {
            Color medalColor = rank == 1 ? GOLD : (rank == 2 ? SILVER : BRONZE);

            Stack stack = new Stack();
            stack.add(new Image(createCircle(medalColor, 28)));

            Table numHolder = new Table();
            Label num = new Label(String.valueOf(rank), skin);
            num.setColor(Color.BLACK);
            num.setAlignment(Align.center);
            numHolder.add(num).expand().center();
            stack.add(numHolder);

            cell.add(stack).size(28);
        } else {
            Label num = new Label(String.valueOf(rank), skin);
            num.setColor(Color.BLACK);
            num.setAlignment(Align.center);
            cell.add(num).width(COL_RANK);
        }

        return cell;
    }

    private Label dataLabel(String text, Color color) {
        Label label = new Label(text, skin);
        label.setColor(color);
        label.setFontScale(1.0f);
        label.setAlignment(Align.left);
        return label;
    }

    private void addDivider() {
        Image divider = new Image(createSolidColor(DIVIDER_COLOR));
        add(divider).growX().height(2).padBottom(8).row();
    }

    private void addColumnDivider(Table row, Color color, float height) {
        Image divider = new Image(createSolidColor(color));
        row.add(divider).width(COL_DIVIDER_W).height(height).padLeft(COL_DIVIDER_PAD).padRight(COL_DIVIDER_PAD);
    }

    private Drawable createSolidColor(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private Drawable createCircle(Color color, int diameter) {
        Pixmap pixmap = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(diameter / 2, diameter / 2, diameter / 2);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
