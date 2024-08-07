/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.LegacyRepositoryLocation;
import uk.ac.core.database.service.repositories.RepositoriesLocationDAO;

/**
 *
 * @author mc26486
 */
@Service
public class MySQLRepositoriesLocationDAO implements RepositoriesLocationDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void insertOrUpdate(LegacyRepositoryLocation repositoryLocation) {
        String insertSql = "INSERT INTO repository_location (id_repository, country_code, longitude, latitude) "
                + "VALUES(?, ?, ?, ?)  ON DUPLICATE KEY UPDATE country_code = ?, longitude = ?, latitude = ?";
        this.jdbcTemplate.update(insertSql, new Object[]{repositoryLocation.getRepositoryId(), repositoryLocation.getCountryCode(),
            repositoryLocation.getLongitude(), repositoryLocation.getLatitude(),
            repositoryLocation.getCountryCode(), repositoryLocation.getLongitude(), repositoryLocation.getLatitude()
        });
    }

    @Override
    public LegacyRepositoryLocation load(Integer repoId) {
        String selectSql = "SELECT * FROM repository_location WHERE id_repository=?";
        return (LegacyRepositoryLocation) this.jdbcTemplate.queryForObject(selectSql, new Object[]{repoId}, new BeanPropertyRowMapper(LegacyRepositoryLocation.class));
    }

}
