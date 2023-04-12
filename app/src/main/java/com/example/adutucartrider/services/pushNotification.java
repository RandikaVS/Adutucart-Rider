package com.example.adutucartrider.services;

import com.example.adutucartrider.models.NotificationData;

public class pushNotification {

    private NotificationData data;
    private String to;

    public pushNotification(NotificationData data, String to) {
        this.data = data;
        this.to = to;
    }

    public NotificationData getData() {
        return data;
    }

    public String getTo() {
        return to;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
