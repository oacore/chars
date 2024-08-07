package uk.ac.core.database.entity;

public enum FileExtensionType {

    WORD_DOC("doc"), WORD_DOCX("docx"), PDF("pdf");

    private final String name;

    FileExtensionType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static FileExtensionType fromNameEqualsIgnoreCase(String str) {
        for (FileExtensionType type : FileExtensionType.values()) {
            if (type.toString().equals(str)) {
                return type;
            }
        }
        return null;
    }
}