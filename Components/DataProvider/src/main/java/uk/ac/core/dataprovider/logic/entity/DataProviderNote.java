/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.dataprovider.logic.entity;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author mc26486
 */
@Entity
@Table(name = "repository_notes")
public class DataProviderNote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column(name = "id_repository")
    private Long idRepository;
    @Column(name = "note")
    private String note;
    @Column(name = "added")
    private LocalDate added;

    public DataProviderNote() {
    }

    public DataProviderNote(Long idRepository, String note, LocalDate added) {
        this.idRepository = idRepository;
        this.note = note;
        this.added = added;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdRepository() {
        return idRepository;
    }

    public void setIdRepository(Long idRepository) {
        this.idRepository = idRepository;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getAdded() {
        return added;
    }

    public void setAdded(LocalDate added) {
        this.added = added;
    }

    @Override
    public String toString() {
        return "RepositoryNote{" + "id=" + id + ", idRepository=" + idRepository + ", note=" + note + ", added=" + added + '}';
    }

}
