package uk.ac.core.itemclouduploader.database.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.itemclouduploader.database.CloudPdfUrlDAO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mc26486
 */
@Service
public class MySqlCloudPdfUrlDAO implements CloudPdfUrlDAO{

    @Autowired 
    private JdbcTemplate jdbcTemplate;
    
    private static final String INSERT_QUERY = "INSERT INTO cloud_pdf_url (id_document, url) VALUES (?,?)  ON DUPLICATE KEY UPDATE url=?;";
    
    @Override
    public Boolean save(Integer idDocument, String url) {
        int updatedRows = this.jdbcTemplate.update(INSERT_QUERY, new Object[]{idDocument, url, url});
        return updatedRows>0;
    }
    
}
