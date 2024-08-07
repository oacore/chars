/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.reporting.metrics.data.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "global_metrics")
public class GlobalMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column(name = "collection_time")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "metadata_number")
    private long metadataCount;

    @Column(name = "fulltext_number")
    private long extractedTextsCount;

    @Column(name = "pdf_number")
    private long downloadedPdfsCount;

    @Column(name = "thumbnail_number")
    private long generatedThumbnailsCount;

    @Column(name = "total_repositories_harvested")
    private long harvestedReposCount;

    private Double freshnessGB;

    @Column(name = "freshnessAll")
    private Double freshness;

    @Column(name = "freshnessDocument")
    private Double documentFreshness;

    @Column(name = "metadata_download_throughput_count")
    private Long downloadedMetadataTasksCount;

    @Column(name = "metadata_extract_throughput_count")
    private Long extractedMetadataTasksCount;

    @Column(name = "document_download_throughput_count")
    private Long downloadedDocumentsTasksCount;

    public GlobalMetrics() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getMetadataCount() {
        return metadataCount;
    }

    public void setMetadataCount(long metadataCount) {
        this.metadataCount = metadataCount;
    }

    public long getExtractedTextsCount() {
        return extractedTextsCount;
    }

    public void setExtractedTextsCount(long extractedTextsCount) {
        this.extractedTextsCount = extractedTextsCount;
    }

    public long getDownloadedPdfsCount() {
        return downloadedPdfsCount;
    }

    public void setDownloadedPdfsCount(long downloadedPdfsCount) {
        this.downloadedPdfsCount = downloadedPdfsCount;
    }

    public long getGeneratedThumbnailsCount() {
        return generatedThumbnailsCount;
    }

    public void setGeneratedThumbnailsCount(long generatedThumbnailsCount) {
        this.generatedThumbnailsCount = generatedThumbnailsCount;
    }

    public long getHarvestedReposCount() {
        return harvestedReposCount;
    }

    public void setHarvestedReposCount(long harvestedReposCount) {
        this.harvestedReposCount = harvestedReposCount;
    }

    public Double getFreshnessGB() {
        return freshnessGB;
    }

    public void setFreshnessGB(Double freshnessGB) {
        this.freshnessGB = freshnessGB;
    }

    public Double getFreshness() {
        return freshness;
    }

    public void setFreshness(Double freshness) {
        this.freshness = freshness;
    }

    public Double getDocumentFreshness() {
        return documentFreshness;
    }

    public void setDocumentFreshness(Double documentFreshness) {
        this.documentFreshness = documentFreshness;
    }

    public Long getDownloadedDocumentsTasksCount() {
        return downloadedDocumentsTasksCount;
    }

    public void setDownloadedDocumentsTasksCount(Long downloadedDocuments) {
        this.downloadedDocumentsTasksCount = downloadedDocuments;
    }

    public Long getDownloadedMetadataTasksCount() {
        return downloadedMetadataTasksCount;
    }

    public void setDownloadedMetadataTasksCount(Long downloadedMetadataTasksCount) {
        this.downloadedMetadataTasksCount = downloadedMetadataTasksCount;
    }

    public Long getExtractedMetadataTasksCount() {
        return extractedMetadataTasksCount;
    }

    public void setExtractedMetadataTasksCount(Long extractedMetadataTasksCount) {
        this.extractedMetadataTasksCount = extractedMetadataTasksCount;
    }
}