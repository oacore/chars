package uk.ac.core.languagenormalise;

public enum Languages {
    UNDEFINED_LANGUAGE("und"),
    MULTIPLE_LANGUAGES("mul"),
    NO_LINGUISTIC_CONTENT("zxx");

    public final String label;

    Languages(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public Languages fromString(String language) {
        for (Languages type : Languages.values()) {
            if (type.toString().equals(language)) {
                return type;
            }
        }
        return null;
    }
}
