package uk.ac.core.database.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.core.database.model.DocumentMetadataExtendedAttributes;

/**
 * Repository for Document Metadata Extended Attributes
 */
//@Repository
public interface DocumentMetadataExtendedAttributesRepository extends JpaRepository<DocumentMetadataExtendedAttributes, Integer> {

}