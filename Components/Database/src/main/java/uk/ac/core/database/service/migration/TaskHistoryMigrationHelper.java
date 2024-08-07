package uk.ac.core.database.service.migration;

import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TaskHistoryMigrationHelper {
    public static Pair<LocalDateTime, LocalDateTime> getIncrementalDatesForMigration(String taskParameters){

        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;

        if (taskParameters != null && !taskParameters.isEmpty()) {
            RepositoryTaskParameters repositoryTaskParameters = new Gson().fromJson(taskParameters, RepositoryTaskParameters.class);

            if (repositoryTaskParameters.getFromDate() != null) {
                fromDate = new Timestamp(repositoryTaskParameters.getFromDate().getTime()).toLocalDateTime();
            }

            if(repositoryTaskParameters.getToDate() != null){
                toDate = new Timestamp(repositoryTaskParameters.getToDate().getTime()).toLocalDateTime();
            }
        }
        return Pair.of(fromDate, toDate);
    }

    public static Integer getRepositoryIdForMigration(String taskParameters){

        Integer repositoryId = null;

        if (taskParameters != null && !taskParameters.isEmpty()){
            RepositoryTaskParameters repositoryTaskParameters = new Gson().fromJson(taskParameters, RepositoryTaskParameters.class);

            if(repositoryTaskParameters.getRepositoryId() != null){
                repositoryId = repositoryTaskParameters.getRepositoryId();
            }
        }
        return repositoryId;
    }
}
