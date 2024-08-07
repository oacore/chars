/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.repositoryDepositData;

import java.util.TreeMap;
import org.springframework.stereotype.Service;

/**
 *
 * @author samuel
 */
public interface RepositoryDepositData {

    void setRepositoryDepositData(int id_repository, TreeMap<String, Integer> history, TreeMap<String, Integer> cumulativeHistory);
    
}
