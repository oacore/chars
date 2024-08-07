package uk.ac.core.dataprovider.logic.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * History repository entity.
 */
@Entity
@Table(name = "repository_history")
public class RepositoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column(name = "id_repository")
    private long repositoryId;

    @Column(name = "historic_url")
    private String historicUrl;

    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now();

    public RepositoryHistory() {

    }

    public RepositoryHistory(int repositoryId, String historicUrl, LocalDate createdAt) {
        this.repositoryId = repositoryId;
        this.historicUrl = historicUrl;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHistoricUrl() {
        return historicUrl;
    }

    public void setHistoricUrl(String historicUrl) {
        this.historicUrl = historicUrl;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
    }
}