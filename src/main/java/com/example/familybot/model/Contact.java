package com.example.familybot.model;

import java.time.LocalDate;

public class Contact {
    private Long id;
    private Long familyId;
    private String firstName;
    private String surname;
    private String phone;
    private LocalDate dob;
    private LocalDate anniversary;

    public Contact() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public LocalDate getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(LocalDate anniversary) {
        this.anniversary = anniversary;
    }

    public String getFullName() {
        String s = surname == null ? "" : surname.trim();
        return (firstName == null ? "" : firstName.trim()) + (s.isEmpty() ? "" : " " + s);
    }
}
