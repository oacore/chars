package uk.ac.core.eventscheduler.service;

import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class CrossrefReharvestingService<T extends RepositoryTaskParameters> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    protected final Gson gson = new Gson();

    public void rerun(String fromStr, String toStr) throws ParseException {
        Date from = new Date(format.parse(fromStr).getTime());
        Date to = new Date(format.parse(toStr).getTime());

        rerunUnsuccessfulTaskParams(from, to);
    }

    protected void rerunUnsuccessfulTaskParams(Date from, Date to) {
        List<Pair<T, Date>> unsuccessfulTasks = getUnsuccessfulTasks(from, to);
        logger.info("Founded {} unsuccessful tasks", unsuccessfulTasks.size());

        unsuccessfulTasks.stream()
                .filter(t -> t.getLeft().getFromDate() != null && t.getLeft().getToDate() != null)
                .map(Pair::getLeft)
                .forEach(this::sendRequest);

        unsuccessfulTasks.stream()
                .filter(t -> t.getLeft().getFromDate() != null && t.getLeft().getToDate() == null)
                .peek(t -> t.getLeft().setToDate(t.getRight()))
                .map(Pair::getLeft)
                .collect(Collectors.groupingBy(RepositoryTaskParameters::getFromDate))
                .entrySet().stream()
                .map(e -> e.getValue().stream().max(Comparator.comparing(T::getToDate)).orElse(null))
                .filter(Objects::nonNull)
                .forEach(this::sendRequest);
    }

    protected abstract List<Pair<T, Date>> getUnsuccessfulTasks(Date fromDate, Date toDate);

    protected abstract void sendRequest(T parameter);
}
