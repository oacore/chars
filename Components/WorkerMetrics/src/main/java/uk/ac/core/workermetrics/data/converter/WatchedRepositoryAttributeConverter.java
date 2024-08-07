package uk.ac.core.workermetrics.data.converter;

import uk.ac.core.workermetrics.data.state.WatchedRepository;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Watched repo flag attribute converter.
 */
@Converter(autoApply = true)
public class WatchedRepositoryAttributeConverter implements AttributeConverter<WatchedRepository, Integer> {

    @Override
    public Integer convertToDatabaseColumn(WatchedRepository watchedRepository) {
        return watchedRepository.getFlag();
    }

    @Override
    public WatchedRepository convertToEntityAttribute(Integer columnValue) {
        return WatchedRepository.getByFlag(columnValue);
    }
}