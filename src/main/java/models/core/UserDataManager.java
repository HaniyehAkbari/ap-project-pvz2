package models.core;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.GameMenuController;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDataManager {
    private static final String USERS_DIR = "pvz2/src/main/java/models/users/";
    private static final String CURRENT_USER_FILE = "pvz2/src/main/java/models/users/current_user.txt";

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return false;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return clazz == java.util.Random.class;
                }
            })
            .setPrettyPrinting()
            .create();

    static {
        File dir = new File(USERS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static boolean saveUser(User user) {
        if (user == null || user.getUsername() == null) return false;

        File userFile = new File(USERS_DIR + user.getUsername() + ".json");

        try (FileWriter writer = new FileWriter(userFile)) {
            GSON.toJson(user, writer);
            return true;
        } catch (IOException e) {
            GameMenuController.updateState("save error: " + e.getMessage());
            return false;
        }
    }

    public static User loadUser(String username) {
        File userFile = new File(USERS_DIR + username + ".json");

        if (!userFile.exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(userFile)) {
            User user = GSON.fromJson(reader, User.class);
            if (user != null) {
                user.afterLoad();
            }
            return user;
        } catch (IOException e) {
            GameMenuController.updateState("read error: " + e.getMessage());
            return null;
        }
    }

    public static List<User> loadAllUsers() {
        List<User> users = new ArrayList<>();
        File dir = new File(USERS_DIR);

        if (!dir.exists() || !dir.isDirectory()) {
            return users;
        }
        File[] userFiles = dir.listFiles((directory, name) -> name.toLowerCase().endsWith(".json"));

        if (userFiles != null) {
            for (File file : userFiles) {
                String fileName = file.getName();
                String username = fileName.substring(0, fileName.lastIndexOf(".json"));

                User user = loadUser(username);
                if (user != null) {
                    users.add(user);
                }
            }
        }
        return users;
    }

    public static boolean userExists(String username) {
        return new File(USERS_DIR + username +
                ".json").exists();
    }

    public static void saveLoggedInUser(String username) {
        try (FileWriter writer = new FileWriter(CURRENT_USER_FILE)) {
            writer.write(username);
        } catch (IOException e) {
            GameMenuController.updateState("save logged in error: " + e.getMessage());
        }
    }

    public static String getLoggedInUsername() {
        File file = new File(CURRENT_USER_FILE);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String username = reader.readLine();
            return (username != null) ? username.trim() : null;
        } catch (IOException e) {
            return null;
        }
    }

    public static void clearLoggedInUser() {
        File file = new File(CURRENT_USER_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean updateUsername(String oldUsername, User user) {
        if (user == null || user.getUsername() == null) return false;

        File oldFile = new File(USERS_DIR + oldUsername + ".json");
        if (oldFile.exists()) {
            oldFile.delete();
        }

        boolean saved = saveUser(user);

        String loggedInUser = getLoggedInUsername();
        if (loggedInUser != null && loggedInUser.equals(oldUsername)) {
            saveLoggedInUser(user.getUsername());
        }

        return saved;
    }
}
