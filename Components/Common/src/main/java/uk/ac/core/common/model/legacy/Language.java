package uk.ac.core.common.model.legacy;

/**
 *
 * @author lucasanastasiou
 */
public class Language {

    private Integer languageId;
    private String code;
    private String name;

    public Language(){}
    public Language(Integer languageId, String code, String name) {
        this.languageId = languageId;
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public String getName() {
        return name;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Language{" + "languageId=" + languageId + ", code=" + code + ", name=" + name + '}';
    }
    
}
