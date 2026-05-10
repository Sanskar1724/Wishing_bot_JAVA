package com.example.familybot.controller;

import com.example.familybot.model.Contact;
import com.example.familybot.model.DashboardRow;
import com.example.familybot.model.Family;
import com.example.familybot.service.FamilyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class FamilyController {
    private final FamilyService familyService;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{8,15}$");

    public FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/create-family";
    }

    @GetMapping("/create-family")
    public String createFamilyForm() {
        return "create-family";
    }

    @PostMapping("/create-family")
    public String createFamily(@RequestParam("familyName") String familyName, Model model, HttpServletRequest request) {
        if (familyName == null || familyName.isBlank()) {
            model.addAttribute("error", "Family name is required");
            model.addAttribute("familyName", familyName);
            return "create-family";
        }
        Family existing = familyService.findFamilyByName(familyName);
        Family family = existing != null ? existing : familyService.createFamily(familyName);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String joinLink = baseUrl + "/join/" + family.getJoinCode();
        model.addAttribute("family", family);
        model.addAttribute("joinLink", joinLink);
        model.addAttribute("familyName", familyName);
        model.addAttribute("reused", existing != null);
        return "create-family";
    }

    @GetMapping("/join/{code}")
    public String joinForm(@PathVariable("code") String code, Model model) {
        Family family = familyService.findFamilyByCode(code);
        if (family == null) {
            model.addAttribute("error", "Invalid family link");
            return "join";
        }
        model.addAttribute("family", family);
        return "join";
    }

    @PostMapping("/join/{code}")
    public String joinFamily(@PathVariable("code") String code,
            @RequestParam("firstName") String firstName,
            @RequestParam(value = "surname", required = false) String surname,
            @RequestParam("phone") String phone,
            @RequestParam("dob") String dob,
            @RequestParam(value = "anniversary", required = false) String anniversary,
            Model model) {
        Family family = familyService.findFamilyByCode(code);
        if (family == null) {
            model.addAttribute("error", "Invalid family link");
            return "join";
        }

        List<String> errors = new ArrayList<>();
        LocalDate today = LocalDate.now();

        if (firstName == null || firstName.isBlank()) {
            errors.add("First name is required");
        }

        if (phone == null || !PHONE_PATTERN.matcher(phone.trim()).matches()) {
            errors.add("Phone number should contain 8 to 15 digits (optional +)");
        }

        LocalDate dobDate = parseDate(dob, "Birthday", errors);
        if (dobDate != null && dobDate.isAfter(today)) {
            errors.add("Birthday cannot be in the future");
        }

        LocalDate anniversaryDate = null;
        if (anniversary != null && !anniversary.isBlank()) {
            anniversaryDate = parseDate(anniversary, "Anniversary", errors);
            if (anniversaryDate != null && anniversaryDate.isAfter(today)) {
                errors.add("Anniversary cannot be in the future");
            }
        }

        if (dobDate != null && anniversaryDate != null && anniversaryDate.isBefore(dobDate)) {
            errors.add("Anniversary cannot be before birthday");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("family", family);
            model.addAttribute("errors", errors);
            model.addAttribute("firstName", firstName);
            model.addAttribute("surname", surname);
            model.addAttribute("phone", phone);
            model.addAttribute("dob", dob);
            model.addAttribute("anniversary", anniversary);
            return "join";
        }

        Contact existingContact = familyService.findContactByPhone(family.getId(), phone.trim());
        if (existingContact != null) {
            model.addAttribute("family", family);
            model.addAttribute("alreadyJoined", true);
            model.addAttribute("dashboardLink", "/dashboard/" + code);
            return "join";
        }

        Contact contact = new Contact();
        contact.setFamilyId(family.getId());
        contact.setFirstName(firstName);
        contact.setSurname(surname);
        contact.setPhone(phone);
        contact.setDob(dobDate);
        contact.setAnniversary(anniversaryDate);

        familyService.saveContact(contact);
        return "redirect:/dashboard/" + code;
    }

    @GetMapping("/dashboard/{code}")
    public String dashboard(@PathVariable("code") String code, Model model) {
        Family family = familyService.findFamilyByCode(code);
        if (family == null) {
            model.addAttribute("error", "Invalid family link");
            return "dashboard";
        }

        List<DashboardRow> rows = familyService.buildDashboard(family.getId());
        int birthdayCount = (int) rows.stream().filter(DashboardRow::isBirthdayToday).count();
        int anniversaryCount = (int) rows.stream().filter(DashboardRow::isAnniversaryToday).count();

        model.addAttribute("family", family);
        model.addAttribute("rows", rows);
        model.addAttribute("totalMembers", rows.size());
        model.addAttribute("birthdayCount", birthdayCount);
        model.addAttribute("anniversaryCount", anniversaryCount);
        return "dashboard";
    }

    @GetMapping("/dashboard/{code}/today")
    public String today(@PathVariable("code") String code, Model model) {
        Family family = familyService.findFamilyByCode(code);
        if (family == null) {
            model.addAttribute("error", "Invalid family link");
            return "today";
        }

        List<DashboardRow> allRows = familyService.buildDashboard(family.getId());
        List<DashboardRow> rows = allRows.stream()
                .filter(row -> row.isBirthdayToday() || row.isAnniversaryToday())
                .toList();
        int birthdayCount = (int) rows.stream().filter(DashboardRow::isBirthdayToday).count();
        int anniversaryCount = (int) rows.stream().filter(DashboardRow::isAnniversaryToday).count();

        model.addAttribute("family", family);
        model.addAttribute("rows", rows);
        model.addAttribute("totalMembers", allRows.size());
        model.addAttribute("birthdayCount", birthdayCount);
        model.addAttribute("anniversaryCount", anniversaryCount);
        return "today";
    }

    @GetMapping("/dashboard/{code}/export")
    public ResponseEntity<String> exportCsv(@PathVariable("code") String code) {
        Family family = familyService.findFamilyByCode(code);
        if (family == null) {
            return ResponseEntity.notFound().build();
        }

        StringBuilder csv = new StringBuilder();
        csv.append("first_name,surname,phone,dob,anniversary\n");
        List<Contact> contacts = familyService.getFamilyContacts(family.getId());
        for (Contact c : contacts) {
            csv.append(escapeCsv(c.getFirstName())).append(",")
                    .append(escapeCsv(c.getSurname())).append(",")
                    .append(escapeCsv(c.getPhone())).append(",")
                    .append(escapeCsv(c.getDob() == null ? "" : c.getDob().toString())).append(",")
                    .append(escapeCsv(c.getAnniversary() == null ? "" : c.getAnniversary().toString()))
                    .append("\n");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=family-contacts.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }

    private LocalDate parseDate(String value, String label, List<String> errors) {
        if (value == null || value.isBlank()) {
            errors.add(label + " is required");
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception ex) {
            errors.add(label + " is not a valid date");
            return null;
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.replace("\"", "\"\"");
        if (cleaned.contains(",") || cleaned.contains("\"") || cleaned.contains("\n")) {
            return "\"" + cleaned + "\"";
        }
        return cleaned;
    }
}
