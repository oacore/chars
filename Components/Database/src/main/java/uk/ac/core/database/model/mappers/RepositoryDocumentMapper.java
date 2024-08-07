package uk.ac.core.database.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.legacy.RepositoryDocument;

/**
 *
 * @author lucasanastasiou
 */
public final class RepositoryDocumentMapper implements RowMapper<RepositoryDocument> {

        @Override
        public RepositoryDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
            RepositoryDocument repositoryDocument = new RepositoryDocument();
            repositoryDocument.setIdDocument(rs.getInt("id_document"));
            repositoryDocument.setIdUpdate(rs.getInt("id_update"));
            repositoryDocument.setOai(rs.getString("oai"));
            repositoryDocument.setDocClass(rs.getString("doc_class"));
            repositoryDocument.setIdRepository(rs.getInt("id_repository"));
            repositoryDocument.setMetadataAdded(rs.getString("metadata_added"));
            repositoryDocument.setMetadataUpdated(rs.getString("metadata_updated"));

            repositoryDocument.setPdfStatus(rs.getInt("pdf_status"));
            repositoryDocument.setPdfFirstAttempt(rs.getString("pdf_first_attempt"));
            repositoryDocument.setPdfFirstAttemptSuccessful(rs.getString("pdf_first_attempt_successful"));
            repositoryDocument.setPdfLastAttempt(rs.getString("pdf_last_attempt"));
            repositoryDocument.setPdfLastAttemptSuccessful(rs.getString("pdf_last_attempt_successful"));

            repositoryDocument.setPreviewStatus(rs.getInt("preview_status"));
            
            repositoryDocument.setTextStatus(rs.getInt("text_status"));
            repositoryDocument.setTextFirstAttempt(rs.getString("text_first_attempt"));
            repositoryDocument.setTextFirstAttemptSuccessful(rs.getString("text_first_attempt_successful"));
            repositoryDocument.setTextLastAttempt(rs.getString("text_last_attempt"));
            repositoryDocument.setTextLastAttemptSuccessful(rs.getString("text_last_attempt_successful"));

            repositoryDocument.setIndexed(rs.getInt("indexed"));
            repositoryDocument.setIndexFirstAttempt(rs.getString("index_first_attempt"));
            repositoryDocument.setIndexFirstAttemptSuccessful(rs.getString("index_first_attempt_successful"));
            repositoryDocument.setIndexLastAttempt(rs.getString("index_last_attempt"));
            repositoryDocument.setIndexLastAttemptSuccessful(rs.getString("index_last_attempt_successful"));
            repositoryDocument.setReindex(rs.getInt("reindex"));

            repositoryDocument.setDateTimeSimilarityCalculated(rs.getString("date_time_similarity_calculated"));
            repositoryDocument.setDateTimeStamp(rs.getString("date_time_stamp"));
            repositoryDocument.setDeletedStatus(rs.getInt("deleted"));
            repositoryDocument.setPdfUrl(rs.getString("url"));
            repositoryDocument.setFullTextSource(rs.getString("full_text_source"));
            return repositoryDocument;
        }
    }
