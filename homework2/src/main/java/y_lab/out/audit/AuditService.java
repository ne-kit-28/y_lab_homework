package y_lab.out.audit;

import java.util.ArrayList;

public interface AuditService {

    void createAudit (AuditRecord auditRecord);

    ArrayList<AuditRecord> getAudit(long userId);
}
