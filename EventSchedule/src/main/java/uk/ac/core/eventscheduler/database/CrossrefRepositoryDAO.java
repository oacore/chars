package uk.ac.core.eventscheduler.database;

import org.apache.commons.lang3.tuple.Pair;

import java.sql.Date;
import java.util.List;

public interface CrossrefRepositoryDAO {

    Date getLastUpdateTime();

    List<Pair<String, Date>> getUnsuccessfulTasks(String taskType, Date fromDate, Date toDate);
}
