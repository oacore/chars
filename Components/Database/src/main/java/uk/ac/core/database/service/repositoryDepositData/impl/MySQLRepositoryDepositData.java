/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.repositoryDepositData.impl;

import com.google.gson.Gson;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.repositoryDepositData.RepositoryDepositData;

/**
 *
 * @author samuel
 */
@Service
public class MySQLRepositoryDepositData implements RepositoryDepositData {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void setRepositoryDepositData(int id_repository, TreeMap<String, Integer> history, TreeMap<String, Integer> cumulativeHistory) {
        Gson gson = new Gson();
        this.jdbcTemplate.update(
                "REPLACE INTO repository_deposit_data (id_repository, history, cumulative_history) VALUES (?, ?, ?)",
                id_repository,
                gson.toJson(history),
                gson.toJson(cumulativeHistory)
        );
    }
}
