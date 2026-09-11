package view.terminalView;

import controller.SignupMenuController;
import models.enums.commands.SignupMenuCommands;
import view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class SignupMenuView implements View {
    private static SignupMenuView instance;
    private SignupMenuController controller;
    private boolean isSigningUp = false;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String gender;
    private String pickQuestionError = "Invalid command. Please pick a question.";

    public static SignupMenuView getInstance() {
        if (instance == null) {
            instance = new SignupMenuView();
            instance.controller = new SignupMenuController();
        }
        return instance;
    }

    @Override
    public void processCommand(String command) {
        boolean commandFound = false;
        for (SignupMenuCommands signupMenuCommands : SignupMenuCommands.values()) {
            Matcher matcher = signupMenuCommands.matcher(command);
            if (matcher.matches()) {
                commandFound = true;
                switch (signupMenuCommands) {
                    case REGISTER: {
                        if (isSigningUp) {
                            System.out.println(pickQuestionError);
                            return;
                        }
                        List<String> errors = new ArrayList<>();
                        username = matcher.group(1);
                        password = matcher.group(2);
                        nickname = matcher.group(4);
                        email = matcher.group(5);
                        gender = matcher.group(6);

                        errors.addAll(controller.getUsernameErrors(username));
                        errors.addAll(controller.getPasswordErrors(password, matcher.group(3)));
                        errors.addAll(controller.getNicknameErrors(nickname));
                        errors.addAll(controller.getEmailErrors(email));
                        errors.addAll(controller.getGenderErrors(gender));

                        if (errors.isEmpty()) {
                            for (String string : controller.getQuestions()) {
                                System.out.println(string);
                            }
                            isSigningUp = true;
                        } else {
                            for (String error : errors) {
                                System.out.println(error);
                            }
                        }
                        break;
                    }
                    case PICK_QUESTION: {
                        if (!isSigningUp) {
                            System.out.println("Please run the register command first!");
                            return;
                        }
                        List<String> errors = new ArrayList<>();
                        int questionNum = Integer.parseInt(matcher.group(1));
                        String question = controller.getQuestion(questionNum);
                        String answer = matcher.group(2);
                        String answerConfirm = matcher.group(3);

                        errors.addAll(controller.getPickQErrors(questionNum, answer, answerConfirm));

                        if (errors.isEmpty()) {
                            String result = controller.createUser(username,
                                    password,
                                    nickname,
                                    email, gender, question, answer);
                            System.out.println(result);
                            isSigningUp = false;
                        } else {
                            for (String error : errors) {
                                System.out.println(error);
                            }
                        }
                        break;
                    }
                    case MENU_SHOW_CURRENT:
                        controller.showCurrentMenu();
                        break;
                    case MENU_ENTER:
                        controller.changeMenu();
                        System.out.println("Entering login menu...\n");
                        break;
                    case MENU_EXIT:
                        controller.exitMenu();
                        break;
                }
                break;
            }
        }
        if (!commandFound) {
            System.out.println("Invalid command!");
        }
    }

    public SignupMenuController getController() {
        return controller;
    }
}
