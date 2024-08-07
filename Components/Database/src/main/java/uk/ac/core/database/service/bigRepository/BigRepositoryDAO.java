package uk.ac.core.database.service.bigRepository;

import org.apache.commons.lang3.tuple.Pair;
import uk.ac.core.database.model.BigRepoHarvestingStatistic;

import java.util.Date;
import java.util.List;

public interface BigRepositoryDAO {

    Pair<Date, Date> getLastSuccessDates(Integer repositoryId);

    void updateLastHarvestingDate(Integer repositoryId, Date date);

    List<BigRepoHarvestingStatistic> getBigRepoHarvestingSuccessStatistic();
}
