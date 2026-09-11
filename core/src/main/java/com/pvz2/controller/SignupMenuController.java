package com.pvz2.controller;

import com.badlogic.gdx.Gdx;
import com.pvz2.models.core.UserManager;
import com.pvz2.view.screen.LoginMenuScreen;
import com.pvz2.view.screen.SignupMenuScreen;

import java.util.ArrayList;
import java.util.List;

public class SignupMenuController implements MenuController {
    private List<String> questions = new ArrayList<>();
    private SignupMenuScreen screen;

    public SignupMenuController(SignupMenuScreen screen) {
        this.screen = screen;
        questions.add("1. What is your best friend's name?");
        questions.add("2. Where was you born?");
        questions.add("3. What is your major?");
    }

    public SignupMenuController() {
    }

    @Override
    public void changeMenu() {
        screen.fadeAndSwitchScreen(new LoginMenuScreen(screen.getGame()));
    }

    @Override
    public void exitMenu() {
        Gdx.app.exit();
    }

    public List<String> getUsernameErrors(String username) {
        List<String> errors = new ArrayList<>();
        if (username == null || username.trim().isEmpty()) {
            errors.add("Please enter username.");
            return errors;
        }
        if (!username.matches("^[0-9A-Za-z-]+$")) {
            errors.add("Invalid username (only letters, digits, and '-' allowed).");
        }
        return errors;
    }

    public List<String> getNicknameErrors(String nickname) {
        List<String> errors = new ArrayList<>();
        if (nickname == null || nickname.trim().isEmpty()) {
            errors.add("Please enter nickname.");
            return errors;
        }
        if (nickname.length() < 3) errors.add("Nickname is too short.");
        if (nickname.length() > 30) errors.add("Nickname is too long.");
        return errors;
    }


    public List<String> validatePasswordStrength(String password) {
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        boolean hasInvalidChar = false;
        String specials = "!#$%^&*()=+{}[]|/\\:;'\",<>?";

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUpper = true;
            } else if (Character.isLowerCase(ch)) {
                hasLower = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (specials.indexOf(ch) >= 0) {
                hasSpecial = true;
            } else {
                hasInvalidChar = true;
            }
        }
        List<String> errors = new ArrayList<>();
        if (!hasUpper) errors.add("password requires at least one uppercase letter.");
        if (!hasLower) errors.add("password requires at least one lowercase letter.");
        if (!hasDigit) errors.add("password requires at least one digit.");
        if (!hasSpecial)
            errors.add("password requires at least one special symbol such as (! $ *).");
        if (hasInvalidChar) errors.add("password contains illegal characters.");
        if (password.length() < 8) errors.add("password requires at least 8 characters.");

        return errors;
    }

    public List<String> getPasswordErrors(String password, String passwordCon) {
        List<String> errors = new ArrayList<>();

        List<String> strengthErrors = validatePasswordStrength(password);
        if (!strengthErrors.isEmpty()) {
            errors.addAll(strengthErrors);
            return errors;
        }

        if (passwordCon == null || !password.equals(passwordCon)) {
            errors.add("Password and password confirm don't match.");
        }

        return errors;
    }

    public String createUser(String username,
                             String password,
                             String nickname,
                             String email,
                             String gender,
                             String securityQ,
                             String securityA) {
        return UserManager.register(username, password, nickname, email, gender, securityQ, securityA);
    }

    public List<String> getEmailErrors(String email) {
        List<String> errors = new ArrayList<>();

        if (email.matches(".*[!#$%^&*()=+{}\\[\\]|/\\\\:;',<>?].*")) {
            errors.add("email should not contain special symbol");
        }

        if (!email.matches("^[^@]+@[^@]+$")) {
            errors.add("email should only contain one @");
            return errors;
        }

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (!username.matches("^[a-zA-Z0-9._-]+$")) {
            errors.add("username before @ can only contain letters, digits and '.' , '-' , '_'");
        }
        if (!username.matches("^[a-zA-Z0-9](.*[a-zA-Z0-9])?$")) {
            errors.add("username before @ should start and end with a letter or digit");
        }
        if (username.matches(".*\\.{2,}.*")) {
            errors.add("username before @ should not contain '.' twice in a row");
        }

        if (!domain.matches(".*\\..*")) {
            errors.add("domain after @ requires at least one '.'");
        }
        if (!domain.matches("^[a-zA-Z0-9.-]+$")) {
            errors.add("domain after @ can only contain letters, digits and -");
        }
        if (!domain.matches("^[a-zA-Z0-9](.*[a-zA-Z0-9])?$")) {
            errors.add("domain after @ should start and end with a letter or digit");
        }
        if (!domain.matches(".*\\.[a-zA-Z0-9]{2,}$")) {
            errors.add("extension requires at least two letters");
        }

        return errors;
    }

    public List<String> getGenderErrors(String gender) {
        List<String> errors = new ArrayList<>();
        if (!gender.equals("male") && !gender.equals("female")) {
            errors.add("Invalid gender. Please enter male or female.");
        }
        return errors;
    }

    public List<String> getAnswerErrors(int num, String answer) {
        List<String> errors = new ArrayList<>();
        if (num <= 0 || num >= 4) {
            errors.add("Please choose a num between 1 and 3.");
        }
        if (answer == null || answer.trim().isEmpty()) {
            errors.add("Please enter an answer.");
        }
        return errors;
    }

    public List<String> getAnswerConfirmErrors(int num, String answer, String answerCon) {
        List<String> errors = getAnswerErrors(num, answer);
        if (!errors.isEmpty()) {
            return errors;
        }

        if (answerCon == null || !answer.equals(answerCon)) {
            errors.add("Answer and answer confirm don't match.");
        }

        return errors;
    }

    @Deprecated
    public List<String> getPickQErrors(int num, String answer, String answerCon) {
        return getAnswerConfirmErrors(num, answer, answerCon);
    }

    public List<String> getQuestions() {
        return questions;
    }

}
