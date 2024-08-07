package uk.ac.core.eventscheduler.service;

import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.database.service.repositories.impl.MySQLRepositoriesDAO;
import uk.ac.core.database.service.bigRepository.MySQLBigRepositoryDAO;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BigRepositoryService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(BigRepositoryService.class);

    @Autowired
    private MySQLBigRepositoryDAO mySQLBigRepositoryDAO;

    @Autowired
    private MySQLRepositoriesDAO mySQLRepositoriesDAO;

    private RepositoryTaskParameters repositoryTaskParameters;



    public Pair<Date, Date> getIncrementalHarvestingDates(Pair<Date, Date> dates){

        Date fromDate = Date.from(LocalDate.now().minusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date toDate = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (dates != null) {
            if (dates.getKey() != null && dates.getValue() != null) {

                LocalDateTime lastToDate = new Timestamp(dates.getValue().getTime()).toLocalDateTime();

                if (lastToDate.isAfter(LocalDateTime.now().minusDays(3))) {
                    return null;
                }

                fromDate = Timestamp.valueOf(lastToDate.plusDays(1));
                toDate = Timestamp.valueOf(lastToDate.plusDays(3));

                if(lastToDate.isBefore(LocalDateTime.now().minusDays(5))){
                    toDate = Timestamp.valueOf(lastToDate.plusDays(5));
                }
            }
        }

        return Pair.of(fromDate, toDate);
    }

    public Pair<Date,Date> getReharvestedDDDates(String taskParameters){
        Date fromDate = Date.from(LocalDate.now().minusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date toDate = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (taskParameters != null && !taskParameters.isEmpty()) {
            this.repositoryTaskParameters = new Gson().fromJson(taskParameters, RepositoryTaskParameters.class);

            if (repositoryTaskParameters.getFromDate() != null && repositoryTaskParameters.getToDate() != null) {

                LocalDateTime lastToDate = repositoryTaskParameters.getToDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                LocalDateTime lastFromDate = repositoryTaskParameters.getFromDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();


                fromDate = Timestamp.valueOf(lastFromDate);
                toDate = Timestamp.valueOf(lastToDate);
            }
        }

        return Pair.of(fromDate, toDate);
    }

    public List<Integer> getBigRepositories() {
        return mySQLRepositoriesDAO.getBigRepositories();
    }

    public Pair<Date, Date> getIncrementalHarvestingDates(Integer repositoryId){
        Pair<Date, Date> lastSuccessDates = mySQLBigRepositoryDAO.getLastSuccessDates(repositoryId);
        return getIncrementalHarvestingDates(lastSuccessDates);

    }

    public List<Pair<Date, Date>> getIncrementalHarvestingDatesForFailedDD(Integer repositoryId, Date date){
        List<String> taskParameters = mySQLBigRepositoryDAO.getTaskParametersForFailedDD(repositoryId, date);

        List<Pair<Date, Date>> allDates = new ArrayList<>();
        for(String taskParameter : taskParameters){
           allDates.add(getReharvestedDDDates(taskParameter));
        }
        return allDates;
    }
    
}
