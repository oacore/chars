package uk.ac.core.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "file_extension")
public class FileExtension {

    @Id
    @Column(name = "id_document")
    private int id;

    @Column
    private FileExtensionType name;

    public FileExtension() {
    }

    public FileExtension(int id, FileExtensionType name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FileExtensionType getName() {
        return name;
    }

    public void setName(FileExtensionType type) {
        this.name = type;
    }
}
