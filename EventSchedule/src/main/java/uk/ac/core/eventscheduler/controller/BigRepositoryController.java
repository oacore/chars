package uk.ac.core.eventscheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.eventscheduler.periodic.BigRepositoriesIncrementalHarvestingFreshScheduler;

@Controller
@RequestMapping("harvesting")
public class BigRepositoryController {

    @Autowired
    private BigRepositoriesIncrementalHarvestingFreshScheduler bigRepositoriesIncrementalHarvestingFreshScheduler;

    @RequestMapping("/hartvestBigRepos")
    public void runIncrementalHarvestingForAllBigRepo() throws CHARSException {
        bigRepositoriesIncrementalHarvestingFreshScheduler.scheduleBigRepositories();
    }


}
