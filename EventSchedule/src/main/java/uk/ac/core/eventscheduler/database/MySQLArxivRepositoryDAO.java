package uk.ac.core.eventscheduler.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class MySQLArxivRepositoryDAO implements ArxivRepositoryDAO {

    @Autowired
    private DocumentRepositoryDAO documentRepositoryDAO;

    /**
     *
     * @return Date or null
     */
    @Override
    public Date getLastUpdateTime() {
        return documentRepositoryDAO.getLastUpdateTime(144);
    }
}
