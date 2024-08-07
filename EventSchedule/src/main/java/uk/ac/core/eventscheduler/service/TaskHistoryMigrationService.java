package uk.ac.core.eventscheduler.service;


import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.bigRepository.MySQLBigRepositoryDAO;
import uk.ac.core.database.service.migration.TaskHistoryMigrationHelper;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class TaskHistoryMigrationService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(TaskHistoryMigrationService.class);

    @Autowired
    private MySQLBigRepositoryDAO mySQLBigRepositoryDAO;

    @Autowired
    private BigRepositoryService bigRepositoryService;


    public void runDatesMigration(){
      List<Integer> bigRepos = bigRepositoryService.getBigRepositories();
      for (Integer repositoryId: bigRepos){
          logger.info("Run migration for repository {}", repositoryId);
          List<Pair<Integer, String>> taskHistoriesForUpdate =
                  mySQLBigRepositoryDAO.getTaskHistoryRecordsForDatesMigration(repositoryId);

          if(!taskHistoriesForUpdate.isEmpty()){
              for (Pair<Integer, String> taskHistoryItem: taskHistoriesForUpdate){
                  logger.info("Run updating for taskHistoryId {}", taskHistoryItem.getKey());
                  Pair<LocalDateTime, LocalDateTime> dates =
                          TaskHistoryMigrationHelper.getIncrementalDatesForMigration(taskHistoryItem.getValue());
                  LocalDateTime fromDate = dates.getKey();
                  LocalDateTime toDate = dates.getValue();
                  mySQLBigRepositoryDAO.updateDates(taskHistoryItem.getKey(), fromDate, toDate, repositoryId);
              }
          }

      }
    }


}
