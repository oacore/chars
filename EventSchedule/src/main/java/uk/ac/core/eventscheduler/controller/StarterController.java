package uk.ac.core.eventscheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.eventscheduler.broker.EventSchedulerQueueBroker;
import uk.ac.core.eventscheduler.periodic.DocumentDownloadChecker;
import uk.ac.core.eventscheduler.periodic.NewRepositoryFirstHarvesting;
import uk.ac.core.eventscheduler.periodic.ScheduleCrossrefFresh;
import uk.ac.core.eventscheduler.service.TaskHistoryMigrationService;

/**
 *
 * @author mc26486
 */
@RestController
public class StarterController {

    @Autowired
    EventSchedulerQueueBroker eventSchedulerQueueBroker;

    @Autowired
    ScheduleCrossrefFresh scheduleCrossrefFresh;

    @Autowired
    NewRepositoryFirstHarvesting newRepositoryFirstHarvesting;

    @Autowired
    DocumentDownloadChecker documentDownloadChecker;

    @Autowired
    TaskHistoryMigrationService taskHistoryMigrationService;

    @RequestMapping("/start")
    public String start() throws CHARSException {
        eventSchedulerQueueBroker.scheduleNewOrOutdatedTasks();
        return "...and good luck";
    }

    @RequestMapping("/test")
    public void test() throws CHARSException {
        scheduleCrossrefFresh.scheduleCrossrefFresh();
    }

    @RequestMapping("/crossreffresh")
    public void scheduleCrossrefFresh() throws CHARSException {
       scheduleCrossrefFresh.scheduleCrossrefFresh();
    }

    @RequestMapping("/insertUnharvested")
    public void insertUnharvestedIntoScheduledRepository()  {
        newRepositoryFirstHarvesting.scheduleNewRepositories();
    }

    @RequestMapping("/checkDD")
    public void checkDD() throws CHARSException {
        documentDownloadChecker.reharvestDocumentDownloadIncrementalTasks();
    }

    @RequestMapping("/runMigration")
    public void runMigration(){
        taskHistoryMigrationService.runDatesMigration();
    }

}
