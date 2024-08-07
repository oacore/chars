package uk.ac.core.datawarehouse.report.worker.model;


/**
 *
 * @author lucas
 */
public class ReportRepository {

    Integer CORE_ID;
    Integer Jisc_ID;
    Integer OpenDOAR_ID;
    String Name;
    String Institution;
    String Country;
    Boolean rioxx_enabled;
    Integer count_metadata;
    Integer count_fulltext;
    Boolean repository_dahboard_registered;
    Boolean recommender_registered;
    String repository_core_inclusion_date;

    public Integer getCORE_ID() {
        return CORE_ID;
    }

    public void setCORE_ID(Integer CORE_ID) {
        this.CORE_ID = CORE_ID;
    }

    public Integer getJisc_ID() {
        return Jisc_ID;
    }

    public void setJisc_ID(Integer jisc_ID) {
        Jisc_ID = jisc_ID;
    }

    public Integer getOpenDOAR_ID() {
        return OpenDOAR_ID;
    }

    public void setOpenDOAR_ID(Integer openDOAR_ID) {
        OpenDOAR_ID = openDOAR_ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getInstitution() {
        return Institution;
    }

    public void setInstitution(String institution) {
        Institution = institution;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public Boolean getRioxx_enabled() {
        return rioxx_enabled;
    }

    public void setRioxx_enabled(Boolean rioxx_enabled) {
        this.rioxx_enabled = rioxx_enabled;
    }

    public Integer getCount_metadata() {
        return count_metadata;
    }

    public void setCount_metadata(Integer count_metadata) {
        this.count_metadata = count_metadata;
    }

    public Integer getCount_fulltext() {
        return count_fulltext;
    }

    public void setCount_fulltext(Integer count_fulltext) {
        this.count_fulltext = count_fulltext;
    }

    public Boolean getRepository_dahboard_registered() {
        return repository_dahboard_registered;
    }

    public void setRepository_dahboard_registered(Boolean repository_dahboard_registered) {
        this.repository_dahboard_registered = repository_dahboard_registered;
    }

    public Boolean getRecommender_registered() {
        return recommender_registered;
    }

    public void setRecommender_registered(Boolean recommender_registered) {
        this.recommender_registered = recommender_registered;
    }

    public String getRepository_core_inclusion_date() {
        return repository_core_inclusion_date;
    }

    public void setRepository_core_inclusion_date(String repository_core_inclusion_date) {
        this.repository_core_inclusion_date = repository_core_inclusion_date;
    }
}
