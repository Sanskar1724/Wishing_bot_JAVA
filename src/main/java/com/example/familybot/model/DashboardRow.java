package com.example.familybot.model;

import java.util.List;

public class DashboardRow {
    private Contact contact;
    private boolean birthdayToday;
    private boolean anniversaryToday;
    private List<String> messages;

    public DashboardRow(Contact contact, boolean birthdayToday, boolean anniversaryToday, List<String> messages) {
        this.contact = contact;
        this.birthdayToday = birthdayToday;
        this.anniversaryToday = anniversaryToday;
        this.messages = messages;
    }

    public Contact getContact() {
        return contact;
    }

    public boolean isBirthdayToday() {
        return birthdayToday;
    }

    public boolean isAnniversaryToday() {
        return anniversaryToday;
    }

    public List<String> getMessages() {
        return messages;
    }
}
