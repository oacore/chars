package uk.ac.core.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "language")
public class Language {

    @Id
    @Column(name = "id_language")
    private int id_language;

    @Column(length = 20)
    private String code;

    @Column(length = 60)
    private String name;

    @Column(name = "iso_639-3", length = 3)
    private String iso639part3;

    @Column(name = "iso_639-2b", length = 3)
    private String iso639part2b;

    @Column(name = "iso_639-2t", length = 3)
    private String iso639part2t;


    public Language() {
    }

    public int getId_language() {
        return id_language;
    }

    public void setId_language(int id_language) {
        this.id_language = id_language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIso639part3() {
        return iso639part3;
    }

    public void setIso639part3(String iso639part3) {
        this.iso639part3 = iso639part3;
    }

    public String getIso639part2b() {
        return iso639part2b;
    }

    public void setIso639part2b(String iso639part2b) {
        this.iso639part2b = iso639part2b;
    }

    public String getIso639part2t() {
        return iso639part2t;
    }

    public void setIso639part2t(String iso639part2t) {
        this.iso639part2t = iso639part2t;
    }
}
