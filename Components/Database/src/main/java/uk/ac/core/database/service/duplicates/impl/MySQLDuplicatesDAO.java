package uk.ac.core.database.service.duplicates.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.duplicates.DuplicatesDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class MySQLDuplicatesDAO implements DuplicatesDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer getParentId(Integer id) {
        String sql = "SELECT id_parent FROM duplicates WHERE id_duplicate = ?";
        Integer parentId = null;
        try {
            parentId = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return null;
        }
        return parentId;
    }

    @Override
    public List<Integer> getChildrenIds(Integer idParent) {

        String sql = "SELECT id_duplicate FROM duplicates WHERE id_parent = ?";
        List<Integer> childrenIds = null;
        try {
            childrenIds = jdbcTemplate.queryForList(sql, new Object[]{idParent}, Integer.class);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return null;
        }

        return childrenIds;

    }

}
