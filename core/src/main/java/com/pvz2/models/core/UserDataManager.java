package com.pvz2.models.core;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pvz2.controller.GameMenuController;
import com.pvz2.models.network.NetworkGson;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserDataManager {
    private static final String USER_HOME = System.getProperty("user.home");

    private static final String BASE_DIR = USER_HOME + File.separator + ".pvz2_server" + File.separator;
    private static final String USERS_DIR = BASE_DIR + "users" + File.separator;
    private static final String SESSION_TOKEN_FILE = BASE_DIR + "session_token.txt";
    private static final Map<String, Object> FILE_LOCKS = new ConcurrentHashMap<>();


    private static Object lockFor(String username) {
        return FILE_LOCKS.computeIfAbsent(username, k -> new Object());
    }

    public static boolean saveUser(User user) {
        if (user == null || user.getUsername() == null) return false;
        synchronized (lockFor(user.getUsername())) {
            File userFile = new File(USERS_DIR + user.getUsername() + ".json");
            try (FileWriter writer = new FileWriter(userFile)) {
                NetworkGson.INSTANCE.toJson(user, writer);
                return true;
            } catch (IOException e) {
                GameMenuController.updateState("save error: " + e.getMessage());
                return false;
            }
        }
    }

    public static User loadUser(String username) {
        synchronized (lockFor(username)) {
            File userFile = new File(USERS_DIR + username + ".json");
            if (!userFile.exists()) return null;
            try (FileReader reader = new FileReader(userFile)) {
                User user = GSON.fromJson(reader, User.class);
                if (user != null) user.afterLoad();
                return user;
            } catch (IOException e) {
                GameMenuController.updateState("read error: " + e.getMessage());
                return null;
            }
        }
    }

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

    public static void saveSessionToken(String token) {
        try (FileWriter writer = new FileWriter(SESSION_TOKEN_FILE)) {
            writer.write(token);
        } catch (IOException e) {
            GameMenuController.updateState("save session error: " + e.getMessage());
        }
    }

    public static String getSessionToken() {
        File file = new File(SESSION_TOKEN_FILE);
        if (!file.exists()) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String token = reader.readLine();
            return (token != null) ? token.trim() : null;
        } catch (IOException e) {
            return null;
        }
    }

    public static void clearSessionToken() {
        File file = new File(SESSION_TOKEN_FILE);
        if (file.exists()) file.delete();
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
        return new File(USERS_DIR + username + ".json").exists();
    }

    public static boolean updateUsername(String oldUsername, User user) {
        if (user == null || user.getUsername() == null) return false;

        boolean saved = saveUser(user); // write the new file FIRST
        if (!saved) return false;

        File oldFile = new File(USERS_DIR + oldUsername + ".json");
        if (oldFile.exists() && !oldUsername.equals(user.getUsername())) {
            oldFile.delete(); // only delete the old one once the new one is confirmed written
        }

        return true;
    }
}
