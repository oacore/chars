package uk.ac.core.database.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.database.service.document.impl.MySQLArticleMetadataDAO;


/**
 *
 * @author Drahomira Herrmannova <d.herrmannova@gmail.com>
 */
public class ArticleMetadataMapper implements RowMapper<ArticleMetadata> {

    @Override
    public ArticleMetadata mapRow(ResultSet rs, int i) throws SQLException {

        ArticleMetadata am = new ArticleMetadata();

        am.setId(rs.getInt("id_document"));

        String authorsString = rs.getString("authors");
        if (authorsString != null) {
            List<String> authors = Arrays.asList(
                    authorsString.split(MySQLArticleMetadataDAO.DELIMITER));
            am.setAuthors(authors);
        }

        String contributorsString = rs.getString("contributors");
        if (contributorsString != null) {
            List<String> contributors = Arrays.asList(
                    contributorsString.split(MySQLArticleMetadataDAO.DELIMITER));
            am.setContributors(contributors);
        }

        String identifiersString = rs.getString("identifiers");
        if (identifiersString != null) {
            List<String> identifiers = Arrays.asList(
                    identifiersString.split(MySQLArticleMetadataDAO.DELIMITER));
            for (String identifier : identifiers) {
                if (identifier.startsWith("oai")){
                    am.setOAIIdentifier(identifier);
                    continue;
                }
            }
            if (am.getOAIIdentifier() == null) {
                am.setOAIIdentifier(identifiers.get(0));
            }
            am.setIdentifiers(identifiers);
        }
        String relationsString = rs.getString("relations");
        if (relationsString != null) {
            List<String> relations = Arrays.asList(
                    relationsString.split(MySQLArticleMetadataDAO.DELIMITER));
            am.setRelations(relations);
        }

        String subjectsString = rs.getString("subjects");
        if (subjectsString != null) {
            List<String> subjects = Arrays.asList(
                    subjectsString.split(MySQLArticleMetadataDAO.DELIMITER));
            am.setSubjects(subjects);
        }

        String topicsString = rs.getString("topics");
        if (topicsString != null) {
            List<String> topics = Arrays.asList(
                    topicsString.split(MySQLArticleMetadataDAO.DELIMITER));
            am.setTopics(topics);
        }

        String typesString = rs.getString("types");
        if (typesString != null) {
            List<String> types = Arrays.asList(typesString.split(MySQLArticleMetadataDAO.DELIMITER));
            am.setSubjects(types);
        }

        String journalIdentifiersString = rs.getString("journal_identifier");
        if (journalIdentifiersString != null) {
            List<String> jIdentifiers = Arrays.asList(journalIdentifiersString.split(MySQLArticleMetadataDAO.DELIMITER));
            am.setJournalIdentifiers(jIdentifiers);
            for (String ji : am.getJournalIdentifiers()) {
                String jil = ji.toLowerCase();
                if (jil.startsWith("issn:")) {
                    am.addJournalIssn(jil.replace("issn:", ""));
                }
            }
        }

        String date = rs.getString("date");
        am.setDate(date);

        // careful with util.Date and sql.Date
        java.util.Date datestamp = rs.getTimestamp("datestamp");
        am.setDateStamp(datestamp);
        
        // careful with util.Date and sql.Date
        java.util.Date depositedDatestamp = rs.getTimestamp("deposited_date");
        am.setDepositedDateStamp(depositedDatestamp);

        String title = rs.getString("title");
        am.setTitle(title);

        String description = rs.getString("description");
        am.setDescription(description);

        String doi = rs.getString("doi");
        am.setDoi(doi);

        Boolean doiFromCrossRef = rs.getBoolean("doi_from_crossref");
        am.setDoiFromCrossRef(doiFromCrossRef);

        String doiMetadataTag = rs.getString("doi_metadata_tag");
        am.setDoiMetadataTag(doiMetadataTag);

        java.util.Date resolved = rs.getTimestamp("doi_datetime_resolved");
        am.setDoiDatetimeResolved(resolved);

        String publisher = rs.getString("publisher");
        am.setPublisher(publisher);

        String license = rs.getString("license");
        am.setLicense(license);
        
        return am;
    }

}

