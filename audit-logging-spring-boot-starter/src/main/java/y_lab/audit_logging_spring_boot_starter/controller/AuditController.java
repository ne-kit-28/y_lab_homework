package y_lab.audit_logging_spring_boot_starter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import y_lab.audit_logging_spring_boot_starter.domain.AuditRecord;
import y_lab.audit_logging_spring_boot_starter.service.AuditService;

import java.util.ArrayList;

@RestController
@RequestMapping("admin/user")
public class AuditController {

    private final AuditService auditService;

    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping(value = "/{userId}/audit")
    public ResponseEntity<ArrayList<AuditRecord>> getAudit(@PathVariable("userId") long userId) {
        ArrayList<AuditRecord> auditRecord = auditService.getAudit(userId);
        if (!auditRecord.isEmpty())
            return ResponseEntity.ok(auditRecord);
        else
            return ResponseEntity.notFound().build();
    }
}
