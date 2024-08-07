
package uk.ac.core.oadiscover.services.epmc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class Journal {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("medlineAbbreviation")
    @Expose
    private String medlineAbbreviation;
    @SerializedName("isoabbreviation")
    @Expose
    private String isoabbreviation;
    @SerializedName("nlmid")
    @Expose
    private String nlmid;
    @SerializedName("essn")
    @Expose
    private String essn;
    @SerializedName("issn")
    @Expose
    private String issn;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMedlineAbbreviation() {
        return medlineAbbreviation;
    }

    public void setMedlineAbbreviation(String medlineAbbreviation) {
        this.medlineAbbreviation = medlineAbbreviation;
    }

    public String getIsoabbreviation() {
        return isoabbreviation;
    }

    public void setIsoabbreviation(String isoabbreviation) {
        this.isoabbreviation = isoabbreviation;
    }

    public String getNlmid() {
        return nlmid;
    }

    public void setNlmid(String nlmid) {
        this.nlmid = nlmid;
    }

    public String getEssn() {
        return essn;
    }

    public void setEssn(String essn) {
        this.essn = essn;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Journal journal = (Journal) o;

        if (!Objects.equals(title, journal.title)) return false;
        if (!Objects.equals(medlineAbbreviation, journal.medlineAbbreviation))
            return false;
        if (!Objects.equals(isoabbreviation, journal.isoabbreviation))
            return false;
        if (!Objects.equals(nlmid, journal.nlmid)) return false;
        if (!Objects.equals(essn, journal.essn)) return false;
        return Objects.equals(issn, journal.issn);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (medlineAbbreviation != null ? medlineAbbreviation.hashCode() : 0);
        result = 31 * result + (isoabbreviation != null ? isoabbreviation.hashCode() : 0);
        result = 31 * result + (nlmid != null ? nlmid.hashCode() : 0);
        result = 31 * result + (essn != null ? essn.hashCode() : 0);
        result = 31 * result + (issn != null ? issn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Journal{" +
                "title='" + title + '\'' +
                ", medlineAbbreviation='" + medlineAbbreviation + '\'' +
                ", isoabbreviation='" + isoabbreviation + '\'' +
                ", nlmid='" + nlmid + '\'' +
                ", essn='" + essn + '\'' +
                ", issn='" + issn + '\'' +
                '}';
    }
}
