package uk.ac.core.eventscheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.workermetrics.data.entity.ScheduledRepository;
import uk.ac.core.workermetrics.data.dao.scheduling.SchedulingRepositoryDAO;
import uk.ac.core.workermetrics.data.state.WatchedRepository;
import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
@RestController
public class DataController {

    @Autowired
    SchedulingRepositoryDAO schedulingRepositoryDAO;

    @RequestMapping(method = RequestMethod.GET, path = {"/data"})
    public @ResponseBody
    List<ScheduledRepository> exportData() {
        return schedulingRepositoryDAO.getAllOrderedBySchedulingScore();
    }

    @RequestMapping(method = RequestMethod.GET, path = {"/data/ongoing"})
    public @ResponseBody
    List<ScheduledRepository> exportOngoingData() {
        return schedulingRepositoryDAO.getOngoingOrderedBySchedulingScore();
    }

    @RequestMapping(method = RequestMethod.GET, path = {"/data/next"})
    public @ResponseBody
    List<ScheduledRepository> exportNextData() {
        return schedulingRepositoryDAO.getNextInLineOrderedBySchedulingScore();
    }

    @RequestMapping(method = RequestMethod.GET, path = {"/data/update-watch-status/{idRepository}/status/{watchedRepositoryStatus}"})
    public @ResponseBody
    String updateWatchStatus(@PathVariable("idRepository") int idRepository, @PathVariable("watchedRepositoryStatus") String watchedRepository) {
        schedulingRepositoryDAO.updateWatchedStatus(idRepository, WatchedRepository.valueOf(watchedRepository).getFlag());
        System.out.println("watchedRepository = " + watchedRepository);
        System.out.println("idRepository = " + idRepository);

        return "Yey!";
    }
}
