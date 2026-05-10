package com.example.familybot.repository;

import com.example.familybot.model.Family;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class FamilyRepository {
    private final JdbcTemplate jdbcTemplate;

    public FamilyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Family> familyRowMapper = (rs, rowNum) -> new Family(
            rs.getLong("id"),
            rs.getString("family_name"),
            rs.getString("join_code"));

    public Family createFamily(String familyName, String joinCode) {
        String sql = "INSERT INTO families (family_name, join_code) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, familyName);
            ps.setString(2, joinCode);
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey() == null ? null : keyHolder.getKey().longValue();
        return new Family(id, familyName, joinCode);
    }

    public Family findFamilyByCode(String joinCode) {
        String sql = "SELECT id, family_name, join_code FROM families WHERE join_code = ?";
        return jdbcTemplate.query(sql, familyRowMapper, joinCode)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public Family findFamilyByName(String familyName) {
        String sql = "SELECT id, family_name, join_code FROM families WHERE family_name = ?";
        return jdbcTemplate.query(sql, familyRowMapper, familyName)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
