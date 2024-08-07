package uk.ac.core.database.service.citation.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import uk.ac.core.common.model.legacy.Citation;
import uk.ac.core.database.model.mappers.CitationMapper;
import uk.ac.core.database.service.citation.CitationDAO;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class MySQLCitationDAO implements CitationDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private Logger logger = LoggerFactory.getLogger("MySQLCitationDAO");

    @Override
    public List<Citation> getCitations(Integer articleId) {
        String sql = "SELECT * FROM citation WHERE id_cites = ?";
        List<Citation> citations = jdbcTemplate.query(sql, new CitationMapper(), articleId);
        return citations;
    }
    
}
