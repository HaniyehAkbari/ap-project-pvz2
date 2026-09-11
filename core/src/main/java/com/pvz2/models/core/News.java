package com.pvz2.models.core;

import com.pvz2.models.enums.NewsType;

import java.time.LocalDate;

public class News {
    private final String title;
    private final String message;
    private final NewsType type;
    private final LocalDate date;

    private boolean read;

    public News(String title, String message, NewsType type) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.date = LocalDate.now();
        this.read = false;
    }

    public String getTitle() {
        return title;
    }

    public String getDate(){
        return date.toString();
    }

    public String getMessage() {
        return message;
    }

    public NewsType getType() {
        return type;
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }
}
