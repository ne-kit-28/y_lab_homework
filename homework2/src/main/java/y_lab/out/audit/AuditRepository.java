package y_lab.out.audit;

import java.sql.SQLException;
import java.util.ArrayList;

public interface AuditRepository {
    void save(AuditRecord auditRecord) throws SQLException;

    ArrayList<AuditRecord> getAuditByUserId(long userId) throws SQLException;
}
