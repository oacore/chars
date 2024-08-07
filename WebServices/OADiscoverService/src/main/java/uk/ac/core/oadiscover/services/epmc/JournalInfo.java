
package uk.ac.core.oadiscover.services.epmc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class JournalInfo {

    @SerializedName("volume")
    @Expose
    private String volume;
    @SerializedName("journalIssueId")
    @Expose
    private Long journalIssueId;
    @SerializedName("dateOfPublication")
    @Expose
    private String dateOfPublication;
    @SerializedName("monthOfPublication")
    @Expose
    private Long monthOfPublication;
    @SerializedName("yearOfPublication")
    @Expose
    private Long yearOfPublication;
    @SerializedName("printPublicationDate")
    @Expose
    private String printPublicationDate;
    @SerializedName("journal")
    @Expose
    private Journal journal;

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public Long getJournalIssueId() {
        return journalIssueId;
    }

    public void setJournalIssueId(Long journalIssueId) {
        this.journalIssueId = journalIssueId;
    }

    public String getDateOfPublication() {
        return dateOfPublication;
    }

    public void setDateOfPublication(String dateOfPublication) {
        this.dateOfPublication = dateOfPublication;
    }

    public Long getMonthOfPublication() {
        return monthOfPublication;
    }

    public void setMonthOfPublication(Long monthOfPublication) {
        this.monthOfPublication = monthOfPublication;
    }

    public Long getYearOfPublication() {
        return yearOfPublication;
    }

    public void setYearOfPublication(Long yearOfPublication) {
        this.yearOfPublication = yearOfPublication;
    }

    public String getPrintPublicationDate() {
        return printPublicationDate;
    }

    public void setPrintPublicationDate(String printPublicationDate) {
        this.printPublicationDate = printPublicationDate;
    }

    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JournalInfo that = (JournalInfo) o;

        if (!Objects.equals(volume, that.volume)) return false;
        if (!Objects.equals(journalIssueId, that.journalIssueId))
            return false;
        if (!Objects.equals(dateOfPublication, that.dateOfPublication))
            return false;
        if (!Objects.equals(monthOfPublication, that.monthOfPublication))
            return false;
        if (!Objects.equals(yearOfPublication, that.yearOfPublication))
            return false;
        if (!Objects.equals(printPublicationDate, that.printPublicationDate))
            return false;
        return Objects.equals(journal, that.journal);
    }

    @Override
    public int hashCode() {
        int result = volume != null ? volume.hashCode() : 0;
        result = 31 * result + (journalIssueId != null ? journalIssueId.hashCode() : 0);
        result = 31 * result + (dateOfPublication != null ? dateOfPublication.hashCode() : 0);
        result = 31 * result + (monthOfPublication != null ? monthOfPublication.hashCode() : 0);
        result = 31 * result + (yearOfPublication != null ? yearOfPublication.hashCode() : 0);
        result = 31 * result + (printPublicationDate != null ? printPublicationDate.hashCode() : 0);
        result = 31 * result + (journal != null ? journal.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JournalInfo{" +
                "volume='" + volume + '\'' +
                ", journalIssueId=" + journalIssueId +
                ", dateOfPublication='" + dateOfPublication + '\'' +
                ", monthOfPublication=" + monthOfPublication +
                ", yearOfPublication=" + yearOfPublication +
                ", printPublicationDate='" + printPublicationDate + '\'' +
                ", journal=" + journal +
                '}';
    }
}
