package uk.ac.core.database.service.document.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.DocumentUrl;
import uk.ac.core.database.model.mappers.DocumentUrlsMapper;
import uk.ac.core.database.service.document.DocumentUrlDAO;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class MySQLDocumentUrlDAO implements DocumentUrlDAO{

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public List<DocumentUrl> load(Integer repositoryId) {
        StringBuilder sqlSb = new StringBuilder("SELECT id, id_document, url, url_tr, pdf_status, source ");
        sqlSb.append("FROM document_urls ");
        sqlSb.append("WHERE id_document = ?");

        List<Map<String, Object>> documentUrls = this.jdbcTemplate.queryForList(sqlSb.toString(),
                repositoryId);
        return new DocumentUrlsMapper().mapToListOfObjects(documentUrls);
    }

    @Override
    public boolean insertDocumentUrl(DocumentUrl documentUrl) {
        return (this.jdbcTemplate.update(
                "INSERT INTO document_urls (id_document, url, url_tr, pdf_status, source) "
                        + "values (?, ?, ?, ?, ?)",
                documentUrl.getIdDocument(),
                documentUrl.getUrl(),
                documentUrl.getUrl_tr(),
                documentUrl.getPdfStatus(),
                documentUrl.getpDFUrlSource().name()) > 0);
                
    }

    /**
     * This gets a list of URLs and adds/replaces/deletes them the the document_urls
     * table. This also delete any URL in the table that DOES NOT exist in
     * newUrls
     *
     * @param id_document the ID of the document
     * @param newUrls a String array of urls
     */
    @Override
    public void synchroniseUrlsAsString(Integer id_document, Map<String, PDFUrlSource> newUrls) {
        List<DocumentUrl> urls = new ArrayList<>();
        
        newUrls.forEach((url, source)->urls.add(new DocumentUrl(null, id_document, url, 0, source)));
        
        synchroniseUrls(id_document, urls);
    }

    /**
     * This gets a list of URLs and adds/replaces/deletes them the the document_urls
     * table. This also delete any URL in the table that DOES NOT exist in
     * newUrls
     *
     * @param id_document the ID of the document
     * @param newUrls a String array of urls
     */
    public void synchroniseUrls(Integer id_document, List<DocumentUrl> newUrls) {
        List<DocumentUrl> oldDatabaseDocumentUrls = this.load(id_document);

        for (DocumentUrl newUrl : newUrls) {
            // If the new metadata url is NOT in the database, add it
            if (!this.urlExistsInArray(newUrl.getUrl(), oldDatabaseDocumentUrls)) {
                Boolean Success = this.insertDocumentUrl(newUrl);
            }
        }

        // Now iterate over database urls. If they do not exist in the metadata,
        // delete them from the database
        List<DocumentUrl> updatedDatabaseDocumentUrls = this.load(id_document);
        for (DocumentUrl databaseUrl : updatedDatabaseDocumentUrls) {
            // If the old url is NOT in the new metadata,
            if (!this.urlExistsInArray(databaseUrl.getUrl(), newUrls)) {
                this.deleteDocumentUrl(databaseUrl);
            }
        }

    }

    private boolean deleteDocumentUrl(DocumentUrl documentUrl) {
        return (this.jdbcTemplate.update(
                "DELETE FROM document_urls WHERE id = ?",
                documentUrl.getId()) > 0);
    }

    private boolean urlExistsInArray(String urlToCheck, List<DocumentUrl> listOfUrls) {
        for (DocumentUrl url : listOfUrls) {
            if (url.getUrl().equalsIgnoreCase(urlToCheck)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getUrlByDocId(Integer docId){
        String query = "SELECT url FROM document_urls WHERE id_document = ?";
        return jdbcTemplate.queryForList(query, String.class, docId);
    }


}
