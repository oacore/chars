package uk.ac.core.common.model.util;

import uk.ac.core.common.model.legacy.ActionType;
import uk.ac.core.common.model.task.TaskType;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author lucasanastasiou
 */
public class Converter {

    private static Map<TaskType, ActionType> conversionMap;

    public static String fromTaskTypeToActionTypeString(TaskType taskType) {

        if (conversionMap == null) {
            conversionMap = new EnumMap<>(TaskType.class);
            conversionMap.put(TaskType.METADATA_DOWNLOAD, ActionType.METADATA_DOWNLOAD);
            conversionMap.put(TaskType.EXTRACT_METADATA, ActionType.METADATA_EXTRACT);
            conversionMap.put(TaskType.DOCUMENT_DOWNLOAD, ActionType.DOCUMENT);
            conversionMap.put(TaskType.EXTRACT_TEXT, ActionType.TEXT_EXTRACTION);
            conversionMap.put(TaskType.INDEX, ActionType.INDEX);
            conversionMap.put(TaskType.THUMBNAIL_GENERATION, ActionType.IMAGE_GENERATION);
            conversionMap.put(TaskType.RIOXX_COMPLIANCE, ActionType.RIOXXCOMPLIANCE);
            conversionMap.put(TaskType.MUCC_DOCUMENT_DOWNLOAD, ActionType.DOCUMENT);
        }
        
        String toReturn;
        
        // We don't want to map everything to the Database, only ones we need to
        //    for legacy or custom reasons
        if (conversionMap.containsKey(taskType)) {
            toReturn = conversionMap.get(taskType).toString();
        } else {
            toReturn = taskType.toString();
        }
        
        return toReturn;

    }
}
