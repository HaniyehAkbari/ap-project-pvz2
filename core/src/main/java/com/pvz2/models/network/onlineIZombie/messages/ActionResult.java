package com.pvz2.models.network.onlineIZombie.messages;

public class ActionResult {
    public boolean success;
    public String message;

    public ActionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
