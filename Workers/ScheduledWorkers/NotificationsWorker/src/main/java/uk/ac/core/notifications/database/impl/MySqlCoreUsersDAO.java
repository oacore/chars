package uk.ac.core.notifications.database.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.notifications.database.CoreUsersDAO;

@Service
public class MySqlCoreUsersDAO implements CoreUsersDAO {
    private static final Logger log = LoggerFactory.getLogger(MySqlCoreUsersDAO.class);

    private final JdbcTemplate template;

    @Autowired
    public MySqlCoreUsersDAO(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public String findEmail(int userId) {
        String sql = "" +
                "select cu.email " +
                "from core_users cu " +
                "where cu.id = ?";
        return this.template.queryForObject(sql, String.class, userId);
    }
}
