package uk.ac.core.common.model.task;

import com.google.gson.Gson;
import java.io.Serializable;
import uk.ac.core.common.model.task.parameters.HarvestTaskParameters;

/**
 *
 * @author mc26486
 */
public class HarvestTaskDescription extends TaskDescription implements Serializable{

    public HarvestTaskDescription(HarvestTaskParameters harvestTaskParameters) {
        super();
        this.setType(TaskType.HARVEST);
        this.setTaskParameters(new Gson().toJson(harvestTaskParameters));
    }

}
