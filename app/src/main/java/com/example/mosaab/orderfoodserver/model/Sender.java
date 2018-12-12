package com.example.mosaab.orderfoodserver.model;

public class Sender {
    public String to;
    public Notification_ notification;

    public Sender() {
    }

    public Sender(String to, Notification_ notification) {
        this.to = to;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification_ getNotification() {
        return notification;
    }

    public void setNotification(Notification_ notification) {
        this.notification = notification;
    }
}
