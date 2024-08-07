/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.dataprovider.logic.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author samuel
 */
@Entity
@Table(name = "repository_harvest_properties")
public class RepositoryHarvestProperties {

    @javax.persistence.Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_repository")
    private Long id;

    private int harvest_level, try_only_pdf, same_domain_policy, algorithm_type, skip_already_downloaded, prioritise_old_documents_for_download;
    private boolean disabled;

    private String accepted_content_types, black_list, harvest_heuristic;
    @Column(name = "license_strategy")
    private int licenseStrategy;

    public RepositoryHarvestProperties() {
    }

    public RepositoryHarvestProperties(Long id) {
        this.id = id;
        this.harvest_level = 1;
        this.try_only_pdf = 1;
        this.disabled = true;
        this.same_domain_policy = 1;
        this.algorithm_type = 1;
        this.skip_already_downloaded = 1;
        this.prioritise_old_documents_for_download = 1;
        this.licenseStrategy = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getHarvest_level() {
        return harvest_level;
    }

    public void setHarvest_level(int harvest_level) {
        this.harvest_level = harvest_level;
    }

    public int getTry_only_pdf() {
        return try_only_pdf;
    }

    public void setTry_only_pdf(int try_only_pdf) {
        this.try_only_pdf = try_only_pdf;
    }

    public int getSame_domain_policy() {
        return same_domain_policy;
    }

    public void setSame_domain_policy(int same_domain_policy) {
        this.same_domain_policy = same_domain_policy;
    }

    public int getAlgorithm_type() {
        return algorithm_type;
    }

    public void setAlgorithm_type(int algorithm_type) {
        this.algorithm_type = algorithm_type;
    }

    public int getSkip_already_downloaded() {
        return skip_already_downloaded;
    }

    public void setSkip_already_downloaded(int skip_already_downloaded) {
        this.skip_already_downloaded = skip_already_downloaded;
    }

    public int getPrioritise_old_documents_for_download() {
        return prioritise_old_documents_for_download;
    }

    public void setPrioritise_old_documents_for_download(int prioritise_old_documents_for_download) {
        this.prioritise_old_documents_for_download = prioritise_old_documents_for_download;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getAccepted_content_types() {
        return accepted_content_types;
    }

    public void setAccepted_content_types(String accepted_content_types) {
        this.accepted_content_types = accepted_content_types;
    }

    public String getBlack_list() {
        return black_list;
    }

    public void setBlack_list(String black_list) {
        this.black_list = black_list;
    }

    public String getHarvest_heuristic() {
        return harvest_heuristic;
    }

    public void setHarvest_heuristic(String harvest_heuristic) {
        this.harvest_heuristic = harvest_heuristic;
    }

    public int getLicenseStrategy() {
        return licenseStrategy;
    }

    public void setLicenseStrategy(int licenseStrategy) {
        this.licenseStrategy = licenseStrategy;
    }
}
