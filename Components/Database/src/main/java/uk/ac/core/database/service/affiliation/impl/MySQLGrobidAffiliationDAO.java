/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.affiliation.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.GrobidAffiliationAuthor;
import uk.ac.core.common.model.GrobidAffiliationInstitution;
import uk.ac.core.common.model.GrobidAffiliationInstitutionRelAuthor;
import uk.ac.core.database.model.mappers.GrobidAffiliationAuthorMapper;
import uk.ac.core.database.model.mappers.GrobidAffiliationInstRelAuthorMapper;
import uk.ac.core.database.service.affiliation.GrobidAffiliationDAO;
import uk.ac.core.database.service.control.tool.DBControlTools;
/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
@Service
public class MySQLGrobidAffiliationDAO implements GrobidAffiliationDAO{
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    DBControlTools GrobidDBTool;

    private Logger logger = LoggerFactory.getLogger(MySQLGrobidAffiliationDAO.class);
    
    @Override
    public Integer insertGrobidAffiliationInstitution(final GrobidAffiliationInstitution institution) {
        final String SQL = "INSERT INTO `core`.`grobid_affiliation_institution` (`institution_id`, `name`,"
                + "address, departments_str, labs_str, country, id_document) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE name=?,address=?,departments_str=?,labs_str=? ";
        KeyHolder holder = new GeneratedKeyHolder();
        
        if(institution.getName().isEmpty()) return null;
        
        try {
            int row = this.jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, institution.getInstitutionId());
                    GrobidDBTool.checkStatementProperty(ps, 2, institution.getName());
                    GrobidDBTool.checkStatementProperty(ps, 3, institution.getAddress());
                    GrobidDBTool.checkStatementProperty(ps, 4, institution.getGrobidAffiliationDepartmentsStr());
                    GrobidDBTool.checkStatementProperty(ps, 5, institution.getGrobidAffiliationLabsStr());
                    GrobidDBTool.checkStatementProperty(ps, 6, institution.getCountry());
                    GrobidDBTool.checkStatementProperty(ps, 7, institution.getDocumentId());
                    GrobidDBTool.checkStatementProperty(ps, 8, institution.getName());
                    GrobidDBTool.checkStatementProperty(ps, 9, institution.getAddress());
                    GrobidDBTool.checkStatementProperty(ps, 10, institution.getGrobidAffiliationDepartmentsStr());
                    GrobidDBTool.checkStatementProperty(ps, 11, institution.getGrobidAffiliationLabsStr());
                    return ps;
                }
            }, holder);
            
            if(row > 0){
                return row;
            }
            return null;
        } catch (DataAccessException ex) {
            logger.error("Exception while updating grobid_affiliation_institution!", ex);
            return null;
        }
    }
    
    @Override
    public Integer insertGrobidAffiliationAuthor(final GrobidAffiliationAuthor author) {
        final String SQL = "INSERT INTO `core`.`grobid_affiliation_author` (`forename_first`,"
                + "forename_middle, surname, contact, rolename) "
                + "VALUES (?, ?, ?, ?, ?) ";
        KeyHolder holder = new GeneratedKeyHolder();
        
        if(author == null) return null;
        if(author.getSurname() == null) return null;
        if(author.getSurname().isEmpty()) return null;
        
        try {
            this.jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                    GrobidDBTool.checkStatementProperty(ps, 1, author.getForenameFirst());
                    GrobidDBTool.checkStatementProperty(ps, 2, author.getForenameMiddle());
                    GrobidDBTool.checkStatementProperty(ps, 3, author.getSurname());
                    GrobidDBTool.checkStatementProperty(ps, 4, author.getContact());
                    GrobidDBTool.checkStatementProperty(ps, 5, author.getRolename());
                    return ps;
                }
            }, holder);
            
            return holder.getKey().intValue();
        } catch (DataAccessException ex) {
            logger.error("Exception while updating grobid_affiliation_author!", ex);
            return null;
        }
    }
    
    @Override
    public Integer insertGrobidAffiliationInstRelAuthor(final GrobidAffiliationInstitutionRelAuthor relation) {
        final String SQL = "INSERT INTO `core`.`grobid_affiliation_institution_rel_author` (`institution_id`, `author_id`) "
                + "VALUES (?, ?) ";
        KeyHolder holder = new GeneratedKeyHolder();
        
        try {
            this.jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                    GrobidDBTool.checkStatementProperty(ps, 1, relation.getInstitutionId());
                    GrobidDBTool.checkStatementProperty(ps, 2, relation.getAuthorId());
                    return ps;
                }
            }, holder);
            
            return holder.getKey().intValue();
        } catch (DataAccessException ex) {
            logger.error("Exception while updating grobid_affiliation_institution_rel_author!", ex);
            return null;
        }
    }
    
    @Override
    public List<GrobidAffiliationInstitutionRelAuthor> getInstitutionRelationsAuthor(GrobidAffiliationInstitutionRelAuthor relation) {
        String sql = "SELECT * FROM grobid_affiliation_institution_rel_author WHERE "
                + GrobidDBTool.createNullCheckedClause("institution_id", relation.getInstitutionId())
                + " AND " + GrobidDBTool.createNullCheckedClause("author_id", relation.getAuthorId());
        List<GrobidAffiliationInstitutionRelAuthor> institutionRelationsAuthor = jdbcTemplate.query(sql, new GrobidAffiliationInstRelAuthorMapper(), 
                new Integer[]{ relation.getInstitutionId(), relation.getAuthorId()});
        return institutionRelationsAuthor;
    }
    
    @Override
    public List<GrobidAffiliationAuthor> getAuthors(GrobidAffiliationAuthor wantedAuthor) {
        String sql = "SELECT * FROM grobid_affiliation_author WHERE "
                + GrobidDBTool.createNullCheckedClause("forename_first", wantedAuthor.getForenameFirst())
                + " AND " + GrobidDBTool.createNullCheckedClause("forename_middle", wantedAuthor.getForenameMiddle())
                + " AND " + GrobidDBTool.createNullCheckedClause("surname", wantedAuthor.getSurname());
        List<GrobidAffiliationAuthor> grobidAffiliationAuthors = jdbcTemplate.query(sql, new GrobidAffiliationAuthorMapper(), 
                new String[]{ wantedAuthor.getForenameFirst(), wantedAuthor.getForenameMiddle(), wantedAuthor.getSurname()});
        return grobidAffiliationAuthors;
    }
}
