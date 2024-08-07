package uk.ac.core.baseimport.model;

import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.dataprovider.logic.entity.BaseRepository;


/**
 * Base repository task item.
 */
public final class BaseRepositoryTaskItem implements TaskItem {

    private final BaseRepository baseRepository;

    public BaseRepositoryTaskItem(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    public BaseRepository getBaseRepository() {
        return baseRepository;
    }
}
