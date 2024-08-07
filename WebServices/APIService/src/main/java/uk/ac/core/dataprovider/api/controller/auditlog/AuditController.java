package uk.ac.core.dataprovider.api.controller.auditlog;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.audit.entity.AuditLog;
import uk.ac.core.audit.service.AuditLogService;

import javax.validation.Valid;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditLogService auditLogService;
    private static final Logger LOG = LoggerFactory.getLogger(AuditLogService.class);

    @Autowired
    public AuditController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @PostMapping
    public ResponseEntity<Void> addAuditLog(@RequestBody @Valid AuditLog audit) {
        try {
            auditLogService.save(audit);
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.noContent().build();
    }
}
