/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.dataprovider.logic.entity;

import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.*;

/**
 * The indexed version of Repositories.
 * <p>
 * We want to reuse Repository but we also want to index the location in the
 * same Json array so we have to create a new Object.
 *
 * @author samuel
 */
@Document(indexName = "repositories", type = "repository")
@Setting(settingPath = "/elasticsearch/mappings/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings/repository.json")
public class IndexedDataProvider extends DataProvider {

    private DataProviderLocation dataProviderLocation;

    @Field(type = FieldType.Boolean)
    private Boolean disabled = false;

    @Field(type = FieldType.Text)
    private String email;

    @Field(type = FieldType.Text)
    private String rorId;

    @Field(type = FieldType.Text)
    private String institutionName;


    @Field(type = FieldType.Auto)
    private Map otherIdentifiers;
    @Field(type = FieldType.Text)
    private List<String> aliases;


    public IndexedDataProvider() {
    }


    public IndexedDataProvider(DataProvider rep, DataProviderLocation rl, DashboardRepo dashboardRepo, RorData rorData) {
        this();
        this.setId(rep.getId());
        this.setOpenDoarId(rep.getOpenDoarId());
        this.setName(rep.getName());
        this.setUri(rep.getUri());
        this.setRorId(rorData.getRorId());
        this.setInstitutionName(rorData.getInstitutionName());
        List<String> aliases = new ArrayList<>();
        if (rorData.getAliases() != null) {
            aliases.addAll(Arrays.asList((rorData.getAliases().split(";"))));
        }
        if (rorData.getLabels() != null) {
            aliases.addAll(Arrays.asList((rorData.getLabels().split(";"))));
        }
        if (rorData.getAcronyms() != null) {
            aliases.addAll(Arrays.asList((rorData.getAcronyms().split(";"))));
        }
        if (rorData.getExternalIds() != null) {
            Map identifiers = new Gson().fromJson(rorData.getExternalIds(), Map.class);
            this.setOtherIdentifiers(identifiers);
        }

        this.setAliases(aliases);


        this.setUrlOaipmh(rep.getUrlOaipmh());
        this.setUrlHomepage(rep.getUrlHomepage());
        this.setSource(rep.getSource());
        this.setSoftware(rep.getSoftware());
        this.setMetadataFormat(rep.getMetadataFormat());
        this.setDescription(rep.getDescription());
        this.setJournal(rep.isJournal());
        this.setCreated_date(rep.getCreated_date());
        this.setDisabled(rep.getDisabled());
        this.setDataProviderLocation(rl);
        this.setEmail(dashboardRepo.getEmail());
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public DataProviderLocation getDataProviderLocation() {
        return dataProviderLocation;
    }

    public void setDataProviderLocation(DataProviderLocation dataProviderLocation) {
        this.dataProviderLocation = dataProviderLocation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRorId(String rorId) {
        this.rorId = rorId;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public void setOtherIdentifiers(Map otherIdentifiers) {
        this.otherIdentifiers = otherIdentifiers;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public String getRorId() {
        return rorId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public Map getOtherIdentifiers() {
        return otherIdentifiers;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
