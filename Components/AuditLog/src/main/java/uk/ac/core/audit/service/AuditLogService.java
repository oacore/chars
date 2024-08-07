package uk.ac.core.audit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.audit.entity.AuditLog;
import uk.ac.core.audit.entity.Product;
import uk.ac.core.audit.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Save an event
     * @param identifier the unique identifier of the identifierType (e.g. 86 for Open Research Online or '10.34/asdf' doi)
     * @param identifierType the kind of identifier (e.g. repository, doi, article, anything)
     * @param user the user who initiated the request (e.g. depends on the product, could be dashboard user, admin - pls be specific)
     * @param product the kind of product (DASHBOARD, APPLE, MetadataExtract)
     * @param action the kind of audited action (document-enabled, document-disabled, user-added)
     */
    public void save(String identifier, String identifierType, String user, Product product, String action) {
        this.save(new AuditLog(
                identifier,
                identifierType,
                user,
                product,
                action
            )
        );
    }

    public void save(AuditLog log) throws IllegalArgumentException {
        this.auditLogRepository.save(log);
    }
}
