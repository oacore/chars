/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.workers.item.doiresolutionworker.crossref;

import java.util.List;
import uk.ac.core.common.model.article.DOI;
import uk.ac.core.common.model.legacy.ArticleMetadataBase;

/**
 *
 * @author samuel
 */
public final class CrossrefCitation implements DOI, Citation {

    private Integer id;
    private List<String> authors;
    private String title;
    private String date;
    
    private String doi;

    public CrossrefCitation(Integer id, List<String> authors, String title, String date) {
        this.id = id;
        this.authors = authors;
        this.title = title;
        this.date = date;        
    }
    
    public CrossrefCitation(final ArticleMetadataBase am) {
        this(am.getId(), am.getAuthors(), am.getTitle(), am.getDate());
    }
    
    @Override
    public String getCitation() {
        StringBuilder sb = new StringBuilder();
        for (String author : this.authors) {
            sb.append(author)
                    .append(" ");
        }
        sb.append(this.title)
                .append(" ");

        if (this.date != null && !this.date.isEmpty()) {
            sb.append("(");
            sb.append(this.date);
            sb.append("). ");
        }
        return sb.toString();
    }

    @Override
    public String toString() {        
        return "CrossrefCitation: " + this.getCitation();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getDoi() {
        return doi;
    }

    @Override
    public void setDoi(String doi) {
        this.doi = doi;
    }

    
    
}
