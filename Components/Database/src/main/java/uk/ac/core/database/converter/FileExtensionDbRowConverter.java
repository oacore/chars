package uk.ac.core.database.converter;

import uk.ac.core.database.entity.FileExtensionType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class FileExtensionDbRowConverter implements AttributeConverter<FileExtensionType, String> {

    @Override
    public String convertToDatabaseColumn(FileExtensionType fileExtensionType) {
        return fileExtensionType.toString();
    }

    @Override
    public FileExtensionType convertToEntityAttribute(String s) {
        return FileExtensionType.fromNameEqualsIgnoreCase(s);
    }
}