package com.pvz2.controller;

import com.badlogic.gdx.Gdx;
import com.pvz2.models.network.NetworkClient;
import com.pvz2.models.network.NetworkMessage;
import com.pvz2.models.network.messages.AckResponse;
import com.pvz2.models.network.onlineIZombie.ClientGameController;
import com.pvz2.models.network.onlineIZombie.messages.*;
import com.pvz2.view.screen.MainMenuScreen;
import com.pvz2.view.screen.OnlineGameScreen;
import com.pvz2.view.screen.OnlineRoomMenuScreen;

public class OnlineRoomMenuController implements MenuController{
    OnlineRoomMenuScreen screen;

    public OnlineRoomMenuController(OnlineRoomMenuScreen screen) {
        this.screen = screen;
        NetworkClient.get().onPush("MATCH_FOUND", msg -> {
            MatchFound info = NetworkClient.get().parsePayload(msg, MatchFound.class);
            Gdx.app.postRunnable(() -> {
                ClientGameController controller = new ClientGameController(info);
                screen.fadeAndSwitchScreen(new OnlineGameScreen(screen.getGame(), controller));
            });
        });
        NetworkClient.get().onPush("CHALLENGE_DECLINED", msg -> {
            ChallengeDeclined info = NetworkClient.get().parsePayload(msg, ChallengeDeclined.class);
            Gdx.app.postRunnable(() -> {
                screen.toggleToRequestSection();
                screen.addToast("INFO", "User " + info.byUsername + "refused your request");
            });
        });
    }

    @Override
    public void changeMenu() {

    }

    @Override
    public void exitMenu() {
        screen.fadeAndSwitchScreen(new MainMenuScreen(screen.getGame()));
    }

    public void challenge(String username){
        try {
            NetworkMessage message =
                NetworkClient.get().sendRequest("CHALLENGE", new ChallengeRequest(username), 5000);
            ChallengeResponse response = NetworkClient.get().parsePayload(message, ChallengeResponse.class);
            Gdx.app.postRunnable(() -> {
                if (response.success){
                    screen.toggleToLoading("challenge");
                }
                else{
                    screen.addToast("Error",response.errorMessage);
                }
            });
        } catch (InterruptedException e) {
            Gdx.app.postRunnable(() -> {
                screen.addToast("Error", "please try again");
            });
        }
    }

    public void play(){
        try {
            NetworkMessage message =
                NetworkClient.get().sendRequest("RANDOM_MATCH", new RandomMatchRequest(), 5000);
            RandomMatchResponse response = NetworkClient.get().parsePayload(message, RandomMatchResponse.class);
            Gdx.app.postRunnable(() -> {
                if (response.success){
                    if (response.waiting){
                        screen.toggleToLoading("play");
                    }
                }
                else{
                    screen.addToast("Error",response.errorMessage);
                }
            });
        } catch (InterruptedException e) {
            Gdx.app.postRunnable(() -> {
                screen.addToast("Error", "please try again");
            });
        }
    }

    public boolean cancelRandomMatch() {
        try {
            NetworkMessage message =
                NetworkClient.get().sendRequest("CANCEL_RANDOM_MATCH", new CancelRandomMatchRequest(), 5000);
            AckResponse response = NetworkClient.get().parsePayload(message, AckResponse.class);
            if (response.success){
                Gdx.app.postRunnable(() -> {
                    screen.toggleToRequestSection();
                });
                return true;
            }
            else{
                Gdx.app.postRunnable(() -> {
                    screen.addToast("Error","please try again");
                });
                return false;
            }
        } catch (InterruptedException e) {
            Gdx.app.postRunnable(() -> {
                screen.addToast("Error", "please try again");
            });
            return false;
        }
    }
}
