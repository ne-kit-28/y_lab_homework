package y_lab.audit_logging_spring_boot_starter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import y_lab.audit_logging_spring_boot_starter.domain.AuditRecord;
import y_lab.audit_logging_spring_boot_starter.repository.AuditRepository;
import y_lab.audit_logging_spring_boot_starter.repository.AuditRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);

    @Autowired
    public AuditServiceImpl (AuditRepositoryImpl auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public void createAudit(AuditRecord auditRecord) {
        try {
            auditRepository.save(auditRecord);
        } catch (SQLException e) {
            logger.info("SQL error in audit create");
        }
    }

    @Override
    public ArrayList<AuditRecord> getAudit(long userId) {
        ArrayList<AuditRecord> auditRecords = new ArrayList<>();

        try {
            auditRecords = auditRepository.getAuditByUserId(userId);
            return auditRecords;
        } catch (SQLException e) {
            logger.info("SQL error in get audit");
            return auditRecords;
        }
    }
}
