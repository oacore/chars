/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.languages.impl;

import java.util.HashMap;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.Language;
import uk.ac.core.database.languages.LanguageDAO;
import uk.ac.core.database.model.mappers.LanguageMapper;

/**
 *
 * @author mc26486
 */
@Service
public class MySqlLanguageDAO implements LanguageDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    HashMap<String, Optional<Integer>> cacheCode = new HashMap<>();
    HashMap<String, Optional<Integer>> cacheIso639_3 = new HashMap<>();

    @Override
    public Optional<Integer> getIdByCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        String selectLanguage = "SELECT id_language FROM `language` WHERE code = ?";
        try {
            return Optional.ofNullable(
                    this.jdbcTemplate.queryForObject(selectLanguage, new Object[]{code}, Integer.class));
        } catch (EmptyResultDataAccessException ex) {
            // Suppress Message
            return Optional.empty();
        }
    }

    @Override
    public Optional<Integer> getIdByIso639_3(String iso639_3) {
        String selectLanguage = "SELECT id_language FROM `language` WHERE `iso_639-3` = ?";
        try {
            return Optional.of(this.jdbcTemplate.queryForObject(selectLanguage, new Object[]{iso639_3}, Integer.class));
        } catch (EmptyResultDataAccessException ex) {
            // Supress Message
            return Optional.empty();
        }
    }

    @Override
    public Optional<Language> getByPartialName(String name) {
        String sql = "SELECT * FROM `language` WHERE `name` = ? LIMIT 1";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new LanguageMapper(), name));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<Integer> cachedGetIdByCode(String code) {
        if (this.cacheCode.containsKey(code)) {
            return this.cacheCode.get(code);
        } else {
            Optional<Integer> result = this.getIdByCode(code);
            this.cacheCode.put(code, result);
            return result;
        }
    }

    public Optional<Integer> cachedGetIdByIso639_3(String code) {
        if (this.cacheIso639_3.containsKey(code)) {
            return this.cacheIso639_3.get(code);
        } else {
            Optional<Integer> result = this.getIdByIso639_3(code);
            this.cacheIso639_3.put(code, result);
            return result;
        }
    }

    @Override
    public void insertLanguageForDocumentBy2LetterCountryCode(final Integer documentId, final String code) {
        this.cachedGetIdByCode(code).ifPresent(value -> {
            String updateDocument = "UPDATE document SET id_language = ? WHERE id_document = ?";
            this.jdbcTemplate.update(updateDocument, new Object[]{value, documentId});
        });

    }

    @Override
    public void insertLanguageForDocumentByIso639_2Code(Integer documentId, String iso639_2) {
        this.cachedGetIdByIso639_3(iso639_2).ifPresent(value -> {
            String updateDocument = "UPDATE document SET id_language = ? WHERE id_document = ?";
            this.jdbcTemplate.update(updateDocument, new Object[]{value, documentId});
        });
    }

    @Override
    public Language getById(Integer id) {
        String sql = "" +
                "select * " +
                "from language l " +
                "where l.id_language = ?";
        return this.jdbcTemplate.queryForObject(sql, new LanguageMapper(), id);
    }
}
