package uk.ac.core.database.service.updates;

import uk.ac.core.common.model.legacy.ActionType;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskStatus;
import uk.ac.core.database.model.TaskUpdate;
import uk.ac.core.database.model.TaskUpdateReporting;
import uk.ac.core.database.model.TaskUpdateStatus;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lucasanastasiou
 */
public interface TaskUpdatesDAO {

    public TaskUpdate getLastRepositoryUpdateByRepositoryId(Integer repositoryId);

    public TaskUpdate getLastRepositoryUpdateByRepositoryId(Integer repositoryId, TaskUpdateStatus status);

    public TaskUpdate getLastRepositoryUpdateByRepositoryId(
            Integer repositoryId, TaskUpdateStatus status, ActionType type);

    public TaskUpdate getLastRepositoryUpdateByRepositoryId(
            Integer repositoryId, TaskUpdateStatus status, ActionType type, Date date);

    public void testCon();

    public List<Integer> getOutdatedUKRepositories(Integer limit);

    public List<Integer> getOutdatedNonUKRepositories(Integer limit);

    public void saveTaskUpdate(final TaskStatus taskStatus, final TaskDescription taskDescription);

    public void saveSingleItemTaskUpdate(final TaskDescription taskDescription, final TaskItemStatus taskItemStatus);

    public List<TaskUpdateReporting> getUpdatesOfTheDayForReporting();

    Double getAverageFreshnessWithCountryCode(String countryCode);

    Double getAverageFreshness();

    List<Map<String, Object>> getRepositoriesWithBadFreshness();
}
