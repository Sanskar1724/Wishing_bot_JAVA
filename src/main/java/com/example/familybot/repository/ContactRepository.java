package com.example.familybot.repository;

import com.example.familybot.model.Contact;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class ContactRepository {
    private final JdbcTemplate jdbcTemplate;

    public ContactRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Contact> contactRowMapper = (rs, rowNum) -> {
        Contact c = new Contact();
        c.setId(rs.getLong("id"));
        c.setFamilyId(rs.getLong("family_id"));
        c.setFirstName(rs.getString("first_name"));
        c.setSurname(rs.getString("surname"));
        c.setPhone(rs.getString("phone"));
        Date dob = rs.getDate("dob");
        Date ann = rs.getDate("anniversary");
        c.setDob(dob == null ? null : dob.toLocalDate());
        c.setAnniversary(ann == null ? null : ann.toLocalDate());
        return c;
    };

    public void saveContact(Contact contact) {
        String sql = "INSERT INTO contacts (family_id, first_name, surname, phone, dob, anniversary) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                contact.getFamilyId(),
                contact.getFirstName(),
                contact.getSurname(),
                contact.getPhone(),
                contact.getDob() == null ? null : Date.valueOf(contact.getDob()),
                contact.getAnniversary() == null ? null : Date.valueOf(contact.getAnniversary()));
    }

    public List<Contact> getFamilyContacts(Long familyId) {
        String sql = "SELECT id, family_id, first_name, surname, phone, dob, anniversary FROM contacts WHERE family_id = ? ORDER BY first_name";
        return jdbcTemplate.query(sql, contactRowMapper, familyId);
    }

    public List<Contact> getTodayContacts(Long familyId) {
        String sql = "SELECT id, family_id, first_name, surname, phone, dob, anniversary " +
                "FROM contacts " +
                "WHERE family_id = ? AND (" +
                "(dob IS NOT NULL AND MONTH(dob) = MONTH(CURDATE()) AND DAY(dob) = DAY(CURDATE())) " +
                "OR " +
                "(anniversary IS NOT NULL AND MONTH(anniversary) = MONTH(CURDATE()) AND DAY(anniversary) = DAY(CURDATE()))"
                +
                ")";
        return jdbcTemplate.query(sql, contactRowMapper, familyId);
    }

    public Contact findByFamilyAndPhone(Long familyId, String phone) {
        String sql = "SELECT id, family_id, first_name, surname, phone, dob, anniversary " +
                "FROM contacts WHERE family_id = ? AND phone = ?";
        return jdbcTemplate.query(sql, contactRowMapper, familyId, phone)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
