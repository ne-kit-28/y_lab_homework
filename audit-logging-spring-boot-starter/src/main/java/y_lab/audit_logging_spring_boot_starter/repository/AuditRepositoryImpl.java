package y_lab.audit_logging_spring_boot_starter.repository;

import y_lab.audit_logging_spring_boot_starter.domain.AuditRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Repository
public class AuditRepositoryImpl implements AuditRepository {

    private final DataSource dataSource;
    @Autowired
    public AuditRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(AuditRecord auditRecord) throws SQLException {
        String sql = SqlScripts.AUDIT_SAVE;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, auditRecord.userId());
            stmt.setString(2, auditRecord.date().toString());
            stmt.setString(3, auditRecord.message());

            stmt.executeUpdate();
        }
    }

    @Override
    @Query
    public ArrayList<AuditRecord> getAuditByUserId(long userId) throws SQLException{
        ArrayList<AuditRecord> auditRecords = new ArrayList<>();

        String sql = SqlScripts.AUDIT_FIND_AUDIT_BY_USER_ID;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                AuditRecord auditRecord = new AuditRecord(
                        resultSet.getLong(2)
                , LocalDateTime.parse(resultSet.getString(3))
                , resultSet.getString(4));
                auditRecords.add(auditRecord);
            }
        }
        return auditRecords;
    }
}
