/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.document.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import uk.ac.core.database.model.DocumentMetadataExtendedAttributes;
import uk.ac.core.database.service.document.ExtendedAttributesDAO;

/**
 *
 * @author samuel
 */
@Service
public class MySQLExtendedAttributesDAO implements ExtendedAttributesDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MySQLExtendedAttributesDAO.class);

    
    @Override
    public boolean save(int idDocument, LocalDateTime repositoryMetadataPublicReleaseDate, Integer attachmentCount) {
        DocumentMetadataExtendedAttributes documentMetadataExtentedAttributes = new DocumentMetadataExtendedAttributes(idDocument);
        documentMetadataExtentedAttributes.setRepositoryMetadataPublicReleaseDate(repositoryMetadataPublicReleaseDate);
        documentMetadataExtentedAttributes.setAttachmentCount(attachmentCount);
        return this.save(documentMetadataExtentedAttributes);
    }

    @Override
    public boolean save(DocumentMetadataExtendedAttributes documentMetadataExtentedAttributes) {
        Optional<DocumentMetadataExtendedAttributes> attr = this.get(documentMetadataExtentedAttributes.getIdDocument());
        if (attr.isPresent()) {
            String sql = "UPDATE document_metadata_extended_attributes "
                    + "SET "
                    + "repository_metadata_public_release_date=?,attachment_count=? "
                    + "WHERE "
                    + "id_document = ?";
            return jdbcTemplate.update(sql,
                    documentMetadataExtentedAttributes.getRepositoryMetadataPublicReleaseDate(),
                    documentMetadataExtentedAttributes.getAttachmentCount(),
                    documentMetadataExtentedAttributes.getIdDocument()) > 0;
        } else {
            String sql = "INSERT INTO document_metadata_extended_attributes "
                    + "(id_document, repository_metadata_public_release_date, attachment_count) "
                    + "VALUES (?,?,?) ";
            return jdbcTemplate.update(sql,
                    documentMetadataExtentedAttributes.getIdDocument(),
                    documentMetadataExtentedAttributes.getRepositoryMetadataPublicReleaseDate(),
                    documentMetadataExtentedAttributes.getAttachmentCount()
            ) > 0;
        }
    }

    @Override
    public Optional<DocumentMetadataExtendedAttributes> get(int idDocument) {
        final String SQL_FIND_PERSON = "SELECT * FROM document_metadata_extended_attributes WHERE id_document = ?";
        try {
            return Optional.of(this.jdbcTemplate.queryForObject(SQL_FIND_PERSON, new Object[]{idDocument}, (ResultSet rs, int rowNum) -> {
                DocumentMetadataExtendedAttributes attrs = new DocumentMetadataExtendedAttributes(idDocument);
                if (null != rs.getDate("repository_metadata_public_release_date")) {
                    attrs.setRepositoryMetadataPublicReleaseDate(LocalDateTime.from(rs.getTimestamp("repository_metadata_public_release_date").toLocalDateTime()));
                }
                attrs.setAttachmentCount(rs.getInt("attachment_count"));
                return attrs;
            }));
        } catch (EmptyResultDataAccessException ex) {
            // gobble exception
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

}
