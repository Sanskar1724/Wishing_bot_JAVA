package com.example.familybot.service;

import com.example.familybot.model.Contact;
import com.example.familybot.model.DashboardRow;
import com.example.familybot.model.Family;
import com.example.familybot.repository.ContactRepository;
import com.example.familybot.repository.FamilyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class FamilyService {
    private final FamilyRepository familyRepository;
    private final ContactRepository contactRepository;

    public FamilyService(FamilyRepository familyRepository, ContactRepository contactRepository) {
        this.familyRepository = familyRepository;
        this.contactRepository = contactRepository;
    }

    public Family createFamily(String familyName) {
        Family existing = familyRepository.findFamilyByName(familyName);
        if (existing != null) {
            return existing;
        }
        String joinCode = toSlug(familyName) + "-" + UUID.randomUUID().toString().substring(0, 6);
        return familyRepository.createFamily(familyName, joinCode);
    }

    public Family findFamilyByCode(String joinCode) {
        return familyRepository.findFamilyByCode(joinCode);
    }

    public Family findFamilyByName(String familyName) {
        return familyRepository.findFamilyByName(familyName);
    }

    public void saveContact(Contact contact) {
        contactRepository.saveContact(contact);
    }

    public List<DashboardRow> buildDashboard(Long familyId) {
        List<Contact> contacts = contactRepository.getFamilyContacts(familyId);
        LocalDate today = LocalDate.now();
        List<DashboardRow> rows = new ArrayList<>();

        for (Contact c : contacts) {
            boolean birthdayToday = isSameMonthDay(c.getDob(), today);
            boolean anniversaryToday = isSameMonthDay(c.getAnniversary(), today);
            List<String> messages = buildMessages(c.getFirstName(), birthdayToday, anniversaryToday);
            rows.add(new DashboardRow(c, birthdayToday, anniversaryToday, messages));
        }

        return rows;
    }

    public int countTodayCelebrations(Long familyId) {
        return contactRepository.getTodayContacts(familyId).size();
    }

    public List<Contact> getFamilyContacts(Long familyId) {
        return contactRepository.getFamilyContacts(familyId);
    }

    public Contact findContactByPhone(Long familyId, String phone) {
        return contactRepository.findByFamilyAndPhone(familyId, phone);
    }

    private boolean isSameMonthDay(LocalDate date, LocalDate today) {
        if (date == null) {
            return false;
        }
        return date.getMonthValue() == today.getMonthValue() && date.getDayOfMonth() == today.getDayOfMonth();
    }

    private List<String> buildMessages(String name, boolean birthday, boolean anniversary) {
        List<String> messages = new ArrayList<>();
        if (birthday) {
            messages.add("Happy Birthday " + name + "! \uD83C\uDF89");
            messages.add("Wishing you a joyful birthday \uD83E\uDD73");
            messages.add("Have an amazing year ahead \uD83C\uDF82");
        }
        if (anniversary) {
            messages.add("Happy Anniversary \uD83D\uDC8D");
            messages.add("Stay blessed together \u2764\uFE0F");
            messages.add("Wishing you a joyful married life \u2728");
        }
        if (messages.isEmpty()) {
            messages.add("Send warm wishes today!");
        }
        return messages;
    }

    private String toSlug(String familyName) {
        if (familyName == null) {
            return "family";
        }
        String slug = familyName.toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        return slug.isEmpty() ? "family" : slug;
    }
}
