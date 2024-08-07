package uk.ac.core.workermetrics.data.converter;

import uk.ac.core.workermetrics.data.state.ScheduledState;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Scheduled state attribute converter.
 */
@Converter(autoApply = true)
public class ScheduledStateAttributeConverter implements AttributeConverter<ScheduledState, String> {

    @Override
    public String convertToDatabaseColumn(ScheduledState scheduledState) {
        return scheduledState.name();
    }

    @Override
    public ScheduledState convertToEntityAttribute(String columnValue) {
        return ScheduledState.valueOf(columnValue);
    }
}