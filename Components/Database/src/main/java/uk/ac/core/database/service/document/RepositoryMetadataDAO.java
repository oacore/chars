package uk.ac.core.database.service.document;

/**
 * @author Giorgio Basile
 * @since 05/04/2017
 */
public interface RepositoryMetadataDAO {


    Integer getIdDocumentByOai(final String oai, final Integer repositoryId);

    Integer getIdDocumentByUrl(final String url);

    void deleteDocument(Integer docId);
}
