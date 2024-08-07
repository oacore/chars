package uk.ac.core.common.model.legacy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import uk.ac.core.common.model.article.ArticleId;
import uk.ac.core.common.model.article.DOI;

/**
 *
 * @author lucasanastasiou
 */
public class ArticleMetadataBase implements ArticleId, DOI {

    protected Integer id;
    protected String date;
    // do not serialize date stamp as is - causes problem with mappings in ES
//    @Expose(serialize = false)
    protected Date dateStamp = null;
    // serialize the string version instead
//    @SerializedName("date-stamp")
    protected String dateStampString = null;
    protected String title = null;
    protected List<String> authors;
    protected List<String> contributors;
    protected String authorsString;
    protected List<String> repositories;
    protected List<Integer> repositoryIds;
    // the abstract
    protected String description = "";
    
    protected String DOI;

    public ArticleMetadataBase() {
        authors = new ArrayList<String>();
        repositories = new ArrayList<String>();
        repositoryIds = new ArrayList<Integer>();
        contributors = new ArrayList<String>();
    }

    /* ID *****************************************************************************************/

    /**
     *
     * @return
     */    
    @Override
    public Integer getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /* DATE ***************************************************************************************/
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /* DATESTAMP **********************************************************************************/
    
    public Date getDateStamp() {
        return dateStamp;
    }

    public String getDateStampString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String sdate = formatter.format(dateStamp.getTime());
        return sdate;
    }

    public void setDateStamp(Date dateStamp) {
        this.dateStamp = dateStamp;
    }
    
    public Date depositedDateStamp;

    public Date getDepositedDateStamp() {
        return depositedDateStamp;
    }

    public void setDepositedDateStamp(Date depositedDateStamp) {
        this.depositedDateStamp = depositedDateStamp;
    }
    
    /* REPOSITORY *********************************************************************************/
    
    public String getRepository() {
        if (repositories.isEmpty()) {
            return null;
        } else {
            return repositories.get(0);
        }
    }

    public List<String> getRepositories() {
        return repositories;
    }

    public void setRepository(String repository) {
        if (repositories.isEmpty()) {
            repositories.add(repository);
        } else {
            repositories.set(0, repository);
        }
    }

    public void addRepository(String repository) {
        repositories.add(repository);
    }

    public void setRepositoryId(Integer repositoryId) {
        if (repositoryIds.isEmpty()) {
            repositoryIds.add(repositoryId);
        } else {
            repositoryIds.set(0, repositoryId);
        }
    }

    public void addRepositoryId(Integer repositoryId) {
        repositoryIds.add(repositoryId);
    }

    public Integer getRepositoryId() {
        if (repositoryIds.isEmpty()) {
            return null;
        } else {
            return repositoryIds.get(0);
        }
    }

    public List<Integer> getRepositoryIds() {
        return repositoryIds;
    }

    public void setRepositoryIds(List<Integer> ids) {
        this.repositoryIds = ids;
    }

    /* TITLE **************************************************************************************/
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /* AUTHORS ********************************************************************************** */
    
    /**
     * Get document authors as a single string.
     *
     * @return
     */
    public String getAuthorsString() {
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

    public List<String> getAuthors() {
        return authors;
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
    public String formatName(String author) {
//        Debugger.debug("preformatted author:" + author, this.getClass());
        
        // If the author field contains a semi colon, do not modify the string
        if (author.contains(";")) {
            return author;
        }
        if (author.contains(",")) {
            String[] splits = author.split(",");
            StringBuilder sb = new StringBuilder();
            for (int i = splits.length - 1; i >= 0; i--) {
                String split = splits[i].trim();
                sb.append(split);
                sb.append(" ");
            }
            String formattedName = sb.toString();
            formattedName = formattedName.trim();
//            Debugger.trace("post formatted author:" + author, this.getClass());
            return formattedName;
        }
        return author;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
        this.authorsString = this.getAuthorsString();
    }

    public void addAuthor(String author) {
        this.authors.add(author);
        this.authorsString = this.getAuthorsString();
    }

    public void addContributor(String contributor) {
        this.contributors.add(contributor);
    }

    public List<String> getContributors() {
        return this.contributors;
    }

    public void setContributors(List<String> contributors) {
        this.contributors = contributors;
    }


    /* DESCRIPTION ****************************************************************************** */
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void addToDescription(String descriptionPart) {
        this.description += descriptionPart;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String getDoi() {
        return DOI;
    }

    @Override
    public void setDoi(String DOI) {
        this.DOI = DOI;
    }
 
    

}
