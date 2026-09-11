package com.pvz2.view.table;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.view.screen.MainMenuScreen;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;

public class ZombiesTable extends Table {
    private static HashMap<String, String> zombiesPicAddress;
    private static HashMap<String, String> zombiesAnimAddress;
    private static HashMap<String, HashMap<String, Boolean>> zombiesVisibilities;
    private Consumer<String> clickMethod;
    private int column;
    private Collection<String> strings;
    private boolean forceShow;

    public ZombiesTable(Consumer<String> clickMethod, int column, Collection<String> strings, boolean forceShow) {
        this.clickMethod = clickMethod;
        this.column = column;
        this.strings = strings;
        this.forceShow = forceShow;
        build();
    }

    private void build(){
        User user = App.getCurrentUser();
        this.defaults().pad(20).padLeft(40).padRight(40);
        if (user == null) return;
        int i = 1;
        for (String zombieName : strings){
            this.add(zombieCell(zombieName, user));
            if (i == column){
                i = 1;
                this.row();
                continue;
            }
            i++;
        }
    }

    private Stack zombieCell(String name, User user){
        ImageButton imageButton = MainMenuScreen.createImageButton(
            "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_READY", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_SELECTED"
            , App.getGameApp().textureBank);
        Stack stack = new Stack();
        stack.add(imageButton);
        if (user.getShowedZombies().get(name) || forceShow){
            Image image = new Image(App.getGameApp().textureBank.region(getZombiesPicAddress().get(name)));
            Table imageWrapper = new Table();
            imageWrapper.add(image).bottom().pad(5);
            stack.add(imageWrapper);
            image.setTouchable(Touchable.disabled);
            imageButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    clickMethod.accept(name);
                }
            });
        }
        return stack;
    }

    public static HashMap<String, String> getZombiesPicAddress() {
        if (zombiesPicAddress == null){
            zombiesPicAddress = new HashMap<>();
            zombiesPicAddress.put("ZombieDefault", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TUTORIAL");
            zombiesPicAddress.put("ZombieConeHead", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TUTORIAL_ARMOR1");
            zombiesPicAddress.put("ZombieBucketHead", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TUTORIAL_ARMOR2");
            zombiesPicAddress.put("ZombieBrickHead", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_MUMMY_ARMOR4");
            zombiesPicAddress.put("ZombieKnight", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_ARMOR3");
            zombiesPicAddress.put("ZombieGargantuar", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TUTORIAL_GARGANTUAR");
            zombiesPicAddress.put("ZombieImp", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TUTORIAL_IMP");
            zombiesPicAddress.put("ZombieRa", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_RA");
            zombiesPicAddress.put("ZombieExplorer", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_EXPLORER");
            zombiesPicAddress.put("ZombieTombRaiser", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TOMB_RAISER");
            zombiesPicAddress.put("ZombieIceAgeDodo", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_ICEAGE_DODO");
            zombiesPicAddress.put("ZombieIceAgeHunter", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_ICEAGE_HUNTER");
            zombiesPicAddress.put("ZombieIceAgeTroglobite", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_ICEAGE_TROGLOBITE");
            zombiesPicAddress.put("ZombieBeachFisherman", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_BEACH_FISHERMAN");
            zombiesPicAddress.put("ZombieBeachOctopus", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_BEACH_OCTOPUS");
            zombiesPicAddress.put("ZombieBeachSnorkel", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_BEACH_SNORKEL");
            zombiesPicAddress.put("ZombieDarkJuggler", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_JUGGLER");
            zombiesPicAddress.put("ZombieWizard", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_WIZARD");
            zombiesPicAddress.put("ZombieDarkKing", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_KING");
            zombiesPicAddress.put("ZombieDarkImpDragon", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_IMP_DRAGON");
            zombiesPicAddress.put("ZombieModernAllStar", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_MODERN_ALLSTAR");
            zombiesPicAddress.put("ZombieLostCityJane", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_LOSTCITY_JANE");
            zombiesPicAddress.put("ZombieCrystalSkull", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_LOSTCITY_CRYSTALSKULL");
            zombiesPicAddress.put("ZombieProspector", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_PROSPECTOR");
            zombiesPicAddress.put("ZombiePiano", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_PIANO");
            zombiesPicAddress.put("ZombieArcade", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_EIGHTIES_ARCADE");
            zombiesPicAddress.put("ZombieNewspaper", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_MODERN_NEWSPAPER");
            zombiesPicAddress.put("ZombieBarrelRoller", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_BARRELROLLER");
        }
        return zombiesPicAddress;
    }

    public static HashMap<String, String> getZombiesAnimAddress() {
        if (zombiesAnimAddress == null){
            zombiesAnimAddress = new HashMap<>();
            zombiesAnimAddress.put("ZombieDefault", "768/INITIAL/ZOMBIE/ZOMBIE_TUTORIAL/ZOMBIE_TUTORIAL.PAM");
            zombiesAnimAddress.put("ZombieConeHead", "768/INITIAL/ZOMBIE/ZOMBIE_TUTORIAL/ZOMBIE_TUTORIAL.PAM");
            zombiesAnimAddress.put("ZombieBucketHead", "768/INITIAL/ZOMBIE/ZOMBIE_TUTORIAL/ZOMBIE_TUTORIAL.PAM");
            zombiesAnimAddress.put("ZombieBrickHead", "768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_BASIC/ZOMBIE_EGYPT_BASIC.PAM");
            zombiesAnimAddress.put("ZombieKnight", "768/FULL/ZOMBIE/ZOMBIE_DARK_BASIC/ZOMBIE_DARK_BASIC.PAM");
            zombiesAnimAddress.put("ZombieGargantuar",
                "768/INITIAL/ZOMBIE/TUTORIAL_GARGANTUAR/TUTORIAL_GARGANTUAR.PAM");
            zombiesAnimAddress.put("ZombieImp", "768/INITIAL/ZOMBIE/ZOMBIE_TUTORIAL_IMP/ZOMBIE_TUTORIAL_IMP.PAM");
            zombiesAnimAddress.put("ZombieRa", "768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_RA/ZOMBIE_EGYPT_RA.PAM");
            zombiesAnimAddress.put("ZombieExplorer", "768/INITIAL/ZOMBIE/ZOMBIE_EXPLORER/ZOMBIE_EXPLORER.PAM");
            zombiesAnimAddress.put("ZombieTombRaiser",
                "768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_TOMBRAISER/ZOMBIE_EGYPT_TOMBRAISER.PAM");
            zombiesAnimAddress.put("ZombieIceAgeDodo",
                "768/FULL/ZOMBIE/ZOMBIE_ICEAGE_DODORIDER/ZOMBIE_ICEAGE_DODORIDER.PAM");
            zombiesAnimAddress.put("ZombieIceAgeHunter",
                "768/FULL/ZOMBIE/ZOMBIE_ICEAGE_HUNTER/ZOMBIE_ICEAGE_HUNTER.PAM");
            zombiesAnimAddress.put("ZombieIceAgeTroglobite",
                "768/FULL/ZOMBIE/ZOMBIE_ICEAGE_TROGLOBITE/ZOMBIE_ICEAGE_TROGLOBITE.PAM");
            zombiesAnimAddress.put("ZombieBeachFisherman",
                "768/FULL/ZOMBIE/ZOMBIE_BEACH_FISHERMAN/ZOMBIE_BEACH_FISHERMAN.PAM");
            zombiesAnimAddress.put("ZombieBeachOctopus",
                "768/FULL/ZOMBIE/ZOMBIE_BEACH_OCTOPUS/ZOMBIE_BEACH_OCTOPUS.PAM");
            zombiesAnimAddress.put("ZombieBeachSnorkel",
                "768/FULL/ZOMBIE/ZOMBIE_BEACH_SNORKELER/ZOMBIE_BEACH_SNORKELER.PAM");
            zombiesAnimAddress.put("ZombieDarkJuggler", "768/FULL/ZOMBIE/ZOMBIE_DARK_JESTER/ZOMBIE_DARK_JESTER.PAM");
            zombiesAnimAddress.put("ZombieWizard", "768/FULL/ZOMBIE/ZOMBIE_DARK_WIZARD/ZOMBIE_DARK_WIZARD.PAM");
            zombiesAnimAddress.put("ZombieDarkKing", "768/FULL/ZOMBIE/ZOMBIE_DARK_KING/ZOMBIE_DARK_KING.PAM");
            zombiesAnimAddress.put("ZombieDarkImpDragon",
                "768/FULL/ZOMBIE/ZOMBIE_DARK_IMP_DRAGON/ZOMBIE_DARK_IMP_DRAGON.PAM");
            zombiesAnimAddress.put("ZombieModernAllStar",
                "768/FULL/ZOMBIE/ZOMBIE_MODERN_ALLSTAR/ZOMBIE_MODERN_ALLSTAR.PAM");
            zombiesAnimAddress.put("ZombieLostCityJane",
                "768/FULL/ZOMBIE/ZOMBIE_LOSTCITY_JANE/ZOMBIE_LOSTCITY_JANE.PAM");
            zombiesAnimAddress.put("ZombieCrystalSkull",
                "768/FULL/ZOMBIE/ZOMBIE_LOSTCITY_CRYSTALSKULL/ZOMBIE_LOSTCITY_CRYSTALSKULL.PAM");
            zombiesAnimAddress.put("ZombieProspector", "768/FULL/ZOMBIE/ZOMBIE_PROSPECTOR/ZOMBIE_PROSPECTOR.PAM");
            zombiesAnimAddress.put("ZombiePiano", "768/FULL/ZOMBIE/ZOMBIE_PIANO/ZOMBIE_PIANO.PAM");
            zombiesAnimAddress.put("Piano", "768/FULL/ZOMBIE/PIANO/PIANO.PAM");
            zombiesAnimAddress.put("ZombieArcade", "768/FULL/ZOMBIE/ZOMBIE_80S_ARCADE/ZOMBIE_80S_ARCADE.PAM");
            zombiesAnimAddress.put("ZombieNewspaper",
                "768/FULL/ZOMBIE/ZOMBIE_MODERN_NEWSPAPER/ZOMBIE_MODERN_NEWSPAPER.PAM");
            zombiesAnimAddress.put("Sheep", "768/FULL/EFFECTS/DARK_WIZARD_SHEEPENING/DARK_WIZARD_SHEEPENING.PAM");
            zombiesAnimAddress.put("ZombieBarrelRoller",
                "768/FULL/ZOMBIE/ZOMBIE_PIRATE_BARREL_PUSHER/ZOMBIE_PIRATE_BARREL_PUSHER.PAM");
            zombiesAnimAddress.put("BarrelPirate",
                "768/FULL/ZOMBIE/ZOMBIE_PIRATE_BARREL_PUSHER_BARREL/ZOMBIE_PIRATE_BARREL_PUSHER_BARREL.PAM");}
        return zombiesAnimAddress;}

    public static HashMap<String, HashMap<String, Boolean>> getZombiesVisibilities() {
        if (zombiesVisibilities == null){
            zombiesVisibilities = new HashMap<>();

            HashMap<String, Boolean> coneHeadHashMap = new HashMap<>();
            coneHeadHashMap.put("zombie_armor_cone_norm", true);
            zombiesVisibilities.put("ZombieConeHead", coneHeadHashMap);
            HashMap<String, Boolean> bucketHeadHashMap = new HashMap<>();
            bucketHeadHashMap.put("zombie_armor_bucket_norm", true);
            zombiesVisibilities.put("ZombieBucketHead", bucketHeadHashMap);
            HashMap<String, Boolean> brickHeadHashMap = new HashMap<>();
            brickHeadHashMap.put("zombie_armor_brick_norm", true);
            zombiesVisibilities.put("ZombieBrickHead", brickHeadHashMap);
            HashMap<String, Boolean> knightHashMap = new HashMap<>();
            knightHashMap.put("_zombie_armor_crown_states", true);
            knightHashMap.put("zombie_armor_crown_norm", true);
            knightHashMap.put("zombie_shoulder_armor", true);
            knightHashMap.put("zombie_shoulder_armor_norm", true);
            zombiesVisibilities.put("ZombieKnight", knightHashMap);
        }
        return zombiesVisibilities;
    }
}
