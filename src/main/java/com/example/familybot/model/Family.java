package com.example.familybot.model;

public class Family {
    private Long id;
    private String familyName;
    private String joinCode;

    public Family() {
    }

    public Family(Long id, String familyName, String joinCode) {
        this.id = id;
        this.familyName = familyName;
        this.joinCode = joinCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }
}
