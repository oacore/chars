/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.citation.impl;

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
import uk.ac.core.common.model.GrobidCitation;
import uk.ac.core.common.model.GrobidCitationAuthor;
import uk.ac.core.common.model.GrobidCitationBiblScope;
import uk.ac.core.common.model.GrobidCitationRelAuthor;
import uk.ac.core.common.model.legacy.Citation;
import uk.ac.core.database.model.mappers.CitationMapper;
import uk.ac.core.database.model.mappers.GrobidCitationAuthorMapper;
import uk.ac.core.database.model.mappers.GrobidCitationRelAuthorMapper;
import uk.ac.core.database.service.citation.GrobidCitationDAO;
import uk.ac.core.database.service.control.tool.DBControlTools;


/**
 *
 * @author vb4826
 */
@Service
public class MySQLGrobidCitationDAO implements GrobidCitationDAO {
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    DBControlTools GrobidDBTool;

    private Logger logger = LoggerFactory.getLogger("MySQLGrobidCitationDAO");
    
    @Override
    public List<Citation> getCitationsByDOI(String doi) {
        String sql = "SELECT * FROM citation WHERE `doi` = ? AND `id_cited` IS NOT NULL";
        List<Citation> citations = jdbcTemplate.query(sql, new CitationMapper(), doi);
        return citations;
    }
    
    @Override
    public List<GrobidCitationAuthor> getAuthors(GrobidCitationAuthor wantedAuthor) {
        String sql = "SELECT * FROM grobid_citation_author WHERE "
                + GrobidDBTool.createNullCheckedClause("forename_first", wantedAuthor.getForenameFirst())
                + " AND " + GrobidDBTool.createNullCheckedClause("forename_middle", wantedAuthor.getForenameMiddle())
                + " AND " + GrobidDBTool.createNullCheckedClause("surname", wantedAuthor.getSurname());
        List<GrobidCitationAuthor> grobidCitationAuthors = jdbcTemplate.query(sql, new GrobidCitationAuthorMapper(), 
                new String[]{ wantedAuthor.getForenameFirst(), wantedAuthor.getForenameMiddle(), wantedAuthor.getSurname()});
        return grobidCitationAuthors;
    }
    
    @Override
    public List<GrobidCitationRelAuthor> getCitationAuthorRelations(GrobidCitationRelAuthor relation) {
        String sql = "SELECT * FROM grobid_citation_rel_author WHERE "
                + GrobidDBTool.createNullCheckedClause("citation_id", relation.getCitationId())
                + " AND " + GrobidDBTool.createNullCheckedClause("author_id", relation.getAuthorId());
        List<GrobidCitationRelAuthor> grobidCitationRelAuthors = jdbcTemplate.query(sql, new GrobidCitationRelAuthorMapper(), 
                new Integer[]{ relation.getCitationId(), relation.getAuthorId()});
        return grobidCitationRelAuthors;
    }
    
    @Override
    public Integer insertGrobidCitation(final GrobidCitation c) {
        final Integer keyValue = (c.getArticleId().toString() +  c.getXmlId()).hashCode();
        
        Integer refDocId = null;
        if(c.getDoi() != null){
            List<Citation> citedByList = this.getCitationsByDOI(c.getDoi());
            for(Citation citation : citedByList){
                refDocId = (citation.getRefDocId() != null) ? citation.getRefDocId() : null;
            }
        }
        final Integer citedBy = refDocId;
            
        KeyHolder holder = new GeneratedKeyHolder();
        final String SQL = "INSERT INTO `core`.`grobid_citation` (`id_document`, `xml_id`, `position`, "
                    + "`title`, `title_tr`, `doi`, `address`, `publisher`, `date`, `date_type`,"
                    + "`citation_id`, `id_cited`, `bibl_scope_str`, `author_str`) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE title=?,title_tr=?,doi=?";
        
        String trimming = null;
        if(c.getTitle() != null){
            trimming = (c.getTitle().length() > 1024) ? c.getTitle().substring(0, 1023) : c.getTitle();
        }else{
            logger.warn("Title of article:" + c.getArticleId() + " with citation id:" + c.getXmlId() + " is null, skipping!");
            return null;
        }
        final String title_tr = trimming;
        
        if(c.getAddress() != null){
            trimming = (c.getAddress().length() > 1024) ? c.getAddress().substring(0, 1023) : c.getAddress();
        }else{
            trimming = null;
        }
        final String address_tr = trimming;
        
        String biblScope = c.getGrobidCitationImprint().getGrobidBiblScopesStr();
        if(!biblScope.isEmpty()){
            trimming = (biblScope.length() > 1024) ? biblScope.substring(0, 1023) : biblScope;
        }else{
            trimming = null;
        }
        final String biblScope_tr = trimming;
        
        String publisher = c.getGrobidCitationImprint().getPublisher();
        if(c.getGrobidCitationImprint().getPublisher()!= null){
            trimming = (publisher.length() > 1024) ? publisher.substring(0, 1023) : publisher;
        }else{
            trimming = null;
        }
        final String publisher_tr = trimming;
                
        try {
            int row = this.jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, c.getArticleId());
                    GrobidDBTool.checkStatementProperty(ps, 2, c.getXmlId());
                    GrobidDBTool.checkStatementProperty(ps, 3, c.getPositionInBiblList());
                    GrobidDBTool.checkStatementProperty(ps, 4, c.getTitle());
                    GrobidDBTool.checkStatementProperty(ps, 5, title_tr);
                    GrobidDBTool.checkStatementProperty(ps, 6, c.getDoi());
                    GrobidDBTool.checkStatementProperty(ps, 7, address_tr);
                    GrobidDBTool.checkStatementProperty(ps, 8, publisher_tr);
                    GrobidDBTool.checkStatementProperty(ps, 9, c.getGrobidCitationImprint().getDate());
                    GrobidDBTool.checkStatementProperty(ps, 10, c.getGrobidCitationImprint().getDateType());
                    GrobidDBTool.checkStatementProperty(ps, 11, keyValue);
                    GrobidDBTool.checkStatementProperty(ps, 12, citedBy);
                    GrobidDBTool.checkStatementProperty(ps, 13, biblScope_tr);
                    GrobidDBTool.checkStatementProperty(ps, 14, c.getAuthorsString());
                    GrobidDBTool.checkStatementProperty(ps, 15, c.getTitle());
                    GrobidDBTool.checkStatementProperty(ps, 16, title_tr);
                    GrobidDBTool.checkStatementProperty(ps, 17, c.getDoi());
                    return ps;
                }
            }, holder);

            
            if(row > 0){
                return keyValue;
            }
            return null;
        } catch (DataAccessException ex) {
            logger.error("Exception while updating grobid_citation!", ex);
            return null;
        }
    }
    
    @Override
    public Integer insertGrobidCitationRelAuthor(final GrobidCitationRelAuthor relation) {
        final String SQL = "INSERT INTO `core`.`grobid_citation_rel_author` (`citation_id`, `author_id`) "
                + "VALUES (?, ?) ";
        KeyHolder holder = new GeneratedKeyHolder();
        
        try {
            this.jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, relation.getCitationId());
                    GrobidDBTool.checkStatementProperty(ps, 2, relation.getAuthorId());
                    return ps;
                }
            }, holder);
            
            return holder.getKey().intValue();
        } catch (DataAccessException ex) {
            logger.error("Exception while updating grobid_citation_rel_author!", ex);
            return null;
        }
    }
    
    @Override
    public Integer insertGrobidCitationAuthor(final GrobidCitationAuthor cAuthor) {
        final String SQL = "INSERT INTO `core`.`grobid_citation_author` (`forename_first`, `forename_middle`, `surname`) "
                + "VALUES (?, ?, ?) ";
        KeyHolder holder = new GeneratedKeyHolder();
        
        if(cAuthor.getSurname() == null) return null;
        
        try {
            this.jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                    if(cAuthor.getForenameFirst() != null){
                        ps.setString(1, cAuthor.getForenameFirst());
                    }else{
                        ps.setNull(1, Types.VARCHAR);
                    }
                    GrobidDBTool.checkStatementProperty(ps, 2, cAuthor.getForenameMiddle());
                    GrobidDBTool.checkStatementProperty(ps, 3, cAuthor.getSurname());
                    return ps;
                }
            }, holder);
            
            return holder.getKey().intValue();
        } catch (DataAccessException ex) {
            logger.error("Exception while updating grobid_citation_author!", ex);
            return null;
        }
    }

    @Override
    public void insertGrobidCitationBiblScope(GrobidCitationBiblScope cBiSc, Integer citationId) {
        String SQL = "INSERT INTO `core`.`grobid_citation_bibl_scope` (`citation_id`, `unit`, `type`, `unit_from`, `unit_to`, `content`) "
                + "VALUES (?, ?, ?, ?, ?, ?) ";
        try {
            this.jdbcTemplate.update(SQL, new Object[]{citationId, cBiSc.getUnit(), cBiSc.getType(),
                cBiSc.getUnitFrom(), cBiSc.getUnitTo(), cBiSc.getContent()});
        } catch (DataAccessException ex) {
            logger.error("Exception while updating grobid_citation_bibl_scope!", ex);
        }
    }
}
