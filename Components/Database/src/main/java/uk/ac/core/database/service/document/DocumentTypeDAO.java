package uk.ac.core.database.service.document;

import org.springframework.data.util.Pair;

/**
 *
 * @author aristotelischaralampous
 */
public interface DocumentTypeDAO {

	public String getDocumentType(Integer documentId);
	public Double getDocumentTypeConfidence(Integer documentId);
	Pair<String, Double> getDocumentTypeInfo(Integer documentId);
}
