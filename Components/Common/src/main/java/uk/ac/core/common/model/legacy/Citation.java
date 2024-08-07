package uk.ac.core.common.model.legacy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import java.util.ArrayList;
import java.util.List;
import uk.ac.core.common.model.article.FormattedName;

/**
 *
 * @author lucasanastasiou
 */
@Deprecated
public class Citation {

    private Integer idCitation;
    private String rawString;
    private List<String> authors;
    private String authorsAsString;
    private String title;
    private String date;
    private String doi;
    /**
     * core id of referenced document.
     */
    private Integer refDocId;
    /**
     * core id of document containing this citation.
     */
    private Integer docId;
    public String citationText;

    public Citation() {
        this.authors = new ArrayList<String>();
    }

    public void setRawString(String rawString) {
        this.rawString = rawString;
    }

    public String getRawString() {
        return this.rawString;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void setAuthors(String authors) {
        this.authorsAsString = authors;
    }

    public List<String> getAuthors() {
        return this.authors;
    }

    public void addAuthor(String author) {
        this.authors.add(author);
    }

    public String getCitationText() {
        return this.constructCitationText();
    }

    public void setCitationText(String citationText) {
        this.citationText = this.constructCitationText();
    }

    /**
     * Get document authors as a single string.
     *
     * @return
     */
    public String getAuthorsString() {
        if (this.authorsAsString != null && !this.authorsAsString.isEmpty()) {
            return this.authorsAsString;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            if (i > 0) {
                if (i == authors.size() - 1) {
                    sb.append(" and ");
                } else {
                    sb.append(", ");
                }
            }
            sb.append(formatName(authors.get(i)));
        }
        return sb.toString();
    }

    /**
     * Method will format authors name.
     *
     * In case the name is written as "surname, name", method will return "name
     * surname".
     *
     * @param author
     * @return
     */
    private String formatName(String author) {
        return new FormattedName(author).toString();
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    public Integer getRefDocId() {
        return refDocId;
    }

    public void setRefDocId(Integer refdocId) {
        this.refDocId = refdocId;
    }

    @Override
    public String toString() {
        String citString = this.title;
        String auth = this.getAuthorsString();
        if (auth != null && !auth.isEmpty()) {
            citString += " - " + this.getAuthorsString();
        }
        if (this.date != null && !this.date.isEmpty()) {
            citString += " - " + this.date;
        }
        return citString;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String constructCitationText() {
        StringBuilder sb = new StringBuilder();
        String auth = this.getAuthorsString();
        if (auth != null && !auth.isEmpty()) {
            sb.append(auth);
            sb.append(". ");
        }
        if (this.date != null && !this.date.isEmpty()) {
            sb.append("(");
            sb.append(this.date);
            sb.append("). ");
        }
        sb.append(this.title);

        return sb.toString();
    }

    /**
     * Format citation for indexing.
     *
     * @return
     */
    public String toIndexedString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.constructCitationText());

        if (this.doi != null) {
            sb.append(" ##doi:");
            sb.append(this.doi);
            sb.append("##");
        }

        if (this.refDocId != null) {
            sb.append(" [");
            sb.append(this.refDocId.toString());
            sb.append("]");
        }
        return sb.toString();
    }

    public Integer getIdCitation() {
        return idCitation;
    }

    public void setIdCitation(Integer idCitation) {
        this.idCitation = idCitation;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.authorsAsString != null ? this.authorsAsString.hashCode() : 0);
        hash = 59 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 59 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 59 * hash + (this.doi != null ? this.doi.hashCode() : 0);
        hash = 59 * hash + (this.refDocId != null ? this.refDocId.hashCode() : 0);
        hash = 59 * hash + (this.docId != null ? this.docId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Citation other = (Citation) obj;
        if ((this.authorsAsString == null) ? (other.authorsAsString != null) : !this.authorsAsString.equals(other.authorsAsString)) {
            return false;
        }
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.date == null) ? (other.date != null) : !this.date.equals(other.date)) {
            return false;
        }
        if ((this.doi == null) ? (other.doi != null) : !this.doi.equals(other.doi)) {
            return false;
        }
        if (this.refDocId != other.refDocId && (this.refDocId == null || !this.refDocId.equals(other.refDocId))) {
            return false;
        }
        if (this.docId != other.docId && (this.docId == null || !this.docId.equals(other.docId))) {
            return false;
        }
        return true;
    }

    /**
     * CitationText field shall not be indexed, only used by the view
     */
    public static class CitationExclStrat implements ExclusionStrategy {

        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes fa) {
            return (fa.getName().equals("citationText"));
        }
    }

    public static void main(String[] args) {

        Citation c0 = new Citation();
        Citation c1 = new Citation();
        Citation c2 = new Citation();

        c0.setTitle("Long Title1");
        c0.addAuthor("John Smith1");
        c0.addAuthor("John Doe1");
        c0.setDate("2011");
        c0.setDocId(1);
        c0.setRefDocId(1);

        c1.setTitle("Long Title2");
        c1.addAuthor("John Smith2");
        c1.addAuthor("John Doe2");
        c1.setDate("2011");
        c1.setDocId(2);
        c1.setRefDocId(2);

        c2.setTitle("Long Title3 ");
        c2.setAuthors("authors");
        c2.setDate("2011");
        c2.setDocId(3);
        c2.setRefDocId(4);

        List<Citation> list = new ArrayList<Citation>();

        list.add(c0);
        list.add(c1);
        list.add(c2);

        Citation needle = new Citation();
        needle.setTitle("Long Title3 ");
        needle.setAuthors("authors");
        needle.setDate("2011");
        needle.setDocId(3);
        needle.setRefDocId(4);

        boolean isIn = list.contains(needle);

        System.out.println("is it in ? " + isIn);
    }
}
