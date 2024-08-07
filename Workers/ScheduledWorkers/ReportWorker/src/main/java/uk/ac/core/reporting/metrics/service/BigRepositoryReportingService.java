package uk.ac.core.reporting.metrics.service;

import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.database.model.BigRepoHarvestingStatistic;
import uk.ac.core.database.service.bigRepository.BigRepositoryDAO;
import uk.ac.core.reporting.metrics.service.dto.BigRepositoryMetric;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BigRepositoryReportingService {
    private final BigRepositoryDAO bigRepositoryDAO;

    private RepositoryTaskParameters repositoryTaskParameters;


    @Autowired
    public BigRepositoryReportingService(BigRepositoryDAO bigRepositoryDAO) {
        this.bigRepositoryDAO = bigRepositoryDAO;
    }

    public List<BigRepositoryMetric> getCurrentBigRepositoryMetrics() {
        return bigRepositoryDAO.getBigRepoHarvestingSuccessStatistic().stream()
                .collect(Collectors.groupingBy(BigRepoHarvestingStatistic::getIdRepository))
                .entrySet().stream()
                .map(this::convertToMetric)
                .collect(Collectors.toList());
    }

    private BigRepositoryMetric convertToMetric(Map.Entry<Integer, List<BigRepoHarvestingStatistic>> statisticEntry) {
        BigRepositoryMetric metric = new BigRepositoryMetric();

        metric.setRepositoryId(statisticEntry.getKey());
        metric.setTaskLastSuccessDate(statisticEntry.getValue().stream()
                .collect(Collectors.toMap(
                        BigRepoHarvestingStatistic::getRoutingKey,
                        e -> getDatesFromParameters(e.getTaskParameters()).getRight())));

        return metric;
    }

    private Pair<Date, Date> getDatesFromParameters(String parameters) {
      this.repositoryTaskParameters =  new Gson().fromJson(parameters, RepositoryTaskParameters.class);
      return Pair.of(repositoryTaskParameters.getFromDate(), repositoryTaskParameters.getToDate());
    }
}
