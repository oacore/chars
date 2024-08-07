package uk.ac.core.workermetrics.data.converter;

import uk.ac.core.workermetrics.data.state.RepositoryPriority;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Repository priority flag attribute converter.
 */
@Converter(autoApply = true)
public class RepoPriorityAttributeConverter implements AttributeConverter<RepositoryPriority, String> {

    @Override
    public String convertToDatabaseColumn(RepositoryPriority repoPriority) {
        return repoPriority.name();
    }

    @Override
    public RepositoryPriority convertToEntityAttribute(String columnValue) {
        return RepositoryPriority.valueOf(columnValue);
    }
}